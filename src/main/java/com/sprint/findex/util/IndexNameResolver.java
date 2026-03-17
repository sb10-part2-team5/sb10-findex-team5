package com.sprint.findex.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class IndexNameResolver {

    // 2024년 12월 6일 이후 변경된 지수명 목록
    // 과거 지수명과 현재 지수명이 달라도 같은 지수로 취급하기 위해 사용
    private static final Map<String, Map<String, String>> INDEX_NAME_CHANGES = Map.of(
            "KOSPI 시리즈", Map.ofEntries(
                    Map.entry("음식료품", "음식료·담배"),
                    Map.entry("음식료·담배", "음식료·담배"),
                    Map.entry("섬유의복", "섬유·의류"),
                    Map.entry("섬유·의류", "섬유·의류"),
                    Map.entry("의약품", "제약"),
                    Map.entry("제약", "제약"),
                    Map.entry("비금속광물", "비금속"),
                    Map.entry("비금속", "비금속"),
                    Map.entry("철강금속", "금속"),
                    Map.entry("금속", "금속"),
                    Map.entry("의료정밀", "의료·정밀기기"),
                    Map.entry("의료·정밀기기", "의료·정밀기기"),
                    Map.entry("기계", "기계·장비"),
                    Map.entry("기계·장비", "기계·장비"),
                    Map.entry("운수장비", "운송장비·부품"),
                    Map.entry("운송장비·부품", "운송장비·부품"),
                    Map.entry("운수창고업", "운송·창고"),
                    Map.entry("운송·창고", "운송·창고")
            ),
            "KOSDAQ 시리즈", Map.ofEntries(
                    Map.entry("일반전기전자", "전기전자"),
                    Map.entry("전기전자", "전기전자"),
                    Map.entry("전기·가스·수도", "전기·가스"),
                    Map.entry("전기·가스", "전기·가스"),
                    Map.entry("운송", "운송·창고"),
                    Map.entry("운송·창고", "운송·창고")
            )
    );

    public List<String> buildSearchNames(String indexClassification, String indexName) {
        // 요청이 온 이름을 표준 이름으로 변환
        String standardName = resolveStandardName(indexClassification, indexName);
        // 조회할 이름 목록
        LinkedHashSet<String> searchNames = new LinkedHashSet<>();
        if (standardName == null) {
            return List.of();
        }

        // 표준 이름 추가
        searchNames.add(standardName);

        // 해당 indexClassification의 변경 목록 조회
        Map<String, String> nameChanges = INDEX_NAME_CHANGES
                .getOrDefault(indexClassification, Map.of());

        // 표준 이름과 같은 지수로 판단되는 이름들 추가
        for (Map.Entry<String, String> entry : nameChanges.entrySet()) {
            if (standardName.equals(entry.getValue())) {
                searchNames.add(entry.getKey());
            }
        }

        return new ArrayList<>(searchNames);
    }

    public String resolveStandardName(String indexClassification, String indexName) {
        return INDEX_NAME_CHANGES
                // indexClassification별 변경 목록 조회
                .getOrDefault(indexClassification, Map.of())
                // 이름 변경 목록에 indexName가 있으면 표준 이름 반환, 없으면 그대로 반환
                .getOrDefault(indexName, indexName);
    }
}