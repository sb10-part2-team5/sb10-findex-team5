package com.sprint.findex.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(value = {MethodArgumentNotValidException.class})
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    log.warn("[MethodArgumentNotValidException] Field Errors: {}",
        e.getBindingResult().getFieldErrors().stream()
            .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
            .toList());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ErrorResponse.of(e.getBindingResult()));
  }

  @ExceptionHandler(value = {ConstraintViolationException.class})
  public ResponseEntity<ErrorResponse> handleConstraintViolationException(
      ConstraintViolationException e) {
    log.warn("[ConstraintViolationException] Violation Errors: {}",
        e.getConstraintViolations().stream()
            .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
            .toList());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ErrorResponse.of(e.getConstraintViolations()));
  }

  @ExceptionHandler(value = {BusinessLogicException.class})
  public ResponseEntity<ErrorResponse> handleBusinessLogicException(BusinessLogicException e) {
    log.error("[BusinessLogicException] Status: {}, Code: {}, Message: {}",
        e.getExceptionCode().getStatus(), e.getExceptionCode().getCode(), e.getMessage());
    return ResponseEntity.status(HttpStatus.valueOf(e.getExceptionCode().getStatus()))
        .body(ErrorResponse.of(e.getExceptionCode().getStatus(), e.getMessage()));
  }

  @ExceptionHandler(value = {Exception.class})
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    log.error("Server Error: ", e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ErrorResponse.of(500, e.getMessage()));
  }

}
