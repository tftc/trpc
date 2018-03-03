package com.itiancai.trpc.core.grpc.client.internal;

import com.itiancai.trpc.core.grpc.GrpcRequest;
import com.itiancai.trpc.core.grpc.annotation.ClientDefinition;
import com.itiancai.trpc.core.grpc.annotation.GrpcMethodType;
import com.itiancai.trpc.core.grpc.client.internal.unary.GrpcBlockingUnaryCommand;
import com.itiancai.trpc.core.grpc.client.internal.unary.GrpcFutureUnaryCommand;
import com.itiancai.trpc.core.grpc.client.internal.unary.GrpcHystrixCommand;
import com.itiancai.trpc.core.grpc.client.internal.unary.GrpcUnaryClientCall;
import com.itiancai.trpc.core.utils.GrpcUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.MethodDescriptor;
import io.grpc.stub.ClientCalls;
import io.grpc.stub.StreamObserver;

@SuppressWarnings("unchecked")
public class ClientInvocation implements InvocationHandler {

  private Channel channel;
  private ClientDefinition clientDefinition;
  private Class serviceClass;

  private int callType;
  private int callTimeout;

  public ClientInvocation(Channel channel, ClientDefinition clientDefinition, int callType, int callTimeout) {
    this.channel = channel;
    this.clientDefinition = clientDefinition;
    this.serviceClass = clientDefinition.getClazz();
    this.callType = callType;
    this.callTimeout = callTimeout;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    GrpcMethodType grpcMethodType = method.getAnnotation(GrpcMethodType.class);
    MethodDescriptor.MethodType methodType = grpcMethodType.methodType();
    try {
      switch (methodType) {
        case UNARY:
          return unaryCall(method, grpcMethodType, args);
        case CLIENT_STREAMING:
          return streamCall(method, grpcMethodType, args);
        case SERVER_STREAMING:
          return streamCall(method, grpcMethodType, args);
        case BIDI_STREAMING:
          return streamCall(method, grpcMethodType, args);
        default:
          throw new RuntimeException("xxx");
      }
    } finally {
    }
  }

  private Object streamCall(Method method, GrpcMethodType grpcMethodType, Object[] args) {
    MethodDescriptor.MethodType methodType = grpcMethodType.methodType();
    MethodDescriptor methodDesc = GrpcUtils.createMethodDescriptor(serviceClass, method);
    switch (methodType) {
      case CLIENT_STREAMING:
        return ClientCalls.asyncClientStreamingCall(
                channel.newCall(methodDesc, CallOptions.DEFAULT),
                (StreamObserver) args[0]
        );
      case SERVER_STREAMING:
        ClientCalls.asyncServerStreamingCall(
                channel.newCall(methodDesc, CallOptions.DEFAULT),
                args[0],
                (StreamObserver) args[1]
        );
        return null;
      case BIDI_STREAMING:
        return ClientCalls.asyncBidiStreamingCall(
                channel.newCall(methodDesc, CallOptions.DEFAULT),
                (StreamObserver) args[0]
        );
      default:
        throw new RuntimeException("xxx");
    }

  }

  private Object unaryCall(Method method, GrpcMethodType grpcMethodType, Object[] args) {
    GrpcHystrixCommand hystrixCommand;
    MethodDescriptor methodDesc = GrpcUtils.createMethodDescriptor(serviceClass, method);
    boolean fallback = clientDefinition.validFallBack(method.getName());
    switch (callType) {
      case 1:
        hystrixCommand = new GrpcFutureUnaryCommand(serviceClass.getName(), method.getName(), fallback);
        break;
      case 2:
        hystrixCommand = new GrpcBlockingUnaryCommand(serviceClass.getName(), method.getName(), fallback);
        break;
      default:
        hystrixCommand = new GrpcFutureUnaryCommand(serviceClass.getName(), method.getName(), fallback);
    }
    GrpcRequest request = new GrpcRequest(methodDesc, grpcMethodType.responseClass(), grpcMethodType.responseClass(), args[0], callType, callTimeout);
    hystrixCommand.setRequest(request);
    int retryCnt = clientDefinition.retryCnt(method.getName());
    hystrixCommand.setClientCall(GrpcUnaryClientCall.create(channel, retryCnt));
    return hystrixCommand.execute();

  }
}
