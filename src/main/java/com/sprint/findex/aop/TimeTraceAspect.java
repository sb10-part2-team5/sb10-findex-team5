package com.sprint.findex.aop;

import com.sprint.findex.exception.BusinessLogicException;
import jakarta.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Slf4j
@Aspect
@Component
public class TimeTraceAspect {

  public static List<String> WHITE_LIST = List.of("syncIndexData", "syncIndexInfo");

  @Around(" execution(* com.sprint.findex.controller..*(..))")
  //서비스 레벨로 찍으려면 아래 문장을 추가해주세요
  //|| execution(* com.sprint.findex.service..*(..))
  public Object trace(ProceedingJoinPoint joinPoint) throws Throwable {
    long start = System.currentTimeMillis();
    boolean success = true;

    String taskName = joinPoint.getSignature().toShortString();
    String methodName = joinPoint.getSignature().getName();

    //연동 작업은 별도의 임계값 설정
    long threshold = WHITE_LIST.contains(methodName) ? 500L : 150L;

    try {
      return joinPoint.proceed();
    } catch (Throwable throwable) {
      success = false;
      // 에러 시에만 파라미터 남기기
      String params = Arrays.toString(joinPoint.getArgs());
      //파라미터가 너무 길게 나오지 않도록 줄이기
      if (params.length() > 100) {
        params = params.substring(0, 100) + "...";
      }


      if (//유효성검사 및 비즈니스 에러는 경고
          throwable instanceof MethodArgumentNotValidException ||
          throwable instanceof ConstraintViolationException ||
          throwable instanceof BusinessLogicException) {
        log.warn("[FAIL-REQUEST] {} | Args: {}", taskName, params);
      } else {
        log.error("[FAIL-CRITICAL] {} | Args: {}", taskName, params);
      }
      throw throwable;//다시 예외 던져주기
    } finally {
      long end = System.currentTimeMillis();
      long time = end - start;

      if (success) { //성공 로그
        if (time > threshold) {
          log.warn("[SLOW] {} | {}ms", taskName, time);
        } else {
          log.info("[OK] {} | {}ms", taskName, time);
        }
      }
    }
  }
}