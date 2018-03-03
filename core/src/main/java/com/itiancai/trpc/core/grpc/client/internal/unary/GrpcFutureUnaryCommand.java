package com.itiancai.trpc.core.grpc.client.internal.unary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import io.grpc.MethodDescriptor;

public class GrpcFutureUnaryCommand<Req, Resp> extends GrpcHystrixCommand<Req, Resp> {

  private static final Logger logger = LoggerFactory.getLogger(GrpcHystrixCommand.class);


  public GrpcFutureUnaryCommand(String serviceName, String methodName, Boolean isEnabledFallBack) {
    super(serviceName, methodName, isEnabledFallBack);
  }

  @Override
  protected Resp run0(Req req, MethodDescriptor<Req, Resp> methodDesc, Integer timeOut, GrpcUnaryClientCall<Req, Resp> clientCall) {
    try {
      return clientCall.unaryFuture(req, methodDesc).get(timeOut, TimeUnit.MILLISECONDS);
    } catch (Throwable e) {
      logger.error(e.getMessage(), e);
      //TODO 自定义服务异常
      throw new RuntimeException("");
    }
  }
}