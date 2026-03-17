package com.sprint.findex.repository.dsl.impl;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.findex.dto.autosyncconfig.AutoSyncConfigDto;
import com.sprint.findex.dto.autosyncconfig.AutoSyncConfigQueryCondition;
import com.sprint.findex.dto.response.PageResponse;
import com.sprint.findex.entity.QAutoSyncConfig;
import com.sprint.findex.exception.BusinessLogicException;
import com.sprint.findex.exception.ExceptionCode;
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
                        getOrderSpecifier(condition.sortField(), condition.sortDirection()),
                        getOrderSpecifier("id", condition.sortDirection())
                )
                .limit(condition.size() + 1L)
                .fetch();

        boolean hasNext = content.size() > condition.size();
        String nextCursor = null;
        UUID nextIdAfter = null;

        if (hasNext) {
            AutoSyncConfigDto lastItem = content.get(condition.size() - 1);
            nextCursor = getNextCursor(lastItem, condition.sortField());
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

    private OrderSpecifier<?> getOrderSpecifier(String sortField, String sortDirection) {
        Order order = "desc".equalsIgnoreCase(sortDirection) ? Order.DESC : Order.ASC;
        return new OrderSpecifier<>(order, getSortField(sortField));
    }

    private ComparableExpressionBase<?> getSortField(String sortField) {
        return switch (sortField) {
            case "id" -> autoSyncConfig.id;
            case "enabled" -> autoSyncConfig.enabled;
            case "indexInfo.indexName" -> autoSyncConfig.indexInfo.indexName;
            default -> throw new BusinessLogicException(
                    ExceptionCode.AUTO_SYNC_CONFIG_INVALID_QUERY_CONDITION);
        };
    }

    private BooleanExpression indexInfoIdEq(UUID indexInfoId) {
        return indexInfoId != null ? autoSyncConfig.indexInfo.id.eq(indexInfoId) : null;
    }

    private BooleanExpression enabledEq(Boolean enabled) {
        return enabled != null ? autoSyncConfig.enabled.eq(enabled) : null;
    }

    private String getNextCursor(AutoSyncConfigDto lastDto, String sortField) {
        return switch (sortField) {
            case "enabled" -> String.valueOf(lastDto.enabled());
            case "indexInfo.indexName" -> lastDto.indexName();
            default -> throw new BusinessLogicException(
                    ExceptionCode.AUTO_SYNC_CONFIG_INVALID_QUERY_CONDITION);
        };
    }

    private BooleanExpression cursorCondition(AutoSyncConfigQueryCondition condition) {
        if (condition.cursor() == null || condition.idAfter() == null) {
            return null;
        }

        boolean asc = !"desc".equalsIgnoreCase(condition.sortDirection());

        switch (condition.sortField()) {
            case "indexInfo.indexName":
                if (asc) {
                    return autoSyncConfig.indexInfo.indexName.gt(condition.cursor())
                            .or(autoSyncConfig.indexInfo.indexName.eq(condition.cursor())
                                    .and(autoSyncConfig.id.gt(condition.idAfter())));
                }
                return autoSyncConfig.indexInfo.indexName.lt(condition.cursor())
                        .or(autoSyncConfig.indexInfo.indexName.eq(condition.cursor())
                                .and(autoSyncConfig.id.lt(condition.idAfter())));

            case "enabled":
                boolean cursorValue = Boolean.parseBoolean(condition.cursor());

                if (asc) {
                    if (!cursorValue) {
                        return autoSyncConfig.enabled.isTrue()
                                .or(autoSyncConfig.enabled.eq(false)
                                        .and(autoSyncConfig.id.gt(condition.idAfter())));
                    }
                    return autoSyncConfig.enabled.eq(true)
                            .and(autoSyncConfig.id.gt(condition.idAfter()));
                }

                if (cursorValue) {
                    return autoSyncConfig.enabled.isFalse()
                            .or(autoSyncConfig.enabled.eq(true)
                                    .and(autoSyncConfig.id.lt(condition.idAfter())));
                }
                return autoSyncConfig.enabled.eq(false)
                        .and(autoSyncConfig.id.lt(condition.idAfter()));

            default:
                throw new BusinessLogicException(
                        ExceptionCode.AUTO_SYNC_CONFIG_INVALID_QUERY_CONDITION);
        }
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
