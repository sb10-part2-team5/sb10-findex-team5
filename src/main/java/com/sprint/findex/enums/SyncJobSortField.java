package com.sprint.findex.enums;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.sprint.findex.entity.IntegrationTask;
import com.sprint.findex.entity.QIntegrationTask;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.UUID;

public enum SyncJobSortField {
    targetDate {
        @Override
        public String getCursor(IntegrationTask integrationTask) {
            return integrationTask.getTargetDate() == null
                    ? NULL_CURSOR
                    : integrationTask.getTargetDate().toString();
        }

        @Override
        public BooleanExpression buildCursorCondition(
                QIntegrationTask integrationTask,
                String cursor,
                UUID idAfter,
                boolean asc
        ) {
            if (NULL_CURSOR.equals(cursor)) {
                BooleanExpression idCondition = asc
                        ? integrationTask.id.gt(idAfter)
                        : integrationTask.id.lt(idAfter);
                return integrationTask.targetDate.isNull().and(idCondition);
            }

            try {
                LocalDate cursorValue = LocalDate.parse(cursor);
                BooleanExpression comparison = asc
                        ? integrationTask.targetDate.gt(cursorValue)
                        : integrationTask.targetDate.lt(cursorValue);
                BooleanExpression equality = integrationTask.targetDate.eq(cursorValue);

                return buildBaseCondition(comparison, equality, integrationTask.id, idAfter, asc)
                        .or(integrationTask.targetDate.isNull());
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("유효하지 않은 targetDate cursor 입니다: " + cursor, e);
            }
        }
    },
    jobTime {
        @Override
        public String getCursor(IntegrationTask integrationTask) {
            return integrationTask.getJobTime().toString();
        }

        @Override
        public BooleanExpression buildCursorCondition(
                QIntegrationTask integrationTask,
                String cursor,
                UUID idAfter,
                boolean asc
        ) {
            try {
                Instant cursorValue = Instant.parse(cursor);
                BooleanExpression comparison = asc
                        ? integrationTask.jobTime.gt(cursorValue)
                        : integrationTask.jobTime.lt(cursorValue);
                BooleanExpression equality = integrationTask.jobTime.eq(cursorValue);

                return buildBaseCondition(comparison, equality, integrationTask.id, idAfter, asc);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("유효하지 않은 jobTime cursor 입니다: " + cursor, e);
            }
        }
    };

    private static final String NULL_CURSOR = "__NULL__";

    public OrderSpecifier<?> getOrderSpecifier(QIntegrationTask integrationTask, String sortDirection) {
        Order order = toOrder(sortDirection);

        return switch (this) {
            case targetDate -> new OrderSpecifier<>(order, integrationTask.targetDate).nullsLast();
            case jobTime -> new OrderSpecifier<>(order, integrationTask.jobTime);
        };
    }

    public abstract String getCursor(IntegrationTask integrationTask);

    public abstract BooleanExpression buildCursorCondition(
            QIntegrationTask integrationTask,
            String cursor,
            UUID idAfter,
            boolean asc
    );

    protected BooleanExpression buildBaseCondition(
            BooleanExpression comparison,
            BooleanExpression equality,
            com.querydsl.core.types.dsl.ComparableExpression<UUID> idField,
            UUID idAfter,
            boolean asc
    ) {
        if (asc) {
            return comparison.or(equality.and(idField.gt(idAfter)));
        }
        return comparison.or(equality.and(idField.lt(idAfter)));
    }

    protected Order toOrder(String sortDirection) {
        return "desc".equalsIgnoreCase(sortDirection) ? Order.DESC : Order.ASC;
    }
}