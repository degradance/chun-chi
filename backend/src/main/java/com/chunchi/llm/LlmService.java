package com.chunchi.llm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Higher-level LLM service used by agents and the REST API.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LlmService {

    private final OllamaClient ollamaClient;
    private final OllamaProperties props;

    /**
     * Single-turn generation with the specified model.
     */
    public String generate(String model, String prompt) {
        String effectiveModel = (model != null && !model.isBlank()) ? model : props.getModel();
        log.debug("LLM generate | model={}", effectiveModel);
        return ollamaClient.generate(effectiveModel, prompt);
    }

    /**
     * Chat-style call (system + user message) with the specified model.
     */
    public String chat(String model, String prompt) {
        return chat(model, null, prompt);
    }

    /**
     * Chat-style call with an optional system message.
     */
    public String chat(String model, String systemMessage, String userMessage) {
        String effectiveModel = (model != null && !model.isBlank()) ? model : props.getModel();
        log.debug("LLM chat | model={}", effectiveModel);

        List<OllamaApi.Message> messages;
        if (systemMessage != null && !systemMessage.isBlank()) {
            messages = List.of(
                    new OllamaApi.Message("system", systemMessage),
                    new OllamaApi.Message("user", userMessage)
            );
        } else {
            messages = List.of(new OllamaApi.Message("user", userMessage));
        }

        return ollamaClient.chat(effectiveModel, messages);
    }

    /**
     * Returns the list of locally available Ollama models.
     */
    public List<OllamaApi.ModelInfo> listAvailableModels() {
        OllamaApi.TagsResponse tags = ollamaClient.listModels();
        return tags != null ? tags.models() : List.of();
    }
}
