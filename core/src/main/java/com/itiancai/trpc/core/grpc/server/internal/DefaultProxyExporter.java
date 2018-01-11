package com.itiancai.trpc.core.grpc.server.internal;

import com.itiancai.trpc.core.grpc.annotation.GrpcMethodType;
import com.itiancai.trpc.core.grpc.server.GrpcProtocolExporter;
import com.itiancai.trpc.core.utils.GrpcUtils;
import com.itiancai.trpc.core.utils.ReflectUtils;

import java.lang.reflect.Method;
import java.util.List;

import io.grpc.MethodDescriptor;
import io.grpc.ServerServiceDefinition;
import io.grpc.stub.ServerCalls;

public class DefaultProxyExporter implements GrpcProtocolExporter {

  @Override
  public ServerServiceDefinition export(Class<?> protocol, Object protocolImpl) {
    Class<?> serivceClass = protocol;
    Object serviceRef = protocolImpl;
    String serviceName = protocol.getName();
    ServerServiceDefinition.Builder serviceDefBuilder = ServerServiceDefinition.builder(serviceName);
    List<Method> methods = ReflectUtils.findAllPublicMethods(serivceClass);
    if (methods.isEmpty()) {
      throw new IllegalStateException("protocolClass " + serviceName + " not have export method" + serivceClass);
    }
    for (Method method : methods) {
      GrpcMethodType grpcMethodType = method.getAnnotation(GrpcMethodType.class);
      MethodDescriptor methodDescriptor = GrpcUtils.createMethodDescriptor(serivceClass, method);
      ServerInvocation serverInvocation = GrpcUtils.createServerInvocation(serviceRef, method);
      switch (grpcMethodType.methodType()) {
        case UNARY:
          serviceDefBuilder.addMethod(methodDescriptor, ServerCalls.asyncUnaryCall(serverInvocation));
          break;
        case CLIENT_STREAMING:
          serviceDefBuilder.addMethod(methodDescriptor, ServerCalls.asyncClientStreamingCall(serverInvocation));
          break;
        case SERVER_STREAMING:
          serviceDefBuilder.addMethod(methodDescriptor, ServerCalls.asyncServerStreamingCall(serverInvocation));
          break;
        case BIDI_STREAMING:
          serviceDefBuilder.addMethod(methodDescriptor, ServerCalls.asyncBidiStreamingCall(serverInvocation));
          break;
        default:
          //TODO(@bao) 自定义异常
          throw new RuntimeException("xx");
      }
    }
    return serviceDefBuilder.build();
  }
}
