package com.sprint.findex.dto.sync;

import com.sprint.findex.enums.JobResult;
import com.sprint.findex.enums.JobType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "연동 작업 DTO")
public record SyncJobDto(
        @Schema(description = "연동 작업 ID", example = "4f737d57-e22e-4e67-86b6-003c6e9e34dc")
        UUID id,

        @Schema(description = "연동 작업 유형", example = "INDEX_INFO")
        JobType jobType,

        @Schema(description = "지수 정보 ID", example = "847a6994-1eba-4fa4-8de5-e7269d58dfbf")
        UUID indexInfoId,

        @Schema(description = "대상 날짜", example = "2023-01-01")
        LocalDate targetDate,

        @Schema(description = "작업자", example = "192.168.0.1")
        String worker,

        @Schema(description = "작업 시각", example = "2023-01-01T12:00:00")
        LocalDateTime jobTime,

        @Schema(description = "작업 결과", example = "SUCCESS")
        JobResult result
) {
}