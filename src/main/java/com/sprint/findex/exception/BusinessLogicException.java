package com.sprint.findex.exception;

import lombok.Getter;

public class BusinessLogicException extends RuntimeException {

  @Getter
  private final ExceptionCode exceptionCode;

  public BusinessLogicException(ExceptionCode code) {
    super(code.getMessage());
    this.exceptionCode = code;
  }
}
