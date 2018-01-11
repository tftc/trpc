package com.itiancai.trpc.core.grpc.client;

import com.itiancai.trpc.core.grpc.client.internal.DefaultProxyClient;

import io.grpc.Channel;

public class GrpcClientStrategy<T> {

  private GrpcProtocolClient<T> grpcProtocolClient;

  private Channel channel;

  public GrpcClientStrategy(Class<T> protocolClass, Channel channel) {
    grpcProtocolClient = new DefaultProxyClient<>(protocolClass);
    this.channel = channel;
  }

  public T getGrpcClient() {
    //TODO(@bao)
    return grpcProtocolClient.getGrpcClient(channel, 0, 0);
  }
}
