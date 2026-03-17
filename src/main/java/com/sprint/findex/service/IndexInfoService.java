package com.sprint.findex.service;

import com.sprint.findex.dto.indexinfo.IndexInfoCreateRequest;
import com.sprint.findex.dto.indexinfo.IndexInfoDto;
import com.sprint.findex.dto.indexinfo.IndexInfoQueryCondition;
import com.sprint.findex.dto.indexinfo.IndexInfoSummaryDto;
import com.sprint.findex.dto.indexinfo.IndexInfoUpdateRequest;
import com.sprint.findex.dto.response.PageResponse;
import com.sprint.findex.entity.IndexInfo;
import com.sprint.findex.enums.SourceType;
import com.sprint.findex.exception.BusinessLogicException;
import com.sprint.findex.exception.ExceptionCode;
import com.sprint.findex.mapper.IndexInfoMapper;
import com.sprint.findex.repository.IndexInfoRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class IndexInfoService {

  private final IndexInfoRepository indexInfoRepository;
  private final IndexInfoMapper indexInfoMapper;
  private final AutoSyncConfigService autoSyncConfigService;

  public IndexInfoDto createIndexInfoByUser(IndexInfoCreateRequest request) {
    validateDuplicateIndexInfo(request);
    IndexInfo indexInfo = createEntity(request, SourceType.USER);
    indexInfoRepository.save(indexInfo);
    autoSyncConfigService.createAutoSyncConfig(indexInfo);
    return indexInfoMapper.toDto(indexInfo);
  }

  /* openApi 지수정보를 생성 및 저장
  public IndexInfoDto createIndexInfoByOpenAPI(IndexInfoCreateRequest request) {
    validateDuplicateIndexInfo(request);
    IndexInfo indexInfo = createEntity(request, SourceType.OPEN_API);
    indexInfoRepository.save(indexInfo);
    indexInfoRepository.save(indexInfo);
    autoIntegrationRepository.save(AutoIntegration.create(indexInfo));
    return indexInfoMapper.toDto(indexInfo);
  }
   */

  public IndexInfoDto updateIndexInfoByUser(UUID id, IndexInfoUpdateRequest request) {
    IndexInfo indexInfo = indexInfoRepository.findById(id)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.INDEX_INFO_NOT_FOUND));
    indexInfo.updateIndexInfo(request.employedItemsCount(), request.basePointInTime(),
        request.baseIndex(), request.favorite());
    return indexInfoMapper.toDto(indexInfo);
  }

  public void deleteIndexInfo(UUID id) {
    if (!indexInfoRepository.existsById(id)) {
      throw new BusinessLogicException(ExceptionCode.INDEX_INFO_NOT_FOUND);
    }
    indexInfoRepository.deleteById(id);
  }

  @Transactional(readOnly = true)
  public PageResponse<IndexInfoDto> getIndexInfoList(IndexInfoQueryCondition condition) {
    return indexInfoRepository.findAllWithIndexInfoQueryCondition(condition);
  }

  @Transactional(readOnly = true)
  public List<IndexInfoSummaryDto> getSummaries(){
    return indexInfoRepository.findAllSummaries();
  }

  private void validateDuplicateIndexInfo(IndexInfoCreateRequest request) {
    if (indexInfoRepository.existsByIndexClassificationAndIndexName(
        request.indexClassification(),
        request.indexName())) {
      throw new BusinessLogicException(ExceptionCode.INDEX_INFO_ALREADY_EXISTS);
    }
  }

  private IndexInfo createEntity(IndexInfoCreateRequest request, SourceType sourceType) {
    return IndexInfo.create(request.indexName(), request.indexClassification(),
        request.employedItemsCount(), request.basePointInTime(), request.baseIndex(),
        sourceType,
        request.favorite());

  }
}
