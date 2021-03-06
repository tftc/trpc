package com.itiancai.trpc.ratelimit.grpc;

import com.itiancai.trpc.ratelimit.config.RateLimitProperties;
import com.itiancai.trpc.ratelimit.core.generator.RateLimitCondition;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import io.grpc.MethodDescriptor;

import static com.itiancai.trpc.ratelimit.config.RateLimitProperties.Policy;

public class GrpcCondition implements RateLimitCondition {

  private Optional<Policy> policy_o;

  private String serviceName;
  private String methodName;

  public GrpcCondition(MethodDescriptor methodDescriptor, RateLimitProperties properties) {
    String fullMethodName = methodDescriptor.getFullMethodName();
    String[] args = fullMethodName.split("/");
    String className = args[0];
    List<String> strs = Arrays.asList(className.split("\\."));
    serviceName = strs.get(strs.size() - 1);
    methodName = args[1];
    this.policy_o = properties.getPolicy(serviceName + "-" + methodName);
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public String getMethodName() {
    return methodName;
  }

  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }

  @Override
  public Optional<Policy> getPolicy() {
    return policy_o;
  }
}
