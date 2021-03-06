package com.itiancai.trpc.ratelimit.grpc;

import com.itiancai.trpc.ratelimit.config.RateLimitProperties;
import com.itiancai.trpc.ratelimit.core.generator.RateLimitCondition;
import com.itiancai.trpc.ratelimit.core.generator.RateLimitKeyGenerator;

import java.util.StringJoiner;

public class GrpcRateLimitKeyGenerator implements RateLimitKeyGenerator {

  private final RateLimitProperties properties;

  public GrpcRateLimitKeyGenerator(RateLimitProperties properties) {
    this.properties = properties;
  }

  @Override
  public String key(RateLimitCondition condition) {
    GrpcCondition cond = (GrpcCondition) condition;
    final StringJoiner joiner = new StringJoiner(":");
    joiner.add(properties.getKeyPrefix());
    joiner.add(cond.getServiceName());
    joiner.add(cond.getMethodName());
    return joiner.toString();
  }
}