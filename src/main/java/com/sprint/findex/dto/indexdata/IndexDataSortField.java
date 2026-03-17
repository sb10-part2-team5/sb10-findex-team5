package com.sprint.findex.dto.indexdata;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.sprint.findex.entity.QIndexData;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public enum IndexDataSortField {

    baseDate {
        @Override
        public ComparableExpressionBase<?> getSortField(QIndexData q) {
            return q.baseDate;
        }

        @Override
        public String getCursor(IndexDataDto dto) {
            return String.valueOf(dto.baseDate());
        }

        @Override
        public BooleanExpression buildCursorCondition(QIndexData q, String cursor, UUID idAfter,
                boolean asc) {
            LocalDate val = LocalDate.parse(cursor);
            return buildCondition(asc ? q.baseDate.gt(val) : q.baseDate.lt(val), q.baseDate.eq(val),
                    q.id, idAfter, asc);
        }
    },
    marketPrice {
        @Override
        public ComparableExpressionBase<?> getSortField(QIndexData q) {
            return q.marketPrice;
        }

        @Override
        public String getCursor(IndexDataDto dto) {
            return String.valueOf(dto.marketPrice());
        }

        @Override
        public BooleanExpression buildCursorCondition(QIndexData q, String cursor, UUID idAfter,
                boolean asc) {
            BigDecimal val = new BigDecimal(cursor);
            return buildCondition(asc ? q.marketPrice.gt(val) : q.marketPrice.lt(val),
                    q.marketPrice.eq(val), q.id, idAfter, asc);
        }
    },
    closingPrice {
        @Override
        public ComparableExpressionBase<?> getSortField(QIndexData q) {
            return q.closingPrice;
        }

        @Override
        public String getCursor(IndexDataDto dto) {
            return String.valueOf(dto.closingPrice());
        }

        @Override
        public BooleanExpression buildCursorCondition(QIndexData q, String cursor, UUID idAfter,
                boolean asc) {
            BigDecimal val = new BigDecimal(cursor);
            return buildCondition(asc ? q.closingPrice.gt(val) : q.closingPrice.lt(val),
                    q.closingPrice.eq(val), q.id, idAfter, asc);
        }
    },
    highPrice {
        @Override
        public ComparableExpressionBase<?> getSortField(QIndexData q) {
            return q.highPrice;
        }

        @Override
        public String getCursor(IndexDataDto dto) {
            return String.valueOf(dto.highPrice());
        }

        @Override
        public BooleanExpression buildCursorCondition(QIndexData q, String cursor, UUID idAfter,
                boolean asc) {
            BigDecimal val = new BigDecimal(cursor);
            return buildCondition(asc ? q.highPrice.gt(val) : q.highPrice.lt(val),
                    q.highPrice.eq(val), q.id, idAfter, asc);
        }
    },
    lowPrice {
        @Override
        public ComparableExpressionBase<?> getSortField(QIndexData q) {
            return q.lowPrice;
        }

        @Override
        public String getCursor(IndexDataDto dto) {
            return String.valueOf(dto.lowPrice());
        }

        @Override
        public BooleanExpression buildCursorCondition(QIndexData q, String cursor, UUID idAfter,
                boolean asc) {
            BigDecimal val = new BigDecimal(cursor);
            return buildCondition(asc ? q.lowPrice.gt(val) : q.lowPrice.lt(val), q.lowPrice.eq(val),
                    q.id, idAfter, asc);
        }
    },
    versus {
        @Override
        public ComparableExpressionBase<?> getSortField(QIndexData q) {
            return q.versus;
        }

        @Override
        public String getCursor(IndexDataDto dto) {
            return String.valueOf(dto.versus());
        }

        @Override
        public BooleanExpression buildCursorCondition(QIndexData q, String cursor, UUID idAfter,
                boolean asc) {
            BigDecimal val = new BigDecimal(cursor);
            return buildCondition(asc ? q.versus.gt(val) : q.versus.lt(val), q.versus.eq(val), q.id,
                    idAfter, asc);
        }
    },
    fluctuationRate {
        @Override
        public ComparableExpressionBase<?> getSortField(QIndexData q) {
            return q.fluctuationRate;
        }

        @Override
        public String getCursor(IndexDataDto dto) {
            return String.valueOf(dto.fluctuationRate());
        }

        @Override
        public BooleanExpression buildCursorCondition(QIndexData q, String cursor, UUID idAfter,
                boolean asc) {
            BigDecimal val = new BigDecimal(cursor);
            return buildCondition(asc ? q.fluctuationRate.gt(val) : q.fluctuationRate.lt(val),
                    q.fluctuationRate.eq(val), q.id, idAfter, asc);
        }
    },
    tradingQuantity {
        @Override
        public ComparableExpressionBase<?> getSortField(QIndexData q) {
            return q.tradingQuantity;
        }

        @Override
        public String getCursor(IndexDataDto dto) {
            return String.valueOf(dto.tradingQuantity());
        }

        @Override
        public BooleanExpression buildCursorCondition(QIndexData q, String cursor, UUID idAfter,
                boolean asc) {
            Long val = Long.parseLong(cursor);
            return buildCondition(asc ? q.tradingQuantity.gt(val) : q.tradingQuantity.lt(val),
                    q.tradingQuantity.eq(val), q.id, idAfter, asc);
        }
    };

    // 엔티티의 어떤 컬럼으로 정렬할지 반환
    public abstract ComparableExpressionBase<?> getSortField(QIndexData q);

    // DTO에서 다음 페이지 조회를 위한 커서 값 추출
    public abstract String getCursor(IndexDataDto dto);

    // 커서 페이징을 위한 WHERE 조건식 생성 (타입 파싱 포함)
    public abstract BooleanExpression buildCursorCondition(QIndexData q, String cursor,
            UUID idAfter, boolean asc);

    // 커서 조건 조립
    protected BooleanExpression buildCondition(
            BooleanExpression comparison, BooleanExpression equality,
            ComparableExpression<UUID> idField, UUID idAfter, boolean asc) {
        if (asc) {
            return comparison.or(equality.and(idField.gt(idAfter)));
        }
        return comparison.or(equality.and(idField.lt(idAfter)));
    }
}