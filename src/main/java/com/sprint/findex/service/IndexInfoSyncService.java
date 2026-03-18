package com.sprint.findex.service;

import com.sprint.findex.dto.indexinfo.IndexInfoCreateRequest;
import com.sprint.findex.dto.indexinfo.IndexInfoUpdateRequest;
import com.sprint.findex.dto.sync.IndexInfoLookup;
import com.sprint.findex.dto.sync.IndexInfoSyncSource;
import com.sprint.findex.dto.sync.SyncJobDto;
import com.sprint.findex.entity.IndexInfo;
import com.sprint.findex.entity.IntegrationTask;
import com.sprint.findex.enums.JobResult;
import com.sprint.findex.enums.JobType;
import com.sprint.findex.enums.SourceType;
import com.sprint.findex.exception.BusinessLogicException;
import com.sprint.findex.exception.ExceptionCode;
import com.sprint.findex.mapper.SyncJobMapper;
import com.sprint.findex.repository.IndexInfoRepository;
import com.sprint.findex.repository.IntegrationTaskRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IndexInfoSyncService {

    private final IndexInfoService indexInfoService;
    private final IndexInfoRepository indexInfoRepository;
    private final IntegrationTaskRepository integrationTaskRepository;
    private final SyncJobMapper syncJobMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SyncJobDto syncSingleItem(
            IndexInfoSyncSource source,
            String standardName,
            IndexInfoLookup lookup,
            String worker,
            Instant jobTime
    ) {
        // IndexInfo 업데이트 또는 생성
        IndexInfo indexInfo = updateOrCreateIndexInfo(source, standardName, lookup);

        // 연동 성공 이력 저장
        IntegrationTask succeedTask = integrationTaskRepository.save(
                IntegrationTask.create(
                        indexInfo,
                        JobType.INDEX_INFO.name(),
                        null,
                        worker,
                        jobTime,
                        JobResult.SUCCESS.name()
                )
        );

        return syncJobMapper.toDto(succeedTask);
    }

    private IndexInfo updateOrCreateIndexInfo(
            IndexInfoSyncSource source,
            String standardName,
            IndexInfoLookup lookup
    ) {
        // lookup은 OPEN_API 데이터만 대상으로 구성되기 때문에 lookup == null은 OPEN_API 데이터가 없음을 의미
        if (lookup == null) {
            // 동일한 지수 이름으로 USER 타입의 데이터가 이미 존재하는지 확인
            // 지수 이름은 변경된 지수명일 수 있으므로 standardName 사용
            boolean existsUserConflict = indexInfoRepository.existsByIndexClassificationAndIndexNameAndSourceType(
                    source.indexClassification(),
                    standardName,
                    SourceType.USER
            );

            // 동일한 지수 이름으로 USER 타입의 데이터가 이미 존재하는 경우
            if (existsUserConflict) {
                throw new BusinessLogicException(ExceptionCode.INDEX_INFO_ALREADY_EXISTS);
            }

            IndexInfoCreateRequest createRequest = new IndexInfoCreateRequest(
                    source.indexClassification(),
                    standardName,
                    source.employedItemsCount(),
                    source.basePointInTime(),
                    source.baseIndex(),
                    null
            );

            // IndexInfoService를 통해 신규 지수 생성/저장
            return indexInfoService.createIndexInfoByOpenAPI(createRequest);
        }

        IndexInfoUpdateRequest updateRequest = new IndexInfoUpdateRequest(
                source.employedItemsCount(),
                source.basePointInTime(),
                source.baseIndex(),
                null
        );

        IndexInfo existing = indexInfoRepository.getReferenceById(lookup.id());
        // IndexInfoService를 통해 지수 정보 업데이트
        return indexInfoService.updateIndexInfoByOpenAPI(existing, updateRequest);
    }
}
