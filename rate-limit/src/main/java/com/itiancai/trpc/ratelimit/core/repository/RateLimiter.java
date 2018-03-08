package com.itiancai.trpc.ratelimit.core.repository;

import com.itiancai.trpc.ratelimit.core.repository.model.Rate;

import static com.itiancai.trpc.ratelimit.config.RateLimitProperties.Policy;

public interface RateLimiter {

  Rate consume(Policy policy, String key, Long requestTime);
}