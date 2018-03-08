package com.itiancai.trpc.ratelimit.core.generator;

public interface RateLimitKeyGenerator {

  String key(RateLimitCondition condition);
}