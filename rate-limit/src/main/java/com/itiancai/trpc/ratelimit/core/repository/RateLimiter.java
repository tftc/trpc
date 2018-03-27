package com.itiancai.trpc.ratelimit.core.repository;

import static com.itiancai.trpc.ratelimit.config.RateLimitProperties.Policy;

public interface RateLimiter {

  boolean tryAcquire(Policy policy, String key);

  void consume(String key, long requestTime);
}