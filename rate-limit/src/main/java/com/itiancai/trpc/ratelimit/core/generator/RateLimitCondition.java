package com.itiancai.trpc.ratelimit.core.generator;


import static com.itiancai.trpc.ratelimit.config.RateLimitProperties.Policy;

public interface RateLimitCondition {
  Policy getPolicy();
}
