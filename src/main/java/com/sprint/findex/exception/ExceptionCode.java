package com.sprint.findex.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionCode {
  //각 서비스에 대한 에러 코드를 입력하세요.
  //지수정보
  INDEX_INFO_NOT_FOUND(404, "INDEX01", "Index Info Not Found");

  private final int status;
  private final String code;
  private final String message;

}
