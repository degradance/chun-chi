package com.chunchi.llm;

import java.util.List;

/**
 * Request / response records for the LM Studio OpenAI-compatible HTTP API.
 * See: https://lmstudio.ai/docs/api
 */
public final class LmStudioApi {

    private LmStudioApi() {}

    // ── /v1/chat/completions ─────────────────────────────────────────────────

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
            String id,
            List<Choice> choices
    ) {}

    public record Choice(
            Message message
    ) {}

    // ── /v1/models ───────────────────────────────────────────────────────────

    public record ModelsResponse(
            List<ModelData> data
    ) {}

    public record ModelData(
            String id,
            String object
    ) {}
}
