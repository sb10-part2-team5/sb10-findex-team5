package com.sprint.findex.service;

import com.sprint.findex.dto.openapi.MarketIndexApiResponse.Item;
import com.sprint.findex.dto.sync.IndexInfoSyncSource;
import com.sprint.findex.dto.sync.SyncJobDto;
import com.sprint.findex.entity.IndexInfo;
import com.sprint.findex.entity.IntegrationTask;
import com.sprint.findex.enums.JobResult;
import com.sprint.findex.enums.JobType;
import com.sprint.findex.mapper.MarketIndexApiSyncMapper;
import com.sprint.findex.mapper.SyncJobMapper;
import com.sprint.findex.repository.IndexInfoRepository;
import com.sprint.findex.repository.IntegrationTaskRepository;
import com.sprint.findex.util.IndexNameResolver;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IndexInfoSyncFailureService {

    private final IndexInfoRepository indexInfoRepository;
    private final IntegrationTaskRepository integrationTaskRepository;
    private final MarketIndexApiSyncMapper marketIndexApiSyncMapper;
    private final SyncJobMapper syncJobMapper;
    private final IndexNameResolver indexNameResolver;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SyncJobDto saveFailure(Item item, String worker, Instant jobTime, Exception e) {
        // Open API 응답 데이터를 IndexInfoSyncSource 객체로 변환
        IndexInfoSyncSource source = marketIndexApiSyncMapper.toSource(item);

        // 변경된 지수명에 해당할 수 있으므로 확인
        String standardName = indexNameResolver.resolveStandardName(
                source.indexClassification(),
                source.indexName()
        );

        // 기존 IndexInfo가 존재하는지 조회
        Optional<IndexInfo> existingOptional = findExistingIndexInfo(
                source.indexClassification(),
                standardName
        );

        // 기존 IndexInfo가 없다는 건 신규 지수 정보라는 의미
        // 정책 변경 혹은 개선 필요
        if (existingOptional.isEmpty()) {
            return null;
        }

        // 연동 실패 이력 저장
        IntegrationTask failedTask = integrationTaskRepository.save(
                IntegrationTask.create(
                        existingOptional.get(),
                        JobType.INDEX_INFO.name(),
                        null,
                        worker,
                        jobTime,
                        JobResult.FAILED.name(),
                        // 실패 메시지 개선 필요
                        e.getMessage()
                )
        );

        return syncJobMapper.toDto(failedTask);
    }

    private Optional<IndexInfo> findExistingIndexInfo(String indexClassification,
            String standardName) {
        // 조회 가능한 이름 목록 생성
        List<String> searchNames = indexNameResolver.buildSearchNames(indexClassification,
                standardName);

        // 이름 후보를 순서대로 조회
        for (String name : searchNames) {
            Optional<IndexInfo> found = indexInfoRepository.findByIndexClassificationAndIndexName(
                    indexClassification,
                    name
            );

            if (found.isPresent()) {
                return found;
            }
        }

        return Optional.empty();
    }
}
