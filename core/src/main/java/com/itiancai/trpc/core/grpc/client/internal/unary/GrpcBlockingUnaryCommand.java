package com.itiancai.trpc.core.grpc.client.internal.unary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.MethodDescriptor;

public class GrpcBlockingUnaryCommand<Req, Resp> extends GrpcHystrixCommand<Req, Resp> {

  private static final Logger logger = LoggerFactory.getLogger(GrpcHystrixCommand.class);

  public GrpcBlockingUnaryCommand(String serviceName, String methodName,
      Boolean isEnabledFallBack) {
    super(serviceName, methodName, isEnabledFallBack);
  }

  @Override
  protected Resp run0(Req req, MethodDescriptor<Req, Resp> methodDesc, Integer timeOut, GrpcUnaryClientCall<Req, Resp> clientCall) {
    try {
      return clientCall.blockingUnaryResult(req, methodDesc);
    } catch (Throwable e) {
      logger.error(e.getMessage(), e);
      throw new RuntimeException();
    }
  }

}