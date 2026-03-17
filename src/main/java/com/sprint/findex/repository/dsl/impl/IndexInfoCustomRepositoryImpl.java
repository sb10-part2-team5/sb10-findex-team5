package com.sprint.findex.repository.dsl.impl;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.findex.dto.indexinfo.IndexInfoDto;
import com.sprint.findex.dto.indexinfo.IndexInfoQueryCondition;
import com.sprint.findex.dto.response.PageResponse;
import com.sprint.findex.entity.QIndexInfo;
import com.sprint.findex.enums.IndexInfoSortField;
import com.sprint.findex.repository.dsl.IndexInfoCustomRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class IndexInfoCustomRepositoryImpl implements IndexInfoCustomRepository {

  private final JPAQueryFactory jpaQueryFactory;
  private static final QIndexInfo indexInfo = QIndexInfo.indexInfo;

  @Override
  public PageResponse<IndexInfoDto> findAllWithIndexInfoQueryCondition(
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
            getIdOrderSpecifier(condition.sortDirection()))
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

    return new PageResponse(content,nextCursor,nextIdAfter, content.size(), totalElements, hasNext);
  }

  private OrderSpecifier<?> getOrderSpecifier(IndexInfoSortField sortField, String sortDirection) {
    Order order = sortDirection.equals("desc") ? Order.DESC : Order.ASC;
    return new OrderSpecifier<>(order, sortField.getSortField(indexInfo));
  }

  private OrderSpecifier<?> getIdOrderSpecifier(String sortDirection) {
    Order order = sortDirection.equals("desc") ? Order.DESC : Order.ASC;
    return new OrderSpecifier<>(order, indexInfo.id);
  }

  private String getNextCursor(IndexInfoDto lastDto, IndexInfoSortField sortField) {
    if(lastDto == null) {
      return null;
    }
    return sortField.getCursor(lastDto);
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
    return condition.sortField()
        .buildCursorCondition(indexInfo, condition.cursor(), condition.idAfter(), asc);
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
