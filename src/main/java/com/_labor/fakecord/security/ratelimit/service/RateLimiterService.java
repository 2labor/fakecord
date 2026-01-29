package com._labor.fakecord.security.ratelimit.service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;

@Service
public class RateLimiterService {
  private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();  

  public boolean tryConsume(String key, int capacity, int refillSeconds) {
    Bucket bucket = buckets.computeIfAbsent(key, k -> createNeBucket(capacity, refillSeconds));

    return bucket.tryConsume(1);
  }

  private Bucket createNeBucket(int capacity, int refillSeconds) {
    Bandwidth limit = Bandwidth.builder()
      .capacity(capacity)
      .refillIntervally(1,Duration.ofSeconds(refillSeconds))
      .build();

    return Bucket.builder()
      .addLimit(limit)
      .build();
  }
}
