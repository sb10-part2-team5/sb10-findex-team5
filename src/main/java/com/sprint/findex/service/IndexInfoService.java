package com.sprint.findex.service;

import com.sprint.findex.dto.indexinfo.IndexInfoCreateRequest;
import com.sprint.findex.dto.indexinfo.IndexInfoDto;
import com.sprint.findex.entity.IndexInfo;
import com.sprint.findex.enums.SourceType;
import com.sprint.findex.exception.BusinessLogicException;
import com.sprint.findex.exception.ExceptionCode;
import com.sprint.findex.mapper.IndexInfoMapper;
import com.sprint.findex.repository.IndexInfoRepository;
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

  private void validateDuplicateIndexInfo(IndexInfoCreateRequest request) {
    if (indexInfoRepository.existsByIndexClassificationAndIndexName(request.indexClassification(),
        request.indexName())) {
      throw new BusinessLogicException(ExceptionCode.INDEX_INFO_ALREADY_EXISTS);
    }
  }

  private IndexInfo createEntity(IndexInfoCreateRequest request, SourceType sourceType) {
    return IndexInfo.create(request.indexName(), request.indexClassification(),
        request.employedItemsCount(), request.basePointInTime(), request.baseIndex(), sourceType,
        request.favorite());

  }
}
