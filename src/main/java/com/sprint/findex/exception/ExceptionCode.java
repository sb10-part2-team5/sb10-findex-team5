package com.sprint.findex.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionCode {
    //각 서비스에 대한 에러 코드를 입력하세요.
    //지수정보
    INDEX_INFO_NOT_FOUND(404, "INDEX01", "Index Info Not Found"),
    INDEX_INFO_ALREADY_EXISTS(409, "INDEX02",
            "Index Already Exists With Same Classification And Name"),


    //자동연동설정
    AUTO_SYNC_CONFIG_NOT_FOUND(404, "AUTO_SYNC_CONFIG01", "Auto Sync Config Not Found"),

    // 지수 데이터
    INDEX_DATA_NOT_FOUND(404, "INDEX_DATA01", "Index Data Not Found"),
    INVALID_INDEX_DATA_REQUEST(400, "INDEX_DATA02", "Invalid Index Data Request"),
    INDEX_DATA_ALREADY_EXISTS(409, "INDEX_DATA03", "Index Data Already Exists"),
    INVALID_DATE_RANGE(400, "INDEX_DATA04", "Invalid Date Range");

    private final int status;
    private final String code;
    private final String message;

}
