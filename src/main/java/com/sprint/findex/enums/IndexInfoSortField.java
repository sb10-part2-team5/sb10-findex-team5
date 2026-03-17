package com.sprint.findex.enums;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.sprint.findex.dto.indexinfo.IndexInfoDto;
import com.sprint.findex.entity.QIndexInfo;
import com.sprint.findex.exception.BusinessLogicException;
import com.sprint.findex.exception.ExceptionCode;
import java.util.UUID;

public enum IndexInfoSortField {
  indexClassification {
    @Override
    public ComparableExpressionBase<?> getSortField(QIndexInfo indexInfo) {
      return indexInfo.indexClassification;
    }

    @Override
    public String getCursor(IndexInfoDto dto) {
      return dto.indexClassification();
    }

    @Override
    public BooleanExpression buildCursorCondition(
        QIndexInfo indexInfo,
        String cursor,
        UUID idAfter,
        boolean asc
    ) {
      return buildCursorCondition(
          asc ? indexInfo.indexClassification.gt(cursor) : indexInfo.indexClassification.lt(cursor),
          indexInfo.indexClassification.eq(cursor),
          indexInfo.id,
          idAfter,
          asc
      );
    }
  },
  indexName {
    @Override
    public ComparableExpressionBase<?> getSortField(QIndexInfo indexInfo) {
      return indexInfo.indexName;
    }

    @Override
    public String getCursor(IndexInfoDto dto) {
      return dto.indexName();
    }

    @Override
    public BooleanExpression buildCursorCondition(
        QIndexInfo indexInfo,
        String cursor,
        UUID idAfter,
        boolean asc
    ) {
      return buildCursorCondition(
          asc ? indexInfo.indexName.gt(cursor) : indexInfo.indexName.lt(cursor),
          indexInfo.indexName.eq(cursor),
          indexInfo.id,
          idAfter,
          asc
      );
    }
  },
  employedItemsCount {
    @Override
    public ComparableExpressionBase<?> getSortField(QIndexInfo indexInfo) {
      return indexInfo.employedItemsCount;
    }

    @Override
    public String getCursor(IndexInfoDto dto) {
      return dto.employedItemsCount().toString();
    }

    @Override
    public BooleanExpression buildCursorCondition(
        QIndexInfo indexInfo,
        String cursor,
        UUID idAfter,
        boolean asc
    ) {
      try {
        Integer cursorValue = Integer.valueOf(cursor);
        return buildCursorCondition(
            asc
                ? indexInfo.employedItemsCount.gt(cursorValue)
                : indexInfo.employedItemsCount.lt(cursorValue),
            indexInfo.employedItemsCount.eq(cursorValue),
            indexInfo.id,
            idAfter,
            asc
        );
      } catch (NumberFormatException e) {
        throw new BusinessLogicException(ExceptionCode.INDEX_INFO_INVALID_QUERY_CONDITION);
      }
    }
  };

  public abstract ComparableExpressionBase<?> getSortField(QIndexInfo indexInfo);

  public abstract String getCursor(IndexInfoDto dto);

  public abstract BooleanExpression buildCursorCondition(
      QIndexInfo indexInfo,
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
}
