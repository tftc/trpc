package com.itiancai.trpc.springsupport.client.factory.normal;

import com.itiancai.trpc.springsupport.client.config.TrpcChannelProperties;
import com.itiancai.trpc.springsupport.client.config.TrpcChannelsProperties;
import com.itiancai.trpc.springsupport.client.factory.GrpcChannelFactory;
import com.itiancai.trpc.springsupport.client.factory.normal.resolver.AddressChannelResolverFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.grpc.Channel;
import io.grpc.ClientInterceptor;
import io.grpc.ClientInterceptors;
import io.grpc.LoadBalancer;
import io.grpc.NameResolver;
import io.grpc.netty.NettyChannelBuilder;

public class AddressChannelFactory implements GrpcChannelFactory {
  private final TrpcChannelsProperties properties;
  private final LoadBalancer.Factory loadBalancerFactory;
  private final NameResolver.Factory nameResolverFactory;

  public AddressChannelFactory(TrpcChannelsProperties properties, LoadBalancer.Factory loadBalancerFactory) {
    this.properties = properties;
    this.loadBalancerFactory = loadBalancerFactory;
    this.nameResolverFactory = new AddressChannelResolverFactory(properties);
  }

  @Override
  public Channel createChannel(String name) {
    return this.createChannel(name, null);
  }

  @Override
  public Channel createChannel(String name, List<ClientInterceptor> interceptors) {
    TrpcChannelProperties channelProperties = properties.getChannel(name);
    NettyChannelBuilder builder = NettyChannelBuilder.forTarget(name)
            .loadBalancerFactory(loadBalancerFactory)
            .nameResolverFactory(nameResolverFactory)
            .usePlaintext(channelProperties.isPlaintext());
    if (channelProperties.isEnableKeepAlive()) {
      builder.keepAliveWithoutCalls(channelProperties.isKeepAliveWithoutCalls())
              .keepAliveTime(channelProperties.getKeepAliveTime(), TimeUnit.SECONDS)
              .keepAliveTimeout(channelProperties.getKeepAliveTimeout(), TimeUnit.SECONDS);
    }
    Channel channel = builder.build();

    return ClientInterceptors.intercept(channel, Collections.EMPTY_LIST);
  }
}
