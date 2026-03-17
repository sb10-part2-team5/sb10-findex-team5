package com.sprint.findex.enums;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.sprint.findex.dto.autosyncconfig.AutoSyncConfigDto;
import com.sprint.findex.entity.QAutoSyncConfig;
import com.sprint.findex.exception.BusinessLogicException;
import com.sprint.findex.exception.ExceptionCode;
import java.util.UUID;

public enum AutoSyncConfigSortField {

    INDEX_INFO_INDEX_NAME {
        @Override
        public ComparableExpressionBase<?> getSortField(QAutoSyncConfig autoSyncConfig) {
            return autoSyncConfig.indexInfo.indexName;
        }

        @Override
        public String getCursor(AutoSyncConfigDto dto) {
            return dto.indexName();
        }

        @Override
        public BooleanExpression buildCursorCondition(
                QAutoSyncConfig autoSyncConfig,
                String cursor,
                UUID idAfter,
                boolean asc
        ) {
            return buildCursorCondition(
                    asc ? autoSyncConfig.indexInfo.indexName.gt(cursor)
                        : autoSyncConfig.indexInfo.indexName.lt(cursor),
                    autoSyncConfig.indexInfo.indexName.eq(cursor),
                    autoSyncConfig.id,
                    idAfter,
                    asc
            );
        }
    },

    ENABLED {
        @Override
        public ComparableExpressionBase<?> getSortField(QAutoSyncConfig autoSyncConfig) {
            return autoSyncConfig.enabled;
        }

        @Override
        public String getCursor(AutoSyncConfigDto dto) {
            return String.valueOf(dto.enabled());
        }

        @Override
        public BooleanExpression buildCursorCondition(
                QAutoSyncConfig autoSyncConfig,
                String cursor,
                UUID idAfter,
                boolean asc
        ) {
            boolean cursorValue = parseBooleanCursor(cursor);

            if (asc) {
                if (!cursorValue) {
                    return autoSyncConfig.enabled.isTrue()
                            .or(autoSyncConfig.enabled.isFalse()
                                    .and(autoSyncConfig.id.gt(idAfter)));
                }
                return autoSyncConfig.enabled.isTrue()
                        .and(autoSyncConfig.id.gt(idAfter));
            }

            if (cursorValue) {
                return autoSyncConfig.enabled.isFalse()
                        .or(autoSyncConfig.enabled.isTrue()
                                .and(autoSyncConfig.id.lt(idAfter)));
            }
            return autoSyncConfig.enabled.isFalse()
                    .and(autoSyncConfig.id.lt(idAfter));
        }
    };

    public abstract ComparableExpressionBase<?> getSortField(QAutoSyncConfig autoSyncConfig);

    public abstract String getCursor(AutoSyncConfigDto dto);

    public abstract BooleanExpression buildCursorCondition(
            QAutoSyncConfig autoSyncConfig,
            String cursor,
            UUID idAfter,
            boolean asc
    );

    protected BooleanExpression buildCursorCondition(
            BooleanExpression sortComparison,
            BooleanExpression sortEquality,
            ComparableExpression<UUID> idField,
            UUID idAfter,
            boolean asc
    ) {
        if (asc) {
            return sortComparison.or(sortEquality.and(idField.gt(idAfter)));
        }
        return sortComparison.or(sortEquality.and(idField.lt(idAfter)));
    }

    public static AutoSyncConfigSortField from(String value) {
        return switch (value) {
            case "indexInfo.indexName" -> INDEX_INFO_INDEX_NAME;
            case "enabled" -> ENABLED;
            default -> throw new BusinessLogicException(
                    ExceptionCode.AUTO_SYNC_CONFIG_INVALID_QUERY_CONDITION);
        };
    }

    protected boolean parseBooleanCursor(String cursor) {
        if ("true".equalsIgnoreCase(cursor)) {
            return true;
        }
        if ("false".equalsIgnoreCase(cursor)) {
            return false;
        }
        throw new BusinessLogicException(ExceptionCode.AUTO_SYNC_CONFIG_INVALID_QUERY_CONDITION);
    }
}
