package com.itiancai.trpc.core.grpc.server.internal;

import com.itiancai.trpc.core.grpc.annotation.GrpcMethodType;

import java.lang.reflect.Method;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.ServerCalls;
import io.grpc.stub.StreamObserver;
import io.netty.util.internal.ThrowableUtil;

@SuppressWarnings("unchecked")
public class ServerInvocation<Req, Resp> implements io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
        ServerCalls.ServerStreamingMethod<Req, Resp>, ServerCalls.ClientStreamingMethod<Req, Resp>,
        ServerCalls.BidiStreamingMethod<Req, Resp> {

  private final Object service;

  private final Method method;

  private final GrpcMethodType grpcMethodType;

  public ServerInvocation(Object service, Method method) {
    this.service = service;
    this.method = method;
    grpcMethodType = method.getAnnotation(GrpcMethodType.class);
  }

  /**
   * StreamingRequestMethod
   * @param responseObserver
   * @return
   */
  @Override
  public StreamObserver<Req> invoke(StreamObserver<Resp> responseObserver) {
    try {
      return (StreamObserver<Req>) method.invoke(service, responseObserver);
    } catch (Throwable e) {
      String stackTrace = ThrowableUtil.stackTraceToString(e);
      StatusRuntimeException statusException = Status.UNAVAILABLE.withDescription(stackTrace).asRuntimeException();
      responseObserver.onError(statusException);
      return null;
    }
  }

  /**
   * UnaryRequestMethod
   * @param request
   * @param responseObserver
   */
  @Override
  public void invoke(Req request, StreamObserver<Resp> responseObserver) {
    switch (grpcMethodType.methodType()) {
      case UNARY:
        unaryCall(request, responseObserver);
        break;
      case SERVER_STREAMING:
        streamCall(request, responseObserver);
        break;
      default:
        break;
    }
  }

  private void streamCall(Req request, StreamObserver<Resp> responseObserver) {
    try {
      method.invoke(service, request, responseObserver);
    } catch (Throwable e) {
      String stackTrace = ThrowableUtil.stackTraceToString(e);
      StatusRuntimeException statusException = Status.UNAVAILABLE.withDescription(stackTrace).asRuntimeException();
      responseObserver.onError(statusException);
    }

  }

  private void unaryCall(Req request, StreamObserver<Resp> responseObserver) {
    try {
      final Resp resp = (Resp) method.invoke(service, request);
      responseObserver.onNext(resp);
      responseObserver.onCompleted();
    } catch (Throwable e) {
      String stackTrace = ThrowableUtil.stackTraceToString(e);
      StatusRuntimeException statusException = Status.UNAVAILABLE.withDescription(stackTrace).asRuntimeException();
      responseObserver.onError(statusException);
    } finally {
    }
  }
}
