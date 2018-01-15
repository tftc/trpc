package com.itiancai.trpc.springsupport.server.factory.impl;

import com.google.common.collect.Lists;
import com.google.common.net.InetAddresses;

import com.itiancai.trpc.springsupport.server.config.TrpcServerProperties;
import com.itiancai.trpc.springsupport.server.factory.TrpcServerFactory;

import java.net.InetSocketAddress;
import java.util.List;

import io.grpc.Server;
import io.grpc.ServerServiceDefinition;
import io.grpc.netty.NettyServerBuilder;

public class NettyTrpcServerFactory implements TrpcServerFactory {

  private final TrpcServerProperties properties;
  private final List<ServerServiceDefinition> services = Lists.newLinkedList();

  public NettyTrpcServerFactory(TrpcServerProperties properties) {
    this.properties = properties;
  }

  @Override
  public Server createServer() {

    NettyServerBuilder builder = NettyServerBuilder.forAddress(new InetSocketAddress(InetAddresses.forString(getAddress()), getPort()));
    for (ServerServiceDefinition serviceDefinition : this.services) {
      builder.addService(serviceDefinition);
    }

    return builder.build();
  }

  @Override
  public String getAddress() {
    return this.properties.getAddress();
  }

  @Override
  public int getPort() {
    return this.properties.getPort();
  }

  @Override
  public void addService(ServerServiceDefinition service) {
    this.services.add(service);
  }

}