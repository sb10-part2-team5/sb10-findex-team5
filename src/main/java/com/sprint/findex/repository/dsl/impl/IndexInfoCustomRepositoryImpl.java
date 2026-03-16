package com.sprint.findex.repository.dsl.impl;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.findex.dto.indexinfo.CursorPageResponseIndexInfoDto;
import com.sprint.findex.dto.indexinfo.IndexInfoDto;
import com.sprint.findex.dto.indexinfo.IndexInfoQueryCondition;
import com.sprint.findex.entity.QIndexInfo;
import com.sprint.findex.exception.BusinessLogicException;
import com.sprint.findex.exception.ExceptionCode;
import com.sprint.findex.mapper.IndexInfoMapper;
import com.sprint.findex.repository.dsl.IndexInfoCustomRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class IndexInfoCustomRepositoryImpl implements IndexInfoCustomRepository {

  private final JPAQueryFactory jpaQueryFactory;
  private final IndexInfoMapper indexInfoMapper;
  private static final QIndexInfo indexInfo = QIndexInfo.indexInfo;

  @Override
  public CursorPageResponseIndexInfoDto findAllWithIndexInfoQueryCondition(
      IndexInfoQueryCondition condition) {

    List<IndexInfoDto> content = jpaQueryFactory
        .select(Projections.constructor(
            IndexInfoDto.class,
            indexInfo.id,
            indexInfo.indexClassification,
            indexInfo.indexName,
            indexInfo.employedItemsCount,
            indexInfo.basePointInTime,
            indexInfo.baseIndex,
            indexInfo.sourceType,
            indexInfo.favorite
        ))
        .from(indexInfo)
        .where(
            classificationContains(condition.indexClassification()),
            nameContains(condition.indexName()),
            favoriteEq(condition.favorite()),
            cursorCondition(condition)
        )
        .orderBy(
            getOrderSpecifier(condition.sortField(), condition.sortDirection()),
            getOrderSpecifier("id", condition.sortDirection()))
        .limit(condition.size()+1)
        .fetch();

    boolean hasNext = content.size() > condition.size();
    String nextCursor = null;
    UUID nextIdAfter = null;

    if (hasNext) {
      nextIdAfter = content.get(condition.size()-1).id();
      nextCursor = getNextCursor(content.get(condition.size()-1), condition.sortField());
      content = content.subList(0, condition.size());
    }

    long totalElements = (condition.cursor() == null && !hasNext) ? //첫 페이지가 전부인 경우, 추가 쿼리 X
        content.size() : getTotalElements(condition);

    return new CursorPageResponseIndexInfoDto(content,nextCursor,nextIdAfter, content.size(), totalElements, hasNext);
  }

  private OrderSpecifier<?> getOrderSpecifier(String sortField, String sortDirection) {
    Order order = sortDirection.equals("desc") ? Order.DESC : Order.ASC;
    return new OrderSpecifier<>(order,getSortField(sortField));
  }

  private ComparableExpressionBase<?> getSortField(String sortField) {
    return switch (sortField) {
      case "id" -> indexInfo.id;
      case "indexName" -> indexInfo.indexName;
      case "employedItemsCount" -> indexInfo.employedItemsCount;
      case "favorite" -> indexInfo.favorite;
      case "basePointInTime" -> indexInfo.basePointInTime;
      case "baseIndex" -> indexInfo.baseIndex;
      case "indexClassification" -> indexInfo.indexClassification;
      default -> throw new BusinessLogicException(ExceptionCode.INDEX_INFO_INVALID_QUERY_CONDITION);//임시
    };
  }

  private String getNextCursor(IndexInfoDto lastDto, String sortField) {
    if(lastDto == null) {
      return null;
    }
    return switch (sortField) {
      case "id" -> lastDto.id().toString();
      case "indexName" -> lastDto.indexName();
      case "indexClassification" -> lastDto.indexClassification();
      case "favorite" -> lastDto.favorite().toString();
      case "basePointInTime" -> lastDto.basePointInTime().toString();
      case "baseIndex" -> lastDto.baseIndex().toString();
      case "employedItemsCount" -> lastDto.employedItemsCount().toString();
      default -> throw new BusinessLogicException(ExceptionCode.INDEX_INFO_INVALID_QUERY_CONDITION);
    };
  }

  private BooleanExpression classificationContains(String classification){
    return (classification != null && !classification.isBlank()) ?
        indexInfo.indexClassification.containsIgnoreCase(classification) : null;
  }

  private BooleanExpression nameContains(String name) {
    return (name != null && !name.isBlank()) ?
        indexInfo.indexName.containsIgnoreCase(name) : null;
  }

  private BooleanExpression favoriteEq(Boolean favorite) {
    return favorite != null ? indexInfo.favorite.eq(favorite) : null;
  }

  private BooleanExpression cursorCondition(IndexInfoQueryCondition condition) {
    if(condition.cursor() == null|| condition.idAfter()==null) {
      return null;
    }

    boolean asc = !condition.sortDirection().equals("desc");

    try{
      switch (condition.sortField()) {
        case "id":
          if(asc) {
            return indexInfo.id.gt(UUID.fromString(condition.cursor()));
          }else {
            return indexInfo.id.lt(UUID.fromString(condition.cursor()));
          }
        case "indexName":
          if(asc) {
            return indexInfo.indexName.gt(condition.cursor())
                .or(indexInfo.indexName.eq(condition.cursor())
                    .and(indexInfo.id.gt(condition.idAfter())));
          }else{
            return indexInfo.indexName.lt(condition.cursor())
                .or(indexInfo.indexName.eq(condition.cursor())
                    .and(indexInfo.id.lt(condition.idAfter())));
          }
        case "indexClassification":
          if(asc) {
            return indexInfo.indexClassification.gt(condition.cursor())
                .or(indexInfo.indexClassification.eq(condition.cursor())
                    .and(indexInfo.id.gt(condition.idAfter())));
          }else {
            return indexInfo.indexClassification.lt(condition.cursor())
                .or(indexInfo.indexClassification.eq(condition.cursor())
                    .and(indexInfo.id.lt(condition.idAfter())));
          }
        case "employedItemsCount":
          if(asc) {
            return indexInfo.employedItemsCount.gt(Integer.valueOf(condition.cursor()))
                .or(indexInfo.employedItemsCount.eq(Integer.valueOf(condition.cursor()))
                    .and(indexInfo.id.gt(condition.idAfter())));
          }else{
            return indexInfo.employedItemsCount.lt(Integer.valueOf(condition.cursor()))
                .or(indexInfo.employedItemsCount.eq(Integer.valueOf(condition.cursor()))
                    .and(indexInfo.id.lt(condition.idAfter())));
          }
        case "favorite":
          if(asc) {
            return indexInfo.favorite.eq(Boolean.valueOf(condition.cursor()))
                .and(indexInfo.id.gt(condition.idAfter()));
          }else {
            return indexInfo.favorite.eq(Boolean.valueOf(condition.cursor()))
                .and(indexInfo.id.lt(condition.idAfter()));
          }
        case "basePointInTime":
          if(asc) {
            return indexInfo.basePointInTime.gt(LocalDate.parse(condition.cursor()))
                .or(indexInfo.basePointInTime.eq(LocalDate.parse(condition.cursor()))
                    .and(indexInfo.id.gt(condition.idAfter())));
          }else {
            return indexInfo.basePointInTime.lt(LocalDate.parse(condition.cursor()))
                .or(indexInfo.basePointInTime.eq(LocalDate.parse(condition.cursor()))
                    .and(indexInfo.id.lt(condition.idAfter())));
          }
        case "baseIndex":
          BigDecimal cursorValue = new BigDecimal(condition.cursor());
          if(asc) {
            return indexInfo.baseIndex.gt(cursorValue)
                .or((indexInfo.baseIndex.eq(cursorValue))
                    .and(indexInfo.id.gt(condition.idAfter())));
          }else {
            return indexInfo.baseIndex.lt(cursorValue)
                .or((indexInfo.baseIndex.eq(cursorValue))
                    .and(indexInfo.id.lt(condition.idAfter())));
          }
        default:
          throw new BusinessLogicException(ExceptionCode.INDEX_INFO_INVALID_QUERY_CONDITION);//임시
      }
    }catch (NumberFormatException | DateTimeParseException e) {
      throw new BusinessLogicException(ExceptionCode.INDEX_INFO_INVALID_QUERY_CONDITION);
    }


  }

  private Long getTotalElements(IndexInfoQueryCondition condition) {
    return jpaQueryFactory
        .select(indexInfo.count())
        .from(indexInfo)
        .where(
            classificationContains(condition.indexClassification()),
            nameContains(condition.indexName()),
            favoriteEq(condition.favorite())
        ).fetchOne();
  }
}
