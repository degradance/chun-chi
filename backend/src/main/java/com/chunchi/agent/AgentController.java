package com.chunchi.agent;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Agent and AgentCycle management.
 */
@RestController
@RequestMapping("/api/agents")
@RequiredArgsConstructor
public class AgentController {

    private final AgentService agentService;

    // ── Agent endpoints ──────────────────────────────────────────────────────

    @GetMapping
    public List<AgentDto.Response> listAgents() {
        return agentService.listAgents();
    }

    @GetMapping("/{id}")
    public AgentDto.Response getAgent(@PathVariable Long id) {
        return agentService.getAgent(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AgentDto.Response createAgent(@Valid @RequestBody AgentDto.CreateRequest req) {
        return agentService.createAgent(req);
    }

    @PutMapping("/{id}")
    public AgentDto.Response updateAgent(
            @PathVariable Long id,
            @RequestBody AgentDto.UpdateRequest req) {
        return agentService.updateAgent(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAgent(@PathVariable Long id) {
        agentService.deleteAgent(id);
    }

    @PatchMapping("/{id}/status")
    public AgentDto.Response updateStatus(
            @PathVariable Long id,
            @RequestParam Agent.AgentStatus status) {
        return agentService.updateAgentStatus(id, status);
    }

    // ── Cycle endpoints ──────────────────────────────────────────────────────

    @GetMapping("/{agentId}/cycles")
    public Page<AgentDto.CycleResponse> listCycles(
            @PathVariable Long agentId,
            @PageableDefault(size = 20) Pageable pageable) {
        return agentService.listCycles(agentId, pageable);
    }

    @GetMapping("/{agentId}/cycles/{cycleId}")
    public AgentDto.CycleResponse getCycle(
            @PathVariable Long agentId,
            @PathVariable Long cycleId) {
        return agentService.getCycle(agentId, cycleId);
    }

    @PostMapping("/{agentId}/cycles")
    @ResponseStatus(HttpStatus.CREATED)
    public AgentDto.CycleResponse runCycle(
            @PathVariable Long agentId,
            @Valid @RequestBody AgentDto.RunCycleRequest req) {
        return agentService.runCycle(agentId, req);
    }

    @PostMapping("/{agentId}/cycles/{cycleId}/cancel")
    public AgentDto.CycleResponse cancelCycle(
            @PathVariable Long agentId,
            @PathVariable Long cycleId) {
        return agentService.cancelCycle(agentId, cycleId);
    }

    @PatchMapping("/{id}/pause")
    public AgentDto.Response pauseAgent(@PathVariable Long id) {
        return agentService.updateAgentStatus(id, Agent.AgentStatus.PAUSED);
    }

    @PatchMapping("/{id}/resume")
    public AgentDto.Response resumeAgent(@PathVariable Long id) {
        return agentService.updateAgentStatus(id, Agent.AgentStatus.IDLE);
    }

    @PatchMapping("/{id}/stop")
    public AgentDto.Response stopAgent(@PathVariable Long id) {
        return agentService.updateAgentStatus(id, Agent.AgentStatus.STOPPED);
    }
}
