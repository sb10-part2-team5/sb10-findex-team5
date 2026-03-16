package com.sprint.findex.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

@Schema(description = "페이지네이션 응답 DTO")
public record PageResponse<T>(
        @Schema(description = "실제 데이터")
        List<T> content,

        @Schema(description = "다음 페이지 커서")
        String nextCursor,

        @Schema(
                description = "마지막 요소 ID",
                example = "123e4567-e89b-12d3-a456-426614174000"
        )
        UUID nextIdAfter,

        @Schema(description = "실제 반환된 데이터 수", example = "10")
        int size,

        @Schema(description = "전체 데이터 수", example = "100")
        Long totalElements,

        @Schema(description = "다음 페이지 존재 여부", example = "true")
        boolean hasNext
) {

}
