package com.sprint.findex.dto.sync;

import com.sprint.findex.enums.JobResult;
import com.sprint.findex.enums.JobType;
import com.sprint.findex.enums.SyncJobSortField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;

@Schema(description = "커서 기반 연동 작업 목록 조회 파라미터")
public record SyncJobQueryCondition(

        @Schema(description = "연동 작업 유형")
        JobType jobType,

        @Schema(description = "지수 정보 ID")
        UUID indexInfoId,

        @Schema(description = "대상 날짜 시작")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate baseDateFrom,

        @Schema(description = "대상 날짜 종료")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate baseDateTo,

        @Schema(description = "작업자")
        String worker,

        @Schema(description = "작업 일시 시작")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime jobTimeFrom,

        @Schema(description = "작업 일시 종료")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime jobTimeTo,

        @Schema(description = "작업 결과")
        JobResult status,

        @Schema(description = "이전 페이지 마지막 요소 ID")
        UUID idAfter,

        @Schema(description = "커서")
        String cursor,

        @Schema(description = "정렬 필드", defaultValue = "jobTime")
        SyncJobSortField sortField,

        @Schema(description = "정렬 방향", defaultValue = "desc")
        @Pattern(regexp = "(?i)(asc|desc)", message = "정렬 방향은 asc 또는 desc여야 합니다.")
        String sortDirection,

        @Schema(description = "페이지 크기", defaultValue = "10")
        @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
        @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다.")
        Integer size
) {

    public SyncJobQueryCondition {
        if (worker != null && worker.isBlank()) {
            worker = null;
        }

        if (cursor != null && cursor.isBlank()) {
            cursor = null;
        }

        if (sortField == null) {
            sortField = SyncJobSortField.jobTime;
        }

        if (sortDirection == null || sortDirection.isBlank()) {
            sortDirection = "desc";
        } else {
            sortDirection = sortDirection.toLowerCase();
        }

        if (size == null) {
            size = 10;
        }
    }
}
