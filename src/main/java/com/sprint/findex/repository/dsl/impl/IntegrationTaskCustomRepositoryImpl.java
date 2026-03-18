package com.sprint.findex.repository.dsl.impl;

import static com.sprint.findex.entity.QIndexInfo.indexInfo;
import static com.sprint.findex.entity.QIntegrationTask.integrationTask;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.findex.dto.response.PageResponse;
import com.sprint.findex.dto.sync.SyncJobQueryCondition;
import com.sprint.findex.entity.IntegrationTask;
import com.sprint.findex.repository.dsl.IntegrationTaskCustomRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class IntegrationTaskCustomRepositoryImpl implements IntegrationTaskCustomRepository {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public PageResponse<IntegrationTask> findAllWithSyncJobQueryCondition(SyncJobQueryCondition condition) {
        List<IntegrationTask> tasks = jpaQueryFactory
                .selectFrom(integrationTask)
                .join(integrationTask.indexInfo, indexInfo).fetchJoin()
                .where(
                        jobTypeEq(condition),
                        indexInfoIdEq(condition),
                        targetDateBetween(condition),
                        workerContains(condition),
                        jobTimeBetween(condition),
                        resultEq(condition),
                        cursorCondition(condition)
                )
                .orderBy(
                        getOrderSpecifier(condition),
                        getIdOrderSpecifier(condition)
                )
                .limit(condition.size() + 1L)
                .fetch();

        boolean hasNext = tasks.size() > condition.size();
        List<IntegrationTask> content = hasNext ? tasks.subList(0, condition.size()) : tasks;

        String nextCursor = null;
        UUID nextIdAfter = null;
        if (!content.isEmpty()) {
            IntegrationTask lastTask = content.get(content.size() - 1);
            nextCursor = condition.sortField().getCursor(lastTask);
            nextIdAfter = lastTask.getId();
        }

        long totalElements = (condition.cursor() == null && !hasNext)
                ? content.size()
                : countWithSyncJobQueryCondition(condition);

        return new PageResponse<>(
                content,
                nextCursor,
                nextIdAfter,
                content.size(),
                totalElements,
                hasNext
        );
    }

    public long countWithSyncJobQueryCondition(SyncJobQueryCondition condition) {
        Long count = jpaQueryFactory
                .select(integrationTask.count())
                .from(integrationTask)
                .where(
                        jobTypeEq(condition),
                        indexInfoIdEq(condition),
                        targetDateBetween(condition),
                        workerContains(condition),
                        jobTimeBetween(condition),
                        resultEq(condition)
                )
                .fetchOne();

        return count == null ? 0L : count;
    }

    private BooleanExpression jobTypeEq(SyncJobQueryCondition condition) {
        return condition.jobType() != null
                ? integrationTask.jobType.eq(condition.jobType().name())
                : null;
    }

    private BooleanExpression indexInfoIdEq(SyncJobQueryCondition condition) {
        UUID indexInfoId = condition.indexInfoId();
        return indexInfoId != null ? integrationTask.indexInfo.id.eq(indexInfoId) : null;
    }

    private BooleanExpression targetDateBetween(SyncJobQueryCondition condition) {
        LocalDate from = condition.baseDateFrom();
        LocalDate to = condition.baseDateTo();

        if (from != null && to != null) {
            return integrationTask.targetDate.between(from, to);
        }
        if (from != null) {
            return integrationTask.targetDate.goe(from);
        }
        if (to != null) {
            return integrationTask.targetDate.loe(to);
        }
        return null;
    }

    private BooleanExpression workerContains(SyncJobQueryCondition condition) {
        return condition.worker() != null
                ? integrationTask.worker.containsIgnoreCase(condition.worker())
                : null;
    }

    private BooleanExpression jobTimeBetween(SyncJobQueryCondition condition) {
        Instant from = toInstant(condition.jobTimeFrom());
        Instant to = toInclusiveInstant(condition.jobTimeTo());

        if (from != null && to != null) {
            return integrationTask.jobTime.between(from, to);
        }
        if (from != null) {
            return integrationTask.jobTime.goe(from);
        }
        if (to != null) {
            return integrationTask.jobTime.loe(to);
        }
        return null;
    }

    private BooleanExpression resultEq(SyncJobQueryCondition condition) {
        return condition.status() != null
                ? integrationTask.result.eq(condition.status().name())
                : null;
    }

    private BooleanExpression cursorCondition(SyncJobQueryCondition condition) {
        if (condition.cursor() == null || condition.idAfter() == null) {
            return null;
        }

        boolean asc = !"desc".equalsIgnoreCase(condition.sortDirection());
        return condition.sortField().buildCursorCondition(
                integrationTask,
                condition.cursor(),
                condition.idAfter(),
                asc
        );
    }

    private OrderSpecifier<?> getOrderSpecifier(SyncJobQueryCondition condition) {
        return condition.sortField().getOrderSpecifier(integrationTask, condition.sortDirection());
    }

    private OrderSpecifier<?> getIdOrderSpecifier(SyncJobQueryCondition condition) {
        boolean desc = "desc".equalsIgnoreCase(condition.sortDirection());
        return desc ? integrationTask.id.desc() : integrationTask.id.asc();
    }

    private Instant toInstant(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.atZone(KST).toInstant();
    }

    private Instant toInclusiveInstant(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }

        LocalDateTime adjustedDateTime = dateTime.toLocalTime().equals(LocalTime.MIDNIGHT)
                ? dateTime.toLocalDate().atTime(LocalTime.MAX)
                : dateTime;

        return adjustedDateTime.atZone(KST).toInstant();
    }
}