package com.chunchi.llm;

import java.util.List;

/**
 * Request / response records for the Ollama HTTP API.
 * See: https://github.com/ollama/ollama/blob/main/docs/api.md
 */
public final class OllamaApi {

    private OllamaApi() {}

    // ── /api/generate ───────────────────────────────────────────────────────

    public record GenerateRequest(
            String model,
            String prompt,
            boolean stream
    ) {}

    public record GenerateResponse(
            String model,
            String response,
            boolean done,
            Long totalDuration,
            Integer promptEvalCount,
            Integer evalCount
    ) {}

    // ── /api/chat ───────────────────────────────────────────────────────────

    public record ChatRequest(
            String model,
            List<Message> messages,
            boolean stream
    ) {}

    public record Message(
            String role,
            String content
    ) {}

    public record ChatResponse(
            String model,
            Message message,
            boolean done,
            Long totalDuration
    ) {}

    // ── /api/tags ───────────────────────────────────────────────────────────

    public record TagsResponse(
            List<ModelInfo> models
    ) {}

    public record ModelInfo(
            String name,
            String modifiedAt,
            Long size
    ) {}
}
