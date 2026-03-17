package com.sprint.findex.service;

import com.sprint.findex.dto.sync.IndexDataSyncSource;
import com.sprint.findex.dto.sync.SyncJobDto;
import com.sprint.findex.entity.IndexData;
import com.sprint.findex.entity.IndexInfo;
import com.sprint.findex.entity.IntegrationTask;
import com.sprint.findex.enums.JobResult;
import com.sprint.findex.enums.JobType;
import com.sprint.findex.enums.SourceType;
import com.sprint.findex.mapper.SyncJobMapper;
import com.sprint.findex.repository.IndexDataRepository;
import com.sprint.findex.repository.IntegrationTaskRepository;
import java.time.Instant;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IndexDataSyncService {

    private final IndexDataRepository indexDataRepository;
    private final IntegrationTaskRepository integrationTaskRepository;
    private final SyncJobMapper syncJobMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SyncJobDto syncSingleItem(
            IndexInfo indexInfo,
            LocalDate targetDate,
            IndexDataSyncSource source,
            IndexData existingIndexData,
            String worker,
            Instant jobTime
    ) {
        updateOrCreateIndexData(indexInfo, source, existingIndexData);

        IntegrationTask succeedTask = integrationTaskRepository.save(
                IntegrationTask.create(
                        indexInfo,
                        JobType.INDEX_DATA.name(),
                        targetDate,
                        worker,
                        jobTime,
                        JobResult.SUCCESS.name()
                )
        );

        return syncJobMapper.toDto(succeedTask);
    }

    private IndexData updateOrCreateIndexData(
            IndexInfo indexInfo,
            IndexDataSyncSource source,
            IndexData existingIndexData
    ) {
        if (existingIndexData != null) {
            existingIndexData.update(
                    source.marketPrice(),
                    source.closingPrice(),
                    source.highPrice(),
                    source.lowPrice(),
                    source.versus(),
                    source.fluctuationRate(),
                    source.tradingQuantity(),
                    source.tradingPrice(),
                    source.marketTotalAmount()
            );
            return existingIndexData;
        }

        return indexDataRepository.save(
                IndexData.create(
                        indexInfo,
                        source.baseDate(),
                        SourceType.OPEN_API,
                        source.marketPrice(),
                        source.closingPrice(),
                        source.highPrice(),
                        source.lowPrice(),
                        source.versus(),
                        source.fluctuationRate(),
                        source.tradingQuantity(),
                        source.tradingPrice(),
                        source.marketTotalAmount()
                )
        );
    }
}
