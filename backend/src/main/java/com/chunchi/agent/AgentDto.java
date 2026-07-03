package com.chunchi.agent;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

/**
 * DTOs for the Agent REST API.
 */
public final class AgentDto {

    private AgentDto() {}

    public record CreateRequest(
            @NotBlank String name,
            String systemPrompt,
            @NotBlank String model
    ) {}

    public record UpdateRequest(
            String name,
            String systemPrompt,
            String model
    ) {}

    public record Response(
            Long id,
            String name,
            String systemPrompt,
            String model,
            Agent.AgentStatus status,
            Instant createdAt,
            Instant updatedAt,
            int totalCycles
    ) {
        static Response from(Agent agent, int totalCycles) {
            return new Response(
                    agent.getId(),
                    agent.getName(),
                    agent.getSystemPrompt(),
                    agent.getModel(),
                    agent.getStatus(),
                    agent.getCreatedAt(),
                    agent.getUpdatedAt(),
                    totalCycles
            );
        }
    }

    public record CycleResponse(
            Long id,
            Long agentId,
            int cycleNumber,
            String inputPrompt,
            String rawResponse,
            String parsedOutput,
            AgentCycle.CycleStatus status,
            Instant startedAt,
            Instant completedAt,
            Long durationMs,
            String errorMessage
    ) {
        static CycleResponse from(AgentCycle cycle) {
            return new CycleResponse(
                    cycle.getId(),
                    cycle.getAgent().getId(),
                    cycle.getCycleNumber(),
                    cycle.getInputPrompt(),
                    cycle.getRawResponse(),
                    cycle.getParsedOutput(),
                    cycle.getStatus(),
                    cycle.getStartedAt(),
                    cycle.getCompletedAt(),
                    cycle.getDurationMs(),
                    cycle.getErrorMessage()
            );
        }
    }

    public record RunCycleRequest(
            @NotBlank String prompt
    ) {}
}
