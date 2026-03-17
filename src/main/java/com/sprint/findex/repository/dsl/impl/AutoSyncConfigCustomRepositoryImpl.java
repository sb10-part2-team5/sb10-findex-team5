package com.sprint.findex.repository.dsl.impl;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.findex.dto.autosyncconfig.AutoSyncConfigDto;
import com.sprint.findex.dto.autosyncconfig.AutoSyncConfigQueryCondition;
import com.sprint.findex.dto.response.PageResponse;
import com.sprint.findex.entity.QAutoSyncConfig;
import com.sprint.findex.repository.dsl.AutoSyncConfigCustomRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AutoSyncConfigCustomRepositoryImpl implements AutoSyncConfigCustomRepository {

    private static final QAutoSyncConfig autoSyncConfig = QAutoSyncConfig.autoSyncConfig;

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public PageResponse<AutoSyncConfigDto> findAllWithCondition(
            AutoSyncConfigQueryCondition condition
    ) {
        List<AutoSyncConfigDto> content = jpaQueryFactory
                .select(Projections.constructor(
                        AutoSyncConfigDto.class,
                        autoSyncConfig.id,
                        autoSyncConfig.indexInfo.id,
                        autoSyncConfig.indexInfo.indexClassification,
                        autoSyncConfig.indexInfo.indexName,
                        autoSyncConfig.enabled
                ))
                .from(autoSyncConfig)
                .where(
                        indexInfoIdEq(condition.indexInfoId()),
                        enabledEq(condition.enabled()),
                        cursorCondition(condition)
                )
                .orderBy(
                        getOrderSpecifier(condition),
                        getIdOrderSpecifier(condition.sortDirection())
                )
                .limit(condition.size() + 1L)
                .fetch();

        boolean hasNext = content.size() > condition.size();
        String nextCursor = null;
        UUID nextIdAfter = null;

        if (hasNext) {
            AutoSyncConfigDto lastItem = content.get(condition.size() - 1);
            nextCursor = condition.sortField().getCursor(lastItem);
            nextIdAfter = lastItem.id();
            content = content.subList(0, condition.size());
        }

        long totalElements = (condition.cursor() == null && !hasNext)
                ? content.size()
                : getTotalElements(condition);

        return new PageResponse<>(
                content,
                nextCursor,
                nextIdAfter,
                content.size(),
                totalElements,
                hasNext
        );
    }

    private OrderSpecifier<?> getOrderSpecifier(AutoSyncConfigQueryCondition condition) {
        Order order = "desc".equalsIgnoreCase(condition.sortDirection()) ? Order.DESC : Order.ASC;
        return new OrderSpecifier<>(order, condition.sortField().getSortField(autoSyncConfig));
    }

    private OrderSpecifier<?> getIdOrderSpecifier(String sortDirection) {
        Order order = "desc".equalsIgnoreCase(sortDirection) ? Order.DESC : Order.ASC;
        return new OrderSpecifier<>(order, autoSyncConfig.id);
    }

    private BooleanExpression indexInfoIdEq(UUID indexInfoId) {
        return indexInfoId != null ? autoSyncConfig.indexInfo.id.eq(indexInfoId) : null;
    }

    private BooleanExpression enabledEq(Boolean enabled) {
        return enabled != null ? autoSyncConfig.enabled.eq(enabled) : null;
    }

    private BooleanExpression cursorCondition(AutoSyncConfigQueryCondition condition) {
        if (condition.cursor() == null || condition.idAfter() == null) {
            return null;
        }

        boolean asc = !"desc".equalsIgnoreCase(condition.sortDirection());
        return condition.sortField().buildCursorCondition(
                autoSyncConfig, condition.cursor(), condition.idAfter(), asc
        );
    }

    private Long getTotalElements(AutoSyncConfigQueryCondition condition) {
        return jpaQueryFactory
                .select(autoSyncConfig.count())
                .from(autoSyncConfig)
                .where(
                        indexInfoIdEq(condition.indexInfoId()),
                        enabledEq(condition.enabled())
                )
                .fetchOne();
    }
}
