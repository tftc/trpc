package com.itiancai.trpc.core.utils;

import com.itiancai.trpc.core.grpc.annotation.GrpcMethodType;
import com.itiancai.trpc.core.grpc.marshaller.DefaultMarshaller;
import com.itiancai.trpc.core.grpc.server.internal.ServerInvocation;

import java.lang.reflect.Method;

import io.grpc.MethodDescriptor;

public class GrpcUtils {

  public static <Req, Resp> io.grpc.MethodDescriptor createMethodDescriptor(Class<?> serivceClass, Method method) {
    String clazzName = serivceClass.getName();
    String methodName = method.getName();
    GrpcMethodType grpcMethodType = method.getAnnotation(GrpcMethodType.class);
    Class<Req> reqClass = (Class<Req>) grpcMethodType.requestClass();
    Class<Resp> respClass = (Class<Resp>) grpcMethodType.responseClass();
    return io.grpc.MethodDescriptor.newBuilder()
            .setType(grpcMethodType.methodType())//
            .setFullMethodName(io.grpc.MethodDescriptor.generateFullMethodName(clazzName, methodName))//
            .setRequestMarshaller(buildMarshaller(reqClass))//
            .setResponseMarshaller(buildMarshaller(respClass))//
            .setSafe(false)//
            .setIdempotent(false)//
            .build();
  }

  public static <Req, Resp> ServerInvocation createServerInvocation(Object service, Method method) {
    return new ServerInvocation<Req, Resp>(service, method);
  }

  private static  <T> MethodDescriptor.Marshaller buildMarshaller(Class<T> clazz) {
    return new DefaultMarshaller<>(clazz);
  }
}
