package com.sprint.findex.service;

import com.sprint.findex.dto.indexdata.IndexDataCreateRequest;
import com.sprint.findex.dto.indexdata.IndexDataDto;
import com.sprint.findex.dto.indexdata.IndexDataUpdateRequest;
import com.sprint.findex.entity.IndexData;
import com.sprint.findex.entity.IndexInfo;
import com.sprint.findex.enums.SourceType;
import com.sprint.findex.exception.BusinessLogicException;
import com.sprint.findex.exception.ExceptionCode;
import com.sprint.findex.mapper.IndexDataMapper;
import com.sprint.findex.repository.IndexDataRepository;
import com.sprint.findex.repository.IndexInfoRepository;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IndexDataService {

    private final IndexDataRepository indexDataRepository;
    private final IndexDataMapper indexDataMapper;
    private final IndexInfoRepository indexInfoRepository;

    @Transactional
    public IndexDataDto save(IndexDataCreateRequest request) {
        IndexInfo indexInfo = indexInfoRepository.findById(request.indexInfoId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.INDEX_INFO_NOT_FOUND));

        validateDuplicate(request.indexInfoId(), request.baseDate());

        IndexData indexData = IndexData.create(
                indexInfo,
                request.baseDate(),
                SourceType.USER,
                request.marketPrice(),
                request.closingPrice(),
                request.highPrice(),
                request.lowPrice(),
                request.versus(),
                request.fluctuationRate(),
                request.tradingQuantity(),
                request.tradingPrice(),
                request.marketTotalAmount()
        );

        return indexDataMapper.toDto(indexDataRepository.save(indexData));
    }

    @Transactional
    public IndexDataDto update(UUID indexDataId, IndexDataUpdateRequest request) {
        IndexData indexData = indexDataRepository.findById(indexDataId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.INDEX_DATA_NOT_FOUND));

        indexData.update(
                request.marketPrice(),
                request.closingPrice(),
                request.highPrice(),
                request.lowPrice(),
                request.versus(),
                request.fluctuationRate(),
                request.tradingQuantity(),
                request.tradingPrice(),
                request.marketTotalAmount()
        );

        return indexDataMapper.toDto(indexData);
    }

    private void validateDuplicate(UUID indexInfoId,
            LocalDate baseDate) { // 지수 정보 + 기준일자가 이미 존재하는지 확인
        if (indexDataRepository.existsByIndexInfo_IdAndBaseDate(indexInfoId, baseDate)) {
            throw new BusinessLogicException(ExceptionCode.INDEX_DATA_ALREADY_EXISTS);
        }
    }


}
