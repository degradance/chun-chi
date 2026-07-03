package com.chunchi.llm;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for LLM model information and direct chat access.
 */
@RestController
@RequestMapping("/api/llm")
@RequiredArgsConstructor
public class LlmController {

    private final LlmService llmService;

    @GetMapping("/models")
    public List<OllamaApi.ModelInfo> listModels() {
        return llmService.listAvailableModels();
    }

    @PostMapping("/chat")
    @ResponseStatus(HttpStatus.OK)
    public ChatResponse chat(@RequestBody ChatRequest req) {
        String response = llmService.chat(req.model(), req.systemMessage(), req.userMessage());
        return new ChatResponse(response);
    }

    public record ChatRequest(
            String model,
            String systemMessage,
            String userMessage
    ) {}

    public record ChatResponse(String response) {}
}
