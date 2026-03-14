package com.sprint.findex.entity;

import com.sprint.findex.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "integration_task")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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

  // 연동 성공 메서드(errorMessage null 처리)
  public static IntegrationTask create(IndexInfo indexInfo, String jobType, LocalDate targetDate,
      String worker, Instant jobTime, String result) {
    if (indexInfo == null || jobType == null || worker == null
        || jobTime == null || result == null) {
      throw new IllegalArgumentException("Null values are not allowed");
    }
    return new IntegrationTask(indexInfo, jobType, targetDate, worker, jobTime, result,
        null);
  }

  // 연동 실패 메서드(errorMessage 주입)
  public static IntegrationTask create(IndexInfo indexInfo, String jobType, LocalDate targetDate,
      String worker, Instant jobTime, String result, String errorMessage) {
    if (indexInfo == null || jobType == null || worker == null
        || jobTime == null || result == null || errorMessage == null) {
      throw new IllegalArgumentException("Null values are not allowed");
    }
    return new IntegrationTask(indexInfo, jobType, targetDate, worker, jobTime, result,
        errorMessage);
  }
}
