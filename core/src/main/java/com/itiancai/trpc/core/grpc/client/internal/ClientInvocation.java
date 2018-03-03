package com.itiancai.trpc.core.grpc.client.internal;

import com.itiancai.trpc.core.grpc.GrpcRequest;
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
  private Class serviceClass;

  private int callType;
  private int callTimeout;

  public ClientInvocation(Channel channel, Class serviceClass, int callType, int callTimeout) {
    this.channel = channel;
    this.serviceClass = serviceClass;
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
          return unaryCall(method, args);
        case CLIENT_STREAMING:
          return streamCall(method, args);
        case SERVER_STREAMING:
          return streamCall(method, args);
        case BIDI_STREAMING:
          return streamCall(method, args);
        default:
          throw new RuntimeException("xxx");
      }
    } finally {
    }
  }

  private Object streamCall(Method method, Object[] args) {
    GrpcMethodType grpcMethodType = method.getAnnotation(GrpcMethodType.class);
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

  private Object unaryCall(Method method, Object[] args) {
    GrpcHystrixCommand hystrixCommand;
    MethodDescriptor methodDesc = GrpcUtils.createMethodDescriptor(serviceClass, method);
    switch (callType) {
      case 1:
        hystrixCommand = new GrpcFutureUnaryCommand(serviceClass.getName(), method.getName(), false);
        break;
      case 2:
        hystrixCommand = new GrpcBlockingUnaryCommand(serviceClass.getName(), method.getName(), false);
        break;
      default:
        hystrixCommand = new GrpcFutureUnaryCommand(serviceClass.getName(), method.getName(), false);
    }
    GrpcRequest request = new GrpcRequest(methodDesc, args[0], callType, callTimeout);
    hystrixCommand.setRequest(request);
    hystrixCommand.setClientCall(GrpcUnaryClientCall.create(channel, 0));
    return hystrixCommand.execute();

  }
}
