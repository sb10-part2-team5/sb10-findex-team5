package com.sprint.findex.entity;

import com.sprint.findex.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
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
public class IndexInfo extends BaseUpdatableEntity {

    @Column(name = "index_name", nullable = false)
    private String indexName;

    @Column(name = "index_classification", nullable = false)
    private String indexClassification;

    @Column(name = "employed_items_count", nullable = false)
    private Integer employedItemsCount;

    @Column(name = "base_point_in_time", nullable = false)
    private LocalDate basePointInTime;

    @Column(name = "base_index", nullable = false)
    private BigDecimal baseIndex;

    @Column(name = "source_type", nullable = false)
    private String sourceType;

    @Column(name = "favorite", nullable = false)
    private Boolean favorite = false;
}
