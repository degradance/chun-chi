package com.chunchi.agent;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgentCycleRepository extends JpaRepository<AgentCycle, Long> {
    List<AgentCycle> findByAgentIdOrderByCycleNumberDesc(Long agentId);
    Page<AgentCycle> findByAgentId(Long agentId, Pageable pageable);
    int countByAgentId(Long agentId);
}
