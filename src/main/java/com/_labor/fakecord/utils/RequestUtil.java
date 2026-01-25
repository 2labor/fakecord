package com._labor.fakecord.utils;

import java.security.KeyStore.Entry;
import java.util.Map;

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

  private static Map<String, String> OS_MAP = Map.of(
    "Windows", "Windows",
    "Mac OS",  "macOS",
    "Android", "Android",
    "iPhone",  "iOS",
    "iPad",    "iOS",
    "Linux",   "Linux"
  );

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

  public static final String getClientOperationSystem(HttpServletRequest request) {
    String userOs = getClientAgent(request);

    return OS_MAP.entrySet().stream()
      .filter(entry -> userOs.contains(entry.getKey()))
      .map(Map.Entry::getValue)
      .findFirst()
      .orElse("Unknown OS");
  }

  public static String getDeviceType(HttpServletRequest request) {
    String ua = getClientAgent(request);
    if (ua.contains("Mobi")) return "Mobile";
    if (ua.contains("Tablet")) return "Tablet";
    return "Desktop";
  }
}
