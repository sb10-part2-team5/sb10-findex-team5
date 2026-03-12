package com.sprint.findex.exception;

import jakarta.validation.ConstraintViolation;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

@Getter
public class ErrorResponse {

  private final Instant timestamp;
  private final int status;
  private final String message;
  private final List<FieldError> fieldErrors;
  private final List<ConstraintViolationError> constraintViolationErrors;

  private ErrorResponse(int status, String message) {
    this.timestamp = Instant.now();
    this.status = status;
    this.message = message;
    this.fieldErrors = null;
    this.constraintViolationErrors = null;
  }

  private ErrorResponse(String message, List<FieldError> fieldErrors,
      List<ConstraintViolationError> constraintViolationErrors) {
    this.timestamp = Instant.now();
    this.status = HttpStatus.BAD_REQUEST.value();
    this.message = message;
    this.fieldErrors = fieldErrors;
    this.constraintViolationErrors = constraintViolationErrors;
  }

  public static ErrorResponse of(int status, String message) {
    return new ErrorResponse(status, message);
  }

  public static ErrorResponse of(BindingResult bindingResult) {
    return new ErrorResponse("입력값 검증 실패", FieldError.of(bindingResult), null);
  }

  public static ErrorResponse of(Set<ConstraintViolation<?>> constraintViolations) {
    return new ErrorResponse("제약 조건 위반", null, ConstraintViolationError.of(constraintViolations));
  }

  public record FieldError(String field, Object rejectedValue, String message) {

    private static List<FieldError> of(BindingResult bindingResult) {
      return bindingResult.getFieldErrors().stream()
          .map(e -> new FieldError(
              e.getField(),
              e.getRejectedValue(),
              e.getDefaultMessage()
          )).toList();
    }
  }

  public record ConstraintViolationError(String propertyPath, Object invalidValue, String message) {

    private static List<ConstraintViolationError> of(
        Set<ConstraintViolation<?>> constraintViolations) {
      return constraintViolations.stream()
          .map(v -> new ConstraintViolationError(
              v.getPropertyPath().toString(),
              v.getInvalidValue(),
              v.getMessage()
          )).toList();
    }
  }
}
