package com.sprint.findex.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.sprint.findex.dto.indexinfo.IndexInfoCreateRequest;
import com.sprint.findex.dto.indexinfo.IndexInfoDto;
import com.sprint.findex.dto.indexinfo.IndexInfoQueryCondition;
import com.sprint.findex.dto.indexinfo.IndexInfoSummaryDto;
import com.sprint.findex.dto.indexinfo.IndexInfoUpdateRequest;
import com.sprint.findex.dto.response.PageResponse;
import com.sprint.findex.entity.AutoSyncConfig;
import com.sprint.findex.entity.IndexData;
import com.sprint.findex.entity.IndexInfo;
import com.sprint.findex.enums.IndexInfoSortField;
import com.sprint.findex.enums.SourceType;
import com.sprint.findex.exception.BusinessLogicException;
import com.sprint.findex.exception.ExceptionCode;
import com.sprint.findex.repository.AutoSyncConfigRepository;
import com.sprint.findex.repository.IndexDataRepository;
import com.sprint.findex.repository.IndexInfoRepository;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class IndexInfoServiceTest {

  @Autowired
  private IndexInfoService indexInfoService;

  @Autowired
  private IndexInfoRepository indexInfoRepository;

  @Autowired
  private AutoSyncConfigRepository autoSyncConfigRepository;

  @Autowired
  private IndexDataRepository indexDataRepository;

  @Autowired
  private EntityManager em;

  @Test
  @DisplayName("성공: 지수 정보 생성 시 DB 저장 및 자동 연동 설정 동시 생성 확인")
  void createIndexInfo_Success_WithAutoSyncConfig() {
    IndexInfoCreateRequest request = new IndexInfoCreateRequest(
        "KOSPI200", "Test", 200, LocalDate.of(2024, 1, 1),
        new BigDecimal("2500.123456"), true
    );

    IndexInfoDto result = indexInfoService.createIndexInfoByUser(request);
    em.flush();
    em.clear();

    IndexInfo savedIndex = indexInfoRepository.findById(result.id()).orElseThrow();
    assertThat(savedIndex.getIndexClassification()).isEqualTo("KOSPI200");
    assertThat(savedIndex.getIndexName()).isEqualTo("Test");
    assertThat(savedIndex.getEmployedItemsCount()).isEqualTo(200);
    assertThat(savedIndex.getBasePointInTime()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(savedIndex.getBaseIndex()).isEqualTo(new BigDecimal("2500.1235")); // 4자리까지 반올림
    assertThat(savedIndex.getFavorite()).isTrue();

    List<AutoSyncConfig> configs = autoSyncConfigRepository.findAll();
    long matchCount = configs.stream()
        .filter(c -> c.getIndexInfo().getId().equals(savedIndex.getId()))
        .count();

    assertThat(matchCount).isEqualTo(1);
  }

  @Test
  @DisplayName("실패: 동일한 분류와 이름을 가진 지수 정보 생성 시 예외 발생")
  void createIndexInfo_Duplicate_Exception() {
    IndexInfoCreateRequest request = new IndexInfoCreateRequest(
        "Same Name", "Same", 100,
        LocalDate.now(), new BigDecimal("2500"), true
    );
    indexInfoService.createIndexInfoByUser(request); // 첫 번째 저장
    em.flush();
    em.clear();

    BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> {
      indexInfoService.createIndexInfoByUser(request); // 중복 저장
    });
    assertThat(exception.getExceptionCode()).isEqualTo(ExceptionCode.INDEX_INFO_ALREADY_EXISTS);
  }

  @Test
  @DisplayName("성공: 지수 정보 수정 - 일부 필드 수정 및 기존 값 유지 확인")
  void updateIndexInfo_Success_Integration() {
    IndexInfoCreateRequest createRequest = new IndexInfoCreateRequest(
        "Test", "Test Name", 100, LocalDate.of(2024, 1, 1),
        new BigDecimal("4000.12344"), true
    );
    IndexInfoDto savedDto = indexInfoService.createIndexInfoByUser(createRequest);
    UUID id = savedDto.id();
    em.flush();
    em.clear();

    IndexInfoUpdateRequest updateRequest = new IndexInfoUpdateRequest(
        101,              // 수정함
        null,             // 수정 안 함 (null)
        new BigDecimal("5000.12344"), // 수정함 (반올림 대상)
        null              // 수정 안 함 (null)
    );

    indexInfoService.updateIndexInfoByUser(id, updateRequest);

    IndexInfo updatedEntity = indexInfoRepository.findById(id).orElseThrow();
    assertThat(updatedEntity.getEmployedItemsCount()).isEqualTo(101);
    assertThat(updatedEntity.getBaseIndex()).isEqualTo(new BigDecimal("5000.1234"));
    //변경 안 한 값 확인
    assertThat(updatedEntity.getBasePointInTime()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(updatedEntity.getFavorite()).isTrue();
  }

  @Test
  @DisplayName("실패: 존재하지 않는 ID로 수정 시도 시 예외 발생")
  void updateIndexInfo_Fail_NotFound() {
    UUID fakeId = UUID.randomUUID();
    IndexInfoUpdateRequest updateRequest = new IndexInfoUpdateRequest(
        100, LocalDate.now(), BigDecimal.valueOf(200), false
    );

    BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> {
      indexInfoService.updateIndexInfoByUser(fakeId, updateRequest);
    });

    assertThat(exception.getExceptionCode()).isEqualTo(ExceptionCode.INDEX_INFO_NOT_FOUND);
  }

  @Test
  @DisplayName("성공: 지수 정보 삭제 시 연관된 데이터 및 자동 연동 설정도 함께 삭제")
  void deleteIndexInfo_WithAutoSyncConfig_Integration() {
    IndexInfoCreateRequest request = new IndexInfoCreateRequest(
        "Test", "Test Name", 100,
        LocalDate.now(), BigDecimal.TEN, false
    );
    IndexInfoDto savedIndex = indexInfoService.createIndexInfoByUser(request);
    UUID indexId = savedIndex.id();
    IndexInfo indexInfo = indexInfoRepository.findById(indexId).orElse(null);
    IndexData data1 = IndexData.create(indexInfo ,LocalDate.of(2024,1,1), SourceType.USER,BigDecimal.TEN,
        BigDecimal.valueOf(100),BigDecimal.valueOf(1000),BigDecimal.TEN,BigDecimal.ONE,BigDecimal.ONE,
        1L,10L,10L);
    IndexData data2 =IndexData.create(indexInfo ,LocalDate.now(), SourceType.USER,BigDecimal.TEN,
        BigDecimal.valueOf(100),BigDecimal.valueOf(1000),BigDecimal.TEN,BigDecimal.ONE,BigDecimal.ONE,
        1L,10L,10L);
    indexDataRepository.save(data1);
    indexDataRepository.save(data2);
    em.flush();
    em.clear();

    List<AutoSyncConfig> configs = autoSyncConfigRepository.findAll();
    long matchCount = configs.stream()
        .filter(c -> c.getIndexInfo().getId().equals(indexId))
        .count();
    assertThat(matchCount).isEqualTo(1);

    List<IndexData> datas = indexDataRepository.findAll();
    long dataCount = datas.stream()
        .filter(d->d.getIndexInfo().getId().equals(indexId))
        .count();
    assertThat(dataCount).isEqualTo(2);

    //삭제
    indexInfoService.deleteIndexInfo(indexId);
    em.flush();
    em.clear();

    assertThat(indexInfoRepository.findById(indexId)).isEmpty();
    //연관 자동설정 삭제 확인
    boolean autoSyncExists = autoSyncConfigRepository.findAll().stream()
        .anyMatch(c -> c.getIndexInfo().getId().equals(indexId));
    assertThat(autoSyncExists).isFalse();
    //데이터 자동 삭제
    boolean dataExists = indexDataRepository.findAll()
        .stream().anyMatch(d->d.getIndexInfo().getId().equals(indexId));
    assertThat(dataExists).isFalse();
  }

  @Test
  @DisplayName("실패: 존재하지 않는 ID 삭제 시 예외 발생")
  void deleteIndexInfo_Fail_NotFound() {
    UUID invalidId = UUID.randomUUID();

    BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> {
      indexInfoService.deleteIndexInfo(invalidId);
    });
    assertThat(exception.getExceptionCode()).isEqualTo(ExceptionCode.INDEX_INFO_NOT_FOUND);
  }

  @Test
  @DisplayName("성공: 지수명 조건 및 즐겨찾기 필터링을 통한 지수 정보 목록 페이징 조회, 다음 페이지 없음")
  void getIndexInfoList_WithIndexNameFilterCondition() {
    indexInfoService.createIndexInfoByUser(
        new IndexInfoCreateRequest("Test", "Test Name1", 500,
            LocalDate.now(), BigDecimal.ONE, true));
    indexInfoService.createIndexInfoByUser(
        new IndexInfoCreateRequest("Test", "Test Name2", 100,
            LocalDate.now(), BigDecimal.ONE, false));//즐겨찾기로 필터링
    indexInfoService.createIndexInfoByUser(
        new IndexInfoCreateRequest("Test2", "Test Name2", 200,
            LocalDate.now(), BigDecimal.ONE, true));
    indexInfoService.createIndexInfoByUser(
        new IndexInfoCreateRequest("Te3", "Test Name2", 200,
            LocalDate.now(), BigDecimal.ONE, true));//지수명으로 필터링
    em.flush();
    em.clear();

    IndexInfoQueryCondition condition =
        new IndexInfoQueryCondition("Tes", null, true,
            null, null, IndexInfoSortField.indexClassification, "asc", 10);

    PageResponse<IndexInfoDto> response = indexInfoService.getIndexInfoList(condition);

    assertThat(response.content().size()).isEqualTo(2);
    assertThat(response.totalElements()).isEqualTo(2);
    assertThat(response.content())
        .extracting("indexClassification")
        .containsExactly("Test", "Test2");//오름차순이기 때문
    assertThat(response.hasNext()).isFalse();
    assertThat(response.nextCursor()).isNull();
  }

  @Test
  @DisplayName("성공: 지수명 조건 필터링을 통한 지수 정보 목록 페이징 조회, 다음 페이지 확인")
  void getIndexInfoList_WithCondition_HasNext() {
    indexInfoService.createIndexInfoByUser(
        new IndexInfoCreateRequest("Test", "Test Name1", 500,
            LocalDate.now(), BigDecimal.ONE, true));
    indexInfoService.createIndexInfoByUser(
        new IndexInfoCreateRequest("Test", "Test Name2", 100,
            LocalDate.now(), BigDecimal.ONE, false));
    IndexInfoDto dto = indexInfoService.createIndexInfoByUser(
        new IndexInfoCreateRequest("Test2", "E2", 200,
            LocalDate.now(), BigDecimal.ONE, false));
    indexInfoService.createIndexInfoByUser(
        new IndexInfoCreateRequest("Test3", "N2", 100,
            LocalDate.now(), BigDecimal.ONE, true));
    indexInfoService.createIndexInfoByUser(
        new IndexInfoCreateRequest("Test4", "T2", 500,
            LocalDate.now(), BigDecimal.ONE, false));

    em.flush();
    em.clear();

    IndexInfoQueryCondition condition =
        new IndexInfoQueryCondition("Tes", "2", false,
            null, null, IndexInfoSortField.employedItemsCount, "desc", 2);

    PageResponse<IndexInfoDto> response = indexInfoService.getIndexInfoList(condition);

    assertThat(response.content().size()).isEqualTo(2);
    assertThat(response.totalElements()).isEqualTo(3);
    assertThat(response.content())
        .extracting("indexName")
        .containsExactly("T2", "E2");//채용 숫자로 내림차순
    assertThat(response.hasNext()).isTrue();
    assertThat(response.nextCursor()).isEqualTo("200");//마지막 요소의 정렬필드(채용숫자)
    assertThat(response.nextIdAfter()).isEqualTo(dto.id());
  }

  @Test
  @DisplayName("성공: 전체 지수 정보의 요약 목록(ID, 이름, 분류)을 조회함")
  void getSummaries_Success_Integration() {
    // 1. Given: 데이터 2개 저장
    indexInfoService.createIndexInfoByUser(new IndexInfoCreateRequest(
        "Test", "Test Name", 100,
        LocalDate.now(), BigDecimal.TEN, true));
    indexInfoService.createIndexInfoByUser(new IndexInfoCreateRequest(
        "Test2", "Test Name2", 120, LocalDate.now(),
        BigDecimal.ONE, false));

    em.flush();
    em.clear();

    List<IndexInfoSummaryDto> summaries = indexInfoService.getSummaries();

    assertThat(summaries.size()).isEqualTo(2);
    assertThat(summaries).extracting("indexName").contains("Test Name", "Test Name2");
    assertThat(summaries).extracting("indexClassification").contains("Test", "Test2");
  }
}