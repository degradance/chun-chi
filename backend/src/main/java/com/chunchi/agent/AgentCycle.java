package com.chunchi.agent;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Represents one execution cycle of an agent (prompt → LLM response → result).
 */
@Entity
@Table(name = "agent_cycles")
@Getter
@Setter
@NoArgsConstructor
public class AgentCycle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "agent_id", nullable = false)
    private Agent agent;

    @Column(nullable = false)
    private int cycleNumber;

    @Column(columnDefinition = "TEXT")
    private String inputPrompt;

    @Column(columnDefinition = "TEXT")
    private String rawResponse;

    @Column(columnDefinition = "TEXT")
    private String parsedOutput;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CycleStatus status = CycleStatus.PENDING;

    @Column(nullable = false, updatable = false)
    private Instant startedAt = Instant.now();

    private Instant completedAt;

    /** Duration in milliseconds */
    private Long durationMs;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @PreUpdate
    void onUpdate() {
        if (status == CycleStatus.COMPLETED || status == CycleStatus.FAILED) {
            completedAt = Instant.now();
            if (startedAt != null) {
                durationMs = completedAt.toEpochMilli() - startedAt.toEpochMilli();
            }
        }
    }

    public enum CycleStatus {
        PENDING, RUNNING, COMPLETED, FAILED, CANCELLED
    }
}
