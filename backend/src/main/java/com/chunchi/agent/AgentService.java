package com.chunchi.agent;

import com.chunchi.llm.LlmService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgentService {

    private final AgentRepository agentRepository;
    private final AgentCycleRepository cycleRepository;
    private final LlmService llmService;

    // ── Agent CRUD ──────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<AgentDto.Response> listAgents() {
        return agentRepository.findAll().stream()
                .map(a -> AgentDto.Response.from(a, cycleRepository.countByAgentId(a.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public AgentDto.Response getAgent(Long id) {
        Agent agent = findAgent(id);
        return AgentDto.Response.from(agent, cycleRepository.countByAgentId(id));
    }

    @Transactional
    public AgentDto.Response createAgent(AgentDto.CreateRequest req) {
        if (agentRepository.existsByName(req.name())) {
            throw new IllegalArgumentException("Agent with name '" + req.name() + "' already exists");
        }
        Agent agent = new Agent();
        agent.setName(req.name());
        agent.setSystemPrompt(req.systemPrompt());
        agent.setModel(req.model());
        Agent saved = agentRepository.save(agent);
        log.info("Created agent '{}' (id={})", saved.getName(), saved.getId());
        return AgentDto.Response.from(saved, 0);
    }

    @Transactional
    public AgentDto.Response updateAgent(Long id, AgentDto.UpdateRequest req) {
        Agent agent = findAgent(id);
        if (req.name() != null && !req.name().isBlank()) {
            agent.setName(req.name());
        }
        if (req.systemPrompt() != null) {
            agent.setSystemPrompt(req.systemPrompt());
        }
        if (req.model() != null && !req.model().isBlank()) {
            agent.setModel(req.model());
        }
        Agent saved = agentRepository.save(agent);
        return AgentDto.Response.from(saved, cycleRepository.countByAgentId(id));
    }

    @Transactional
    public void deleteAgent(Long id) {
        Agent agent = findAgent(id);
        agentRepository.delete(agent);
        log.info("Deleted agent id={}", id);
    }

    @Transactional
    public AgentDto.Response updateAgentStatus(Long id, Agent.AgentStatus newStatus) {
        Agent agent = findAgent(id);
        agent.setStatus(newStatus);
        Agent saved = agentRepository.save(agent);
        log.info("Agent id={} status → {}", id, newStatus);
        return AgentDto.Response.from(saved, cycleRepository.countByAgentId(id));
    }

    // ── Cycle management ────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<AgentDto.CycleResponse> listCycles(Long agentId, Pageable pageable) {
        findAgent(agentId); // validate agent exists
        return cycleRepository.findByAgentId(agentId, pageable)
                .map(AgentDto.CycleResponse::from);
    }

    @Transactional(readOnly = true)
    public AgentDto.CycleResponse getCycle(Long agentId, Long cycleId) {
        AgentCycle cycle = findCycle(cycleId);
        if (!cycle.getAgent().getId().equals(agentId)) {
            throw new EntityNotFoundException("Cycle " + cycleId + " not found for agent " + agentId);
        }
        return AgentDto.CycleResponse.from(cycle);
    }

    /**
     * Runs a single agent cycle: sends the prompt to the LLM and stores the result.
     */
    @Transactional
    public AgentDto.CycleResponse runCycle(Long agentId, AgentDto.RunCycleRequest req) {
        Agent agent = findAgent(agentId);

        if (agent.getStatus() == Agent.AgentStatus.STOPPED) {
            throw new IllegalStateException("Agent is stopped. Resume it before running a cycle.");
        }

        int nextNumber = cycleRepository.countByAgentId(agentId) + 1;

        AgentCycle cycle = new AgentCycle();
        cycle.setAgent(agent);
        cycle.setCycleNumber(nextNumber);
        cycle.setInputPrompt(req.prompt());
        cycle.setStatus(AgentCycle.CycleStatus.RUNNING);
        cycle.setStartedAt(Instant.now());
        cycle = cycleRepository.save(cycle);

        agent.setStatus(Agent.AgentStatus.RUNNING);
        agentRepository.save(agent);

        try {
            String fullPrompt = buildPrompt(agent.getSystemPrompt(), req.prompt());
            String response = llmService.chat(agent.getModel(), fullPrompt);

            cycle.setRawResponse(response);
            cycle.setParsedOutput(response); // extend with parsing logic as needed
            cycle.setStatus(AgentCycle.CycleStatus.COMPLETED);
            cycle.setCompletedAt(Instant.now());
            cycle.setDurationMs(cycle.getCompletedAt().toEpochMilli() - cycle.getStartedAt().toEpochMilli());

            agent.setStatus(Agent.AgentStatus.IDLE);
        } catch (Exception e) {
            log.error("Cycle {} for agent {} failed: {}", nextNumber, agentId, e.getMessage(), e);
            cycle.setStatus(AgentCycle.CycleStatus.FAILED);
            cycle.setErrorMessage(e.getMessage());
            cycle.setCompletedAt(Instant.now());
            cycle.setDurationMs(cycle.getCompletedAt().toEpochMilli() - cycle.getStartedAt().toEpochMilli());
            agent.setStatus(Agent.AgentStatus.ERROR);
        }

        cycleRepository.save(cycle);
        agentRepository.save(agent);

        return AgentDto.CycleResponse.from(cycle);
    }

    @Transactional
    public AgentDto.CycleResponse cancelCycle(Long agentId, Long cycleId) {
        AgentCycle cycle = findCycle(cycleId);
        if (!cycle.getAgent().getId().equals(agentId)) {
            throw new EntityNotFoundException("Cycle " + cycleId + " not found for agent " + agentId);
        }
        if (cycle.getStatus() != AgentCycle.CycleStatus.RUNNING) {
            throw new IllegalStateException("Only RUNNING cycles can be cancelled");
        }
        cycle.setStatus(AgentCycle.CycleStatus.CANCELLED);
        cycle.setCompletedAt(Instant.now());
        AgentCycle saved = cycleRepository.save(cycle);

        Agent agent = cycle.getAgent();
        agent.setStatus(Agent.AgentStatus.IDLE);
        agentRepository.save(agent);

        return AgentDto.CycleResponse.from(saved);
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    private Agent findAgent(Long id) {
        return agentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Agent not found: " + id));
    }

    private AgentCycle findCycle(Long id) {
        return cycleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("AgentCycle not found: " + id));
    }

    private String buildPrompt(String systemPrompt, String userPrompt) {
        if (systemPrompt == null || systemPrompt.isBlank()) {
            return userPrompt;
        }
        return systemPrompt + "\n\n" + userPrompt;
    }
}
