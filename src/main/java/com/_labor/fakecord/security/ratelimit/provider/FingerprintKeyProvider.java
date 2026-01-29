package com._labor.fakecord.security.ratelimit.provider;

import java.io.IOException;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com._labor.fakecord.security.ratelimit.RateLimitSource;
import com._labor.fakecord.security.ratelimit.annotation.RateLimited;
import com._labor.fakecord.utils.RequestUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class FingerprintKeyProvider implements RateLimitKeyProvider {

  private final ObjectMapper objectMapper;

  public FingerprintKeyProvider(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public String generateKey(HttpServletRequest request, RateLimited annotation) {
    StringBuilder sb = new StringBuilder();

    sb.append(annotation.key()).append(":");

    if (annotation.source() == RateLimitSource.JSON_BODY) {
      sb.append("email:").append(extractEmailFromBody(request));
    }

    sb.append(":ip").append(RequestUtil.getClientIp(request))
      .append(":os").append(RequestUtil.getClientOperationSystem(request))
      .append(":dev").append(RequestUtil.getDeviceType(request));

    String rawKey = sb.toString();
    String hashedKey = DigestUtils.md5Hex(rawKey);

    log.debug("Generated RateLimit Key: {} -> {}", rawKey, hashedKey);

    return hashedKey;
  }

  private String extractEmailFromBody(HttpServletRequest request) {
    if (request instanceof ContentCachingRequestWrapper wrapper) {
      byte[] body = wrapper.getContentAsByteArray();

      if (body.length == 0) {
        try {
          wrapper.getInputStream().readAllBytes();
          body = wrapper.getContentAsByteArray();
        } catch (IOException e) {
            log.error("Failed to force read request body", e);
        }
      }

      if (body.length > 0) {
        try {
          JsonNode node = objectMapper.readTree(body);
          if (node.has("email")) {
            return node.get("email").asText();
          }
        } catch (IOException e) {
            log.warn("Could not parse JSON body for rate limiting");
        }
      }
    }
    return "anonymous";
  }
}