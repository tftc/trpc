package com.itiancai.trpc.core.utils;

import com.itiancai.trpc.core.grpc.annotation.GrpcMethodType;
import com.itiancai.trpc.core.grpc.marshaller.DefaultMarshaller;
import com.itiancai.trpc.core.grpc.server.internal.ServerInvocation;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

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
            .setFullMethodName(MethodDescriptor.generateFullMethodName(clazzName, methodName))//
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

  private static final Pattern IP_PATTERN = Pattern.compile(
          "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}");

  public static boolean isIP(String addr) {
    if (addr.length() < 7 || addr.length() > 15 || "".equals(addr)) {
      return false;
    }
    return IP_PATTERN.matcher(addr).find();
  }
}
