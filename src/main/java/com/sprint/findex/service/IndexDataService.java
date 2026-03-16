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
import com.sprint.findex.util.SortUtils;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
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

    @Transactional
    public void delete(UUID indexDataId) {
        IndexData indexData = indexDataRepository.findById(indexDataId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.INDEX_DATA_NOT_FOUND));

        indexDataRepository.delete(indexData);
    }

    public Resource export(UUID indexInfoId, LocalDate startDate, LocalDate endDate,
            String sortField,
            String sortDirection) {
        validateDateRange(startDate, endDate);
        StringBuilder builder = new StringBuilder();

        builder.append("기준일자,시가,종가,고가,저가,전일 대비 등락,전일 대비 등락률,거래량,거래대금,시가총액\n");

        Sort sort = Sort.by(SortUtils.directionOf(sortDirection), sortField);
        List<IndexData> rows = indexDataRepository.findAllForExport(
                indexInfoId,
                startDate,
                endDate,
                sort
        );

        for (IndexData row : rows) {
            builder.append(row.getBaseDate()).append(',')
                    .append(row.getSourceType()).append(',')
                    .append(row.getMarketPrice()).append(',')
                    .append(row.getClosingPrice()).append(',')
                    .append(row.getHighPrice()).append(',')
                    .append(row.getLowPrice()).append(',')
                    .append(row.getVersus()).append(',')
                    .append(row.getFluctuationRate()).append(',')
                    .append(row.getTradingQuantity()).append(',')
                    .append(row.getTradingPrice()).append(',')
                    .append(row.getMarketTotalAmount())
                    .append('\n');
        }
        return new ByteArrayResource(builder.toString().getBytes(StandardCharsets.UTF_8));

    }

    private void validateDuplicate(UUID indexInfoId,
            LocalDate baseDate) { // 지수 정보 + 기준일자가 이미 존재하는지 확인
        if (indexDataRepository.existsByIndexInfo_IdAndBaseDate(indexInfoId, baseDate)) {
            throw new BusinessLogicException(ExceptionCode.INDEX_DATA_ALREADY_EXISTS);
        }
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) { // 시작일이 종료일보다 늦는지 확인
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new BusinessLogicException(ExceptionCode.INVALID_DATE_RANGE);
        }
    }
}
