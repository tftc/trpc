package com.itiancai.trpc.core.grpc.client.internal.unary;

import com.itiancai.trpc.core.grpc.GrpcRequest;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.MethodDescriptor;

public abstract class GrpcHystrixCommand<Req, Resp> extends HystrixCommand<Resp> {

  private static final Logger logger = LoggerFactory.getLogger(GrpcHystrixCommand.class);

  private final String serviceName;

  private final String methodName;

  private GrpcUnaryClientCall<Req, Resp> clientCall;

  private GrpcRequest<Req, Resp> request;

  public GrpcHystrixCommand(String serviceName, String methodName, Boolean isEnabledFallBack) {
    super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(serviceName))//
        .andCommandKey(HystrixCommandKey.Factory.asKey(serviceName + ":" + methodName))//
        .andCommandPropertiesDefaults(
            HystrixCommandProperties.Setter().withCircuitBreakerRequestVolumeThreshold(20)// 10秒钟内至少19此请求失败，熔断器才发挥起作用
                .withCircuitBreakerSleepWindowInMilliseconds(30000)// 熔断器中断请求30秒后会进入半打开状态,放部分流量过去重试
                .withCircuitBreakerErrorThresholdPercentage(50)// 错误率达到50开启熔断保护
                .withExecutionTimeoutEnabled(false)// 禁用这里的超时
                .withFallbackEnabled(isEnabledFallBack))//
        .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(100)
            .withAllowMaximumSizeToDivergeFromCoreSize(true).withMaximumSize(Integer.MAX_VALUE)));
    this.serviceName = serviceName;
    this.methodName = methodName;
  }

  @Override
  public Resp execute() {
    try {
      return super.execute();
    } finally {
    }
  }

  @Override
  protected Resp run() throws Exception {
    try {
      Resp response = this.run0(request.getRequestParam(), request.getMethodDescriptor(), request.getCallTimeOut(), clientCall);
      return response;
    } finally {
    }
  }

  @Override
  protected Resp getFallback() {
    try {
      return request.getResponseClass().newInstance();
    } catch (Throwable e) {
      //ignore
      return null;
    }
  }

  protected abstract Resp run0(Req req, MethodDescriptor<Req, Resp> methodDesc, Integer timeOut, GrpcUnaryClientCall<Req, Resp> clientCall);

  public void setRequest(GrpcRequest<Req, Resp> request) {
    this.request = request;
  }

  public void setClientCall(GrpcUnaryClientCall<Req, Resp> clientCall) {
    this.clientCall = clientCall;
  }
}