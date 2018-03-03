package com.itiancai.trpc.core.grpc.client;

import com.itiancai.trpc.core.grpc.annotation.ClientDefinition;
import com.itiancai.trpc.core.grpc.client.internal.DefaultProxyClient;

import io.grpc.Channel;

public class GrpcClientStrategy<T> {

  private int callType;
  private int callTimeout;
  private GrpcProtocolClient<T> grpcProtocolClient;

  private Channel channel;

  public GrpcClientStrategy(Channel channel, ClientDefinition<T> clientDefinition) {
    grpcProtocolClient = new DefaultProxyClient<>(clientDefinition);
    callType = clientDefinition.isAsync() ? 1 : 2;
    callTimeout = clientDefinition.getCallTimeout();
    this.channel = channel;
  }

  public T getGrpcClient() {
    return grpcProtocolClient.getGrpcClient(channel, callType, callTimeout);
  }
}
