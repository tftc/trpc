package com.itiancai.trpc.core.grpc;

import com.google.common.collect.Maps;

import com.itiancai.trpc.core.grpc.annotation.ClientDefinition;
import com.itiancai.trpc.core.grpc.annotation.ServiceDefinition;
import com.itiancai.trpc.core.grpc.client.GrpcClientStrategy;
import com.itiancai.trpc.core.grpc.client.resolver.GrpcNameResolverProvider;
import com.itiancai.trpc.core.grpc.server.GrpcServerStrategy;
import com.itiancai.trpc.core.registry.Registry;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.grpc.Channel;
import io.grpc.ClientInterceptors;
import io.grpc.Server;
import io.grpc.ServerInterceptors;
import io.grpc.ServerServiceDefinition;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.NettyServerBuilder;
import io.grpc.util.RoundRobinLoadBalancerFactory;

public class GrpcEngine {

  private final Map<String, Channel> channelPool = Maps.newConcurrentMap();

  private Registry registry;

  public GrpcEngine(Registry registry) {
    this.registry = registry;
  }

  public <T> T createClient(ClientDefinition clientDefinition) {
    String group = clientDefinition.getGroup();
    Channel channel = channelPool.get(group);
    if (channel == null) {
      NettyChannelBuilder builder = NettyChannelBuilder.forTarget(group);
      if (registry != null) {
        builder.loadBalancerFactory(RoundRobinLoadBalancerFactory.getInstance());
        builder.nameResolverFactory(new GrpcNameResolverProvider(registry));
      }
      channel = builder.usePlaintext(true)
              .keepAliveTime(1, TimeUnit.DAYS)
              .directExecutor()
              .build();
      channel = ClientInterceptors.intercept(channel, clientDefinition.getInterceptorList());
      channelPool.put(group, channel);
    }
    GrpcClientStrategy grpcClientStrategy = new GrpcClientStrategy(channel, clientDefinition);
    return (T) grpcClientStrategy.getGrpcClient();
  }

  public Server createServer(List<ServiceDefinition> definitionList, int port) {
    NettyServerBuilder builder = NettyServerBuilder.forPort(port);
    for (ServiceDefinition definition : definitionList) {
      GrpcServerStrategy grpcServerStrategy = new GrpcServerStrategy(definition.getClazz(), definition.getImpl());
      ServerServiceDefinition serverServiceDefinition = ServerInterceptors.intercept(
              grpcServerStrategy.getServerDefintion(), definition.getInterceptorList()
      );
      builder.addService(serverServiceDefinition);
    }
    if(registry != null) {
      registry.register(port);
    }
    return builder.build();
  }
}
