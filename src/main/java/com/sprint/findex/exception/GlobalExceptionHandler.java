package com.sprint.findex.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(value = {MethodArgumentNotValidException.class})
  public ResponseEntity<?> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ErrorResponse.of(e.getBindingResult()));
  }

  @ExceptionHandler(value = {ConstraintViolationException.class})
  public ResponseEntity<ErrorResponse> handleConstraintViolationException(
      ConstraintViolationException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ErrorResponse.of(e.getConstraintViolations()));
  }

  @ExceptionHandler(value = {BusinessLogicException.class})
  public ResponseEntity<ErrorResponse> handleBusinessLogicException(BusinessLogicException e) {
    return ResponseEntity.status(HttpStatus.valueOf(e.getExceptionCode().getStatus()))
        .body(ErrorResponse.of(e.getExceptionCode().getStatus(), e.getMessage()));
  }

  @ExceptionHandler(value = {Exception.class})
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ErrorResponse.of(500, e.getMessage()));
  }

}
