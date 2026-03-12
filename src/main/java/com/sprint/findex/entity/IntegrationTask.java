package com.sprint.findex.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.time.OffsetDateTime;

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
    private OffsetDateTime taskAt;

    @Column(name = "result", nullable = false)
    private String result;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
