package com.sprint.findex.entity;

import com.sprint.findex.entity.base.BaseUpdatableEntity;
import com.sprint.findex.enums.SourceType;
import jakarta.persistence.*;
import java.math.RoundingMode;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Entity
@Table(name = "index_info",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_index_info_class_name",
            columnNames = {"index_classification", "index_name"})
    })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class IndexInfo extends BaseUpdatableEntity {

  @Column(name = "index_name", nullable = false)
  private String indexName;

  @Column(name = "index_classification", nullable = false)
  private String indexClassification;

  @Column(name = "employed_items_count", nullable = false)
  private Integer employedItemsCount;

  @Column(name = "base_point_in_time", nullable = false)
  private LocalDate basePointInTime;

  @Column(name = "base_index", nullable = false, precision = 19, scale = 4)
  private BigDecimal baseIndex;

  @Column(name = "source_type", nullable = false)
  @Enumerated(EnumType.STRING)
  private SourceType sourceType;

  @Column(name = "favorite", nullable = false)
  private Boolean favorite = false;

  public static IndexInfo create(String indexName, String indexClassification,
      Integer employedItemsCount, LocalDate basePointInTime, BigDecimal baseIndex,
      SourceType sourceType, Boolean favorite) {
    if (indexName == null || indexClassification == null || employedItemsCount == null
        || basePointInTime == null || baseIndex == null || sourceType == null) {
      throw new IllegalArgumentException("Null values are not allowed");
    }
    return new IndexInfo(indexName, indexClassification, employedItemsCount, basePointInTime,
        baseIndex.setScale(4, RoundingMode.HALF_UP), sourceType,
        favorite != null && favorite);
  }
}
