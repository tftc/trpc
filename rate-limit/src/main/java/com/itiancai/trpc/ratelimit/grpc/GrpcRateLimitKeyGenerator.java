package com.itiancai.trpc.ratelimit.grpc;

import com.itiancai.trpc.ratelimit.config.RateLimitProperties;
import com.itiancai.trpc.ratelimit.core.generator.RateLimitCondition;
import com.itiancai.trpc.ratelimit.core.generator.RateLimitKeyGenerator;

import java.util.StringJoiner;

import lombok.RequiredArgsConstructor;

import static com.itiancai.trpc.ratelimit.config.RateLimitProperties.Policy;

@RequiredArgsConstructor
public class GrpcRateLimitKeyGenerator implements RateLimitKeyGenerator {

  private final RateLimitProperties properties;

  @Override
  public String key(RateLimitCondition condition) {
    GrpcCondition cond = (GrpcCondition) condition;
    Policy policy = cond.getPolicy();
    final StringJoiner joiner = new StringJoiner(":");
    joiner.add(properties.getKeyPrefix());
    joiner.add(cond.getMethodName());
    return joiner.toString();
  }
}