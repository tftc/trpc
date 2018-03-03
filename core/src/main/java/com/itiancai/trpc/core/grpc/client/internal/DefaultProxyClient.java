package com.itiancai.trpc.core.grpc.client.internal;

import com.baidu.bjf.remoting.protobuf.utils.ClassHelper;
import com.itiancai.trpc.core.grpc.annotation.ClientDefinition;
import com.itiancai.trpc.core.grpc.client.GrpcProtocolClient;

import java.lang.reflect.Proxy;

import io.grpc.Channel;

public class DefaultProxyClient<T> implements GrpcProtocolClient {

  ClientDefinition<T> clientDefinition;

  public DefaultProxyClient(ClientDefinition<T> clientDefinition) {
    this.clientDefinition = clientDefinition;
  }

  @Override
  public T getGrpcClient(Channel channel, int callType, int callTimeout) {
    return (T) Proxy.newProxyInstance(
            ClassHelper.getClassLoader(),
            new Class[]{clientDefinition.getClazz()},
            new ClientInvocation(channel, clientDefinition, callType, callTimeout)
    );
  }
}
