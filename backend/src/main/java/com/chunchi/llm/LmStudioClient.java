package com.chunchi.llm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

/**
 * LlmClient implementation that wraps the LM Studio OpenAI-compatible REST API.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "llm.provider", havingValue = "lmstudio")
public class LmStudioClient implements LlmClient {

    private final WebClient lmStudioWebClient;
    private final LmStudioProperties props;

    /**
     * Wraps the prompt as a user message and calls /v1/chat/completions.
     */
    @Override
    public String generate(String model, String prompt) {
        return chat(model, List.of(new OllamaApi.Message("user", prompt)));
    }

    /**
     * Calls /v1/chat/completions (non-streaming).
     */
    @Override
    public String chat(String model, List<OllamaApi.Message> messages) {
        List<LmStudioApi.Message> lmMessages = messages.stream()
                .map(m -> new LmStudioApi.Message(m.role(), m.content()))
                .toList();

        LmStudioApi.ChatRequest body = new LmStudioApi.ChatRequest(model, lmMessages, false);
        LmStudioApi.ChatResponse response = lmStudioWebClient.post()
                .uri("/v1/chat/completions")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(LmStudioApi.ChatResponse.class)
                .timeout(Duration.ofSeconds(props.getTimeoutSeconds()))
                .block();

        if (response == null || response.choices() == null || response.choices().isEmpty()) {
            throw new LlmException("Empty response from LM Studio /v1/chat/completions");
        }
        LmStudioApi.Message msg = response.choices().get(0).message();
        if (msg == null) {
            throw new LlmException("No message in LM Studio response choice");
        }
        return msg.content();
    }

    /**
     * Lists available models via /v1/models, adapting them to OllamaApi.ModelInfo.
     */
    @Override
    public List<OllamaApi.ModelInfo> listModels() {
        LmStudioApi.ModelsResponse resp = lmStudioWebClient.get()
                .uri("/v1/models")
                .retrieve()
                .bodyToMono(LmStudioApi.ModelsResponse.class)
                .timeout(Duration.ofSeconds(10))
                .onErrorResume(e -> {
                    log.warn("Could not reach LM Studio to list models: {}", e.getMessage());
                    return Mono.just(new LmStudioApi.ModelsResponse(List.of()));
                })
                .block();

        if (resp == null || resp.data() == null) {
            return List.of();
        }
        return resp.data().stream()
                .map(m -> new OllamaApi.ModelInfo(m.id(), null, null))
                .toList();
    }

    @Override
    public String defaultModel() {
        return props.getModel();
    }
}
