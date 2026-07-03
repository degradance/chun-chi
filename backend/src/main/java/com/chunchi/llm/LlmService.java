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

    private final LlmClient llmClient;

    /**
     * Single-turn generation with the specified model.
     */
    public String generate(String model, String prompt) {
        String effectiveModel = (model != null && !model.isBlank()) ? model : llmClient.defaultModel();
        log.debug("LLM generate | model={}", effectiveModel);
        return llmClient.generate(effectiveModel, prompt);
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
        String effectiveModel = (model != null && !model.isBlank()) ? model : llmClient.defaultModel();
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

        return llmClient.chat(effectiveModel, messages);
    }

    /**
     * Returns the list of models available on the active LLM backend.
     */
    public List<OllamaApi.ModelInfo> listAvailableModels() {
        return llmClient.listModels();
    }
}
