package com.sprint.findex.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class ClientIpResolver {

  public String resolve(HttpServletRequest request) {
    String forwardedFor = request.getHeader("X-Forwarded-For");
    if (hasText(forwardedFor)) {
      return forwardedFor.split(",")[0].trim();
    }

    String realIp = request.getHeader("X-Real-IP");
    if (hasText(realIp)) {
      return realIp.trim();
    }

    return request.getRemoteAddr();
  }

  private boolean hasText(String value) {
    return value != null && !value.isBlank();
  }
}