package com.sprint.findex.dto.indexinfo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

@Schema(description = "커서 기반 페이지 응답")
public record CursorPageResponseIndexInfoDto(
    @Schema(description = "페이지 내용")
    List<IndexInfoDto> content,

    @Schema(description = "다음 페이지 커서", example = "KOSPI시리즈")
    String nextCursor,

    @Schema(description = "마지막 요소의 ID", example = "31f6a8b9-cc2c-4c20-b43a-1c96881fafd5")
    UUID nextIdAfter,

    @Schema(description = "페이지 크기", example = "10")
    Integer size,

    @Schema(description = "총 요소 수", example = "100")
    Long totalElements,

    @Schema(description = "다음 페이지 여부", example = "true")
    Boolean hasNext
) {

}
