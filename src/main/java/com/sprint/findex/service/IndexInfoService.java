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

  public IndexInfoDto createIndexInfoByOpenAPI(IndexInfoCreateRequest request) {
    //validateDuplicateIndexInfo(request);//이미 새것만 저장이기 때문에 생략
    IndexInfo indexInfo = createEntity(request, SourceType.OPEN_API);
    indexInfoRepository.save(indexInfo);
    autoSyncConfigService.createAutoSyncConfig(indexInfo);
    return indexInfoMapper.toDto(indexInfo); // 반환 값 필요없으면 삭제 가능
  }

  public IndexInfoDto updateIndexInfoByUser(UUID id, IndexInfoUpdateRequest request) {
    IndexInfo indexInfo = indexInfoRepository.findById(id)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.INDEX_INFO_NOT_FOUND));
    indexInfo.updateIndexInfo(request.employedItemsCount(), request.basePointInTime(),
        request.baseIndex(), request.favorite());
    return indexInfoMapper.toDto(indexInfo);
  }

  public IndexInfo updateIndexInfoByOpenAPI(IndexInfo indexInfo, IndexInfoUpdateRequest request) {
    indexInfo.updateIndexInfo(request.employedItemsCount(), request.basePointInTime(),
        request.baseIndex(), null);//즐겨찾기는 변경 없음
    return indexInfo;
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
