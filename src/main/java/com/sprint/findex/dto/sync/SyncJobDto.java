package com.sprint.findex.dto.sync;

import com.sprint.findex.enums.JobResult;
import com.sprint.findex.enums.JobType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record SyncJobDto(
    UUID id,
    JobType jobType,
    UUID indexInfoId,
    LocalDate targetDate,
    String worker,
    LocalDateTime jobTime,
    JobResult result
) {

}
