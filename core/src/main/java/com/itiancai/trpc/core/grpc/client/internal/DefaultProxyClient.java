package com.itiancai.trpc.core.grpc.client.internal;

import com.baidu.bjf.remoting.protobuf.utils.ClassHelper;
import com.itiancai.trpc.core.grpc.client.GrpcProtocolClient;

import java.lang.reflect.Proxy;

import io.grpc.Channel;

public class DefaultProxyClient<T> implements GrpcProtocolClient {

  Class<T> serviceClass;

  public DefaultProxyClient(Class<T> clazz) {
    this.serviceClass = clazz;
  }

  @Override
  public T getGrpcClient(Channel channel, int callType, int callTimeout) {
    return (T) Proxy.newProxyInstance(
            ClassHelper.getClassLoader(),
            new Class[]{serviceClass},
            new ClientInvocation(channel, serviceClass, callType, callTimeout)
    );
  }
}
