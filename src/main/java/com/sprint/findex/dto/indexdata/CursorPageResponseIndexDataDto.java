package com.sprint.findex.dto.indexdata;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

@Schema(description = "커서 기반 페이지 응답")
public record CursorPageResponseIndexDataDto(
        @Schema(description = "페이지 내용", contains = IndexDataDto.class)
        List<IndexDataDto> content,

        @Schema(description = "다음 페이지 커서", example = "2023-01-01")
        String nextCursor,

        @Schema(description = "마지막 요소의 ID", example = "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d")
        UUID nextIdAfter,

        @Schema(description = "페이지 크기", example = "10")
        int size,

        @Schema(description = "총 요소 수", example = "100")
        long totalElements,

        @Schema(description = "다음 페이지 여부", example = "true")
        boolean hasNext
) {

}
