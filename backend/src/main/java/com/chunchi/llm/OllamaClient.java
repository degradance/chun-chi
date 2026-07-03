package com.chunchi.llm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

/**
 * Low-level HTTP client wrapping the Ollama REST API.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OllamaClient {

    private final WebClient ollamaWebClient;
    private final OllamaProperties props;

    /**
     * Calls /api/generate (single-turn, non-streaming).
     */
    public String generate(String model, String prompt) {
        OllamaApi.GenerateRequest body = new OllamaApi.GenerateRequest(model, prompt, false);
        OllamaApi.GenerateResponse response = ollamaWebClient.post()
                .uri("/api/generate")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(OllamaApi.GenerateResponse.class)
                .timeout(Duration.ofSeconds(props.getTimeoutSeconds()))
                .block();

        if (response == null) {
            throw new LlmException("Empty response from Ollama /api/generate");
        }
        return response.response();
    }

    /**
     * Calls /api/chat (multi-turn, non-streaming).
     */
    public String chat(String model, List<OllamaApi.Message> messages) {
        OllamaApi.ChatRequest body = new OllamaApi.ChatRequest(model, messages, false);
        OllamaApi.ChatResponse response = ollamaWebClient.post()
                .uri("/api/chat")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(OllamaApi.ChatResponse.class)
                .timeout(Duration.ofSeconds(props.getTimeoutSeconds()))
                .block();

        if (response == null || response.message() == null) {
            throw new LlmException("Empty response from Ollama /api/chat");
        }
        return response.message().content();
    }

    /**
     * Lists locally available models via /api/tags.
     */
    public OllamaApi.TagsResponse listModels() {
        return ollamaWebClient.get()
                .uri("/api/tags")
                .retrieve()
                .bodyToMono(OllamaApi.TagsResponse.class)
                .timeout(Duration.ofSeconds(10))
                .onErrorResume(e -> {
                    log.warn("Could not reach Ollama to list models: {}", e.getMessage());
                    return Mono.just(new OllamaApi.TagsResponse(List.of()));
                })
                .block();
    }
}
