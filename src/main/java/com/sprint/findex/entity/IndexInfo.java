package com.sprint.findex.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

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

    @Column(name = "constituent_count")
    private Integer constituentCount;

    @Column(name = "base_date")
    private LocalDate baseDate;

    @Column(name = "base_value")
    private BigDecimal baseValue;

    @Column(name = "source_type", nullable = false)
    private String sourceType;

    @Column(name = "is_favorite", nullable = false)
    private Boolean isFavorite = false;
}
