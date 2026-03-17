package com.sprint.findex.dto.indexinfo;

import com.sprint.findex.enums.IndexInfoSortField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;

@Schema(description = "커서 기반 지수 정보 목록 조회 파라미터 목록")
public record IndexInfoQueryCondition(

    @Schema(description = "지수 분류명")
    String indexClassification,

    @Schema(description = "지수명")
    String indexName,

    @Schema(description = "즐겨찾기 여부")
    Boolean favorite,

    @Schema(description = "이전 페이지 마지막 요소 ID")
    UUID idAfter,

    @Schema(description = "커서 (다음 페이지 시작점)")
    String cursor,

    @Schema(description = "정렬 필드( indexClassification, indexName, employedItemsCount)",
        defaultValue = "indexClassification")
    IndexInfoSortField sortField,

    @Schema(description = "정렬 방향 (asc, desc)", defaultValue = "asc")
    @Pattern(regexp = "(?i)(asc|desc)")
    String sortDirection,

    @Schema(description = "페이지 크기", defaultValue = "10")
    @Min(1)
    @Max(100)
    Integer size
) {

  public IndexInfoQueryCondition {
    if (size == null) {
      size = 10;
    }
    if (sortField == null) {
      sortField = IndexInfoSortField.indexClassification;
    }
    if (sortDirection == null || sortDirection.isBlank()) {
      sortDirection = "asc";
    }else {
      sortDirection = sortDirection.toLowerCase();
    }
  }
}