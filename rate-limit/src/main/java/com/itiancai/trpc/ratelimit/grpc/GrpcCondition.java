package com.itiancai.trpc.ratelimit.grpc;

import com.itiancai.trpc.ratelimit.core.generator.RateLimitCondition;
import static com.itiancai.trpc.ratelimit.config.RateLimitProperties.Policy;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GrpcCondition implements RateLimitCondition {

  private Policy policy;
  private String methodName;

  @Override
  public Policy getPolicy() {
    return policy;
  }
}
