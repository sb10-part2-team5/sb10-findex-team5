package com.sprint.findex.entity;

import com.sprint.findex.entity.base.BaseUpdatableEntity;
import com.sprint.findex.enums.SourceType;
import com.sprint.findex.exception.BusinessLogicException;
import com.sprint.findex.exception.ExceptionCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

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
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class IndexData extends BaseUpdatableEntity {

    private static final int PRICE_SCALE = 4;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "index_info_id", nullable = false)
    private IndexInfo indexInfo;

    @Column(name = "base_date", nullable = false)
    private LocalDate baseDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false, length = 30)
    @Check(constraints = "source_type IN ('USER', 'OPEN_API')")
    private SourceType sourceType;

    @Column(name = "market_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal marketPrice;

    @Column(name = "closing_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal closingPrice;

    @Column(name = "high_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal highPrice;

    @Column(name = "low_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal lowPrice;

    @Column(name = "versus", nullable = false, precision = 19, scale = 4)
    private BigDecimal versus;

    @Column(name = "fluctuation_rate", nullable = false, precision = 9, scale = 4)
    private BigDecimal fluctuationRate;

    @Column(name = "trading_quantity", nullable = false)
    private Long tradingQuantity;

    @Column(name = "trading_price", nullable = false)
    private Long tradingPrice;

    @Column(name = "market_total_amount", nullable = false)
    private Long marketTotalAmount;

    public static IndexData create(
            IndexInfo indexInfo,
            LocalDate baseDate,
            SourceType sourceType,
            BigDecimal marketPrice,
            BigDecimal closingPrice,
            BigDecimal highPrice,
            BigDecimal lowPrice,
            BigDecimal versus,
            BigDecimal fluctuationRate,
            Long tradingQuantity,
            Long tradingPrice,
            Long marketTotalAmount
    ) {
        return new IndexData(
                requireValue(indexInfo),
                requireValue(baseDate),
                requireValue(sourceType),
                normalize(marketPrice),
                normalize(closingPrice),
                normalize(highPrice),
                normalize(lowPrice),
                normalize(versus),
                normalize(fluctuationRate),
                requireValue(tradingQuantity),
                requireValue(tradingPrice),
                requireValue(marketTotalAmount)
        );
    }

    private static <T> T requireValue(T value) { // Dto를 거치지않고 내부 호출 시 null 예외 처리
        if (value == null) {
            throw new BusinessLogicException(ExceptionCode.INVALID_INDEX_DATA_REQUEST);
        }
        return value;
    }

    private static BigDecimal normalize(BigDecimal value) { // 소수점 (PRICE_SCALE + 1)자리에서 반올림
        return requireValue(value).setScale(PRICE_SCALE, RoundingMode.HALF_UP);
    }

    public void update(
            BigDecimal marketPrice,
            BigDecimal closingPrice,
            BigDecimal highPrice,
            BigDecimal lowPrice,
            BigDecimal versus,
            BigDecimal fluctuationRate,
            Long tradingQuantity,
            Long tradingPrice,
            Long marketTotalAmount
    ) {
        this.marketPrice = normalize(marketPrice);
        this.closingPrice = normalize(closingPrice);
        this.highPrice = normalize(highPrice);
        this.lowPrice = normalize(lowPrice);
        this.versus = normalize(versus);
        this.fluctuationRate = normalize(fluctuationRate);
        this.tradingQuantity = requireValue(tradingQuantity);
        this.tradingPrice = requireValue(tradingPrice);
        this.marketTotalAmount = requireValue(marketTotalAmount);
    }
}
