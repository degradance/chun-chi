package com.chunchi.llm;

import java.util.List;

/**
 * Abstraction over any LLM backend (Ollama, LM Studio, etc.).
 */
public interface LlmClient {

    /**
     * Single-turn generation.
     */
    String generate(String model, String prompt);

    /**
     * Chat-style call with a list of messages.
     */
    String chat(String model, List<OllamaApi.Message> messages);

    /**
     * Lists models available on the backend.
     */
    List<OllamaApi.ModelInfo> listModels();

    /**
     * Returns the configured default model name.
     */
    String defaultModel();
}
