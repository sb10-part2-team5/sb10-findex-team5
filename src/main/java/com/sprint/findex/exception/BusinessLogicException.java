package com.sprint.findex.exception;

import lombok.Getter;

@Getter
public class BusinessLogicException extends RuntimeException {

  private final ExceptionCode exceptionCode;

  public BusinessLogicException(ExceptionCode code) {
    super(code.getMessage());
    this.exceptionCode = code;
  }
}
