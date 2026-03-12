package com.sprint.findex.entity;

import com.sprint.findex.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Entity
@Table(name = "integration_task")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IntegrationTask extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "index_info_id", nullable = false)
    private IndexInfo indexInfo;

    @Column(name = "job_type", nullable = false)
    private String jobType;

    @Column(name = "target_date")
    private LocalDate targetDate;

    @Column(name = "worker", nullable = false)
    private String worker;

    @Column(name = "job_time", nullable = false)
    private Instant jobTime;

    @Column(name = "result", nullable = false)
    private String result;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
}
