package com.sprint.findex.service;

import com.sprint.findex.dto.sync.IndexInfoLookup;
import com.sprint.findex.dto.sync.IndexInfoSyncSource;
import com.sprint.findex.dto.sync.SyncJobDto;
import com.sprint.findex.entity.IndexInfo;
import com.sprint.findex.entity.IntegrationTask;
import com.sprint.findex.enums.JobResult;
import com.sprint.findex.enums.JobType;
import com.sprint.findex.enums.SourceType;
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
        // 신규 지수 정보라면 생성
        if (lookup == null) {
            // 동일한 지수 이름으로 USER 타입의 데이터가 이미 존재하는지 확인
            boolean existsUserConflict = indexInfoRepository.existsByIndexClassificationAndIndexNameAndSourceType(
                    source.indexClassification(),
                    standardName,
                    SourceType.USER
            );

            // 동일한 지수 이름으로 USER 타입의 데이터가 이미 존재하는 경우
            if (existsUserConflict) {
                // 예외 개선 필요 - 같은 분류, 같은 지수 이름으로 이미 데이터가 존재함
                throw new RuntimeException();
            }

            // DB에 존재하지 않는 신규 지수 정보라면 생성
            return indexInfoRepository.save(
                    IndexInfo.create(
                            standardName,
                            source.indexClassification(),
                            source.employedItemsCount(),
                            source.basePointInTime(),
                            source.baseIndex(),
                            SourceType.OPEN_API,
                            false
                    )
            );
        }

        // 기존 IndexInfo 데이터 최신화
        IndexInfo existing = indexInfoRepository.getReferenceById(lookup.id());
        existing.updateIndexInfo(
                source.employedItemsCount(),
                source.basePointInTime(),
                source.baseIndex(),
                existing.getFavorite()
        );

        return existing;
    }
}
