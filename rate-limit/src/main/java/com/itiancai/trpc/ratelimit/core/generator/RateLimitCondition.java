package com.itiancai.trpc.ratelimit.core.generator;

import java.util.Optional;

import static com.itiancai.trpc.ratelimit.config.RateLimitProperties.Policy;

public interface RateLimitCondition {
  Optional<Policy> getPolicy();
}
