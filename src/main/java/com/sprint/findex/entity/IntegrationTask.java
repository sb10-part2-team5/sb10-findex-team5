package com.sprint.findex.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "integration_task")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IntegrationTask extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "index_id", nullable = false)
    private IndexInfo indexInfo;

    @Column(name = "task_type", nullable = false)
    private String taskType;

    @Column(name = "target_date")
    private LocalDate targetDate;

    @Column(name = "operator", nullable = false)
    private String operator;

    @Column(name = "task_at", nullable = false)
    private Instant taskAt;

    @Column(name = "result", nullable = false)
    private String result;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
}
