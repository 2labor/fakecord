package com._labor.fakecord.utils;

import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;

public class RequestUtil {
  private static final String[] IP_HEADER_CANDIDATES = {
    "X-Forwarded-For",
    "Proxy-Client-IP",
    "WL-Proxy-Client-IP",
    "HTTP_X_FORWARDED_FOR",
    "HTTP_X_FORWARDED",
    "HTTP_X_CLUSTER_CLIENT_IP",
    "HTTP_CLIENT_IP",
    "HTTP_FORWARDED_FOR",
    "HTTP_FORWARDED",
    "HTTP_VIA",
    "REMOTE_ADDR"
  };

  public static final String getClientIp(HttpServletRequest request) {
    for (String header : IP_HEADER_CANDIDATES) {
      String ipList = request.getHeader(header);
      if (null != ipList && !ipList.trim().isEmpty() && !"unknown".equalsIgnoreCase(ipList)) {
        return ipList.split(",")[0].trim();
      }
    }
    return request.getRemoteAddr();
  }

  public static final String getClientAgent(HttpServletRequest request) {
    String userAgent = request.getHeader("User-Agent");
    return StringUtils.hasText(userAgent) ? userAgent : "unknown";
  }
}
