package com.sprint.findex.repository.dsl.impl;

import static com.sprint.findex.entity.QIndexData.indexData;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.findex.dto.indexdata.IndexDataDto;
import com.sprint.findex.dto.indexdata.IndexDataQueryCondition;
import com.sprint.findex.dto.indexdata.IndexDataSortField;
import com.sprint.findex.repository.dsl.IndexDataCustomRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class IndexDataCustomRepositoryImpl implements IndexDataCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<IndexDataDto> findAllWithIndexDataQueryCondition(
            IndexDataQueryCondition condition) {
        return jpaQueryFactory
                .select(Projections.constructor(IndexDataDto.class,
                        indexData.id,
                        indexData.indexInfo.id,
                        indexData.baseDate,
                        indexData.sourceType,
                        indexData.marketPrice,
                        indexData.closingPrice,
                        indexData.highPrice,
                        indexData.lowPrice,
                        indexData.versus,
                        indexData.fluctuationRate,
                        indexData.tradingQuantity,
                        indexData.tradingPrice,
                        indexData.marketTotalAmount
                ))
                .from(indexData)
                .where(
                        indexInfoIdEq(condition.indexInfoId()),
                        dateRange(condition.startDate(), condition.endDate()),
                        cursorCondition(condition) // Enum에게 위임
                )
                .orderBy(
                        getOrderSpecifier(condition.sortField(), condition.sortDirection()),
                        getIdOrderSpecifier(condition.sortDirection()) // 2차 정렬 분리
                )
                .limit(condition.size() + 1)
                .fetch();
    }

    private BooleanExpression indexInfoIdEq(UUID indexInfoId) {
        return indexInfoId != null ? indexData.indexInfo.id.eq(indexInfoId) : null;
    }

    private BooleanExpression dateRange(LocalDate start, LocalDate end) {
        if (start != null && end != null) {
            return indexData.baseDate.between(start, end);
        }
        if (start != null) {
            return indexData.baseDate.goe(start);
        }
        if (end != null) {
            return indexData.baseDate.loe(end);
        }
        return null;
    }

    private BooleanExpression cursorCondition(IndexDataQueryCondition condition) {
        if (condition.cursor() == null || condition.idAfter() == null) {
            return null;
        }
        boolean asc = !condition.sortDirection().equals("desc");
        return condition.sortField()
                .buildCursorCondition(indexData, condition.cursor(), condition.idAfter(), asc);
    }

    private OrderSpecifier<?> getOrderSpecifier(IndexDataSortField sortField,
            String sortDirection) {
        Order order = sortDirection.equals("desc") ? Order.DESC : Order.ASC;
        return new OrderSpecifier<>(order, sortField.getSortField(indexData));
    }

    private OrderSpecifier<?> getIdOrderSpecifier(String sortDirection) {
        Order order = sortDirection.equals("desc") ? Order.DESC : Order.ASC;
        return new OrderSpecifier<>(order, indexData.id);
    }
}