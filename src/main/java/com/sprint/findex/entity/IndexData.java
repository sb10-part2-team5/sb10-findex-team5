package com.sprint.findex.entity;

import com.sprint.findex.entity.base.BaseUpdatableEntity;
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
                        name = "uk_index_data_index_info_base_date",
                        columnNames = {"index_info_id", "base_date"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IndexData extends BaseUpdatableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "index_info_id", nullable = false)
    private IndexInfo indexInfo;

    @Column(name = "base_date", nullable = false)
    private LocalDate baseDate;

    @Column(name = "source_type", nullable = false)
    private String sourceType;

    @Column(name = "market_price", nullable = false)
    private BigDecimal marketPrice;

    @Column(name = "closing_price", nullable = false)
    private BigDecimal closingPrice;

    @Column(name = "high_price", nullable = false)
    private BigDecimal highPrice;

    @Column(name = "low_price", nullable = false)
    private BigDecimal lowPrice;

    @Column(name = "versus", nullable = false)
    private BigDecimal versus;

    @Column(name = "fluctuation_rate", nullable = false)
    private BigDecimal fluctuationRate;

    @Column(name = "trading_quantity", nullable = false)
    private Long tradingQuantity;

    @Column(name = "trading_price", nullable = false)
    private Long tradingPrice;

    @Column(name = "market_total_amount", nullable = false)
    private Long marketTotalAmount;
}
