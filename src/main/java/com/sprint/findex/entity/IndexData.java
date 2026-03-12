package com.sprint.findex.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Entity
@Table(
        name = "index_data",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_index_data_index_info_trade_date",
                        columnNames = {"index_info_id", "trade_date"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IndexData extends BaseUpdatableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "index_id", nullable = false)
    private IndexInfo indexInfo;

    @Column(name = "trade_date", nullable = false)
    private LocalDate tradeDate;

    @Column(name = "source_type", nullable = false)
    private String sourceType;

    @Column(name = "open_value")
    private BigDecimal openValue;

    @Column(name = "close_value")
    private BigDecimal closeValue;

    @Column(name = "high_value")
    private BigDecimal highValue;

    @Column(name = "low_value")
    private BigDecimal lowValue;

    @Column(name = "change_amount")
    private BigDecimal changeAmount;

    @Column(name = "change_rate")
    private BigDecimal changeRate;

    @Column(name = "volume")
    private Long volume;

    @Column(name = "trade_amount")
    private BigDecimal tradeAmount;

    @Column(name = "market_cap")
    private BigDecimal marketCap;
}
