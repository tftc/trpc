package com.itiancai.trpc.springsupport.server;

import com.itiancai.trpc.springsupport.server.config.TrpcServerProperties;
import com.itiancai.trpc.springsupport.server.factory.TrpcServerFactory;
import com.itiancai.trpc.springsupport.server.factory.TrpcServerLifecycle;
import com.itiancai.trpc.springsupport.server.factory.impl.NettyTrpcServerFactory;
import com.itiancai.trpc.springsupport.server.finder.TrpcServiceFinder;
import com.itiancai.trpc.springsupport.server.finder.impl.AnnotationTrpcServiceFinder;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.grpc.Channel;
import io.grpc.Server;
import io.grpc.ServerServiceDefinition;

@Configuration
@EnableConfigurationProperties
@ConditionalOnProperty(prefix = "trpc.server", name = "port")
@ConditionalOnClass({Server.class, TrpcServerFactory.class})
public class TrpcServerAutoConfiguration {

  @ConditionalOnMissingBean
  @Bean
  public TrpcServerProperties trpcServerProperties() {
    return new TrpcServerProperties();
  }


  @ConditionalOnMissingBean
  @Bean
  public TrpcServiceFinder trpcServiceFinder() {
    return new AnnotationTrpcServiceFinder();
  }

  @ConditionalOnMissingBean
  @ConditionalOnClass(Channel.class)
  @Bean
  public TrpcServerFactory trpcServiceFactory(TrpcServerProperties properties, TrpcServiceFinder trpcServiceFinder) {
    TrpcServerFactory factory = new NettyTrpcServerFactory(properties);
    for (ServerServiceDefinition service : trpcServiceFinder.findTrpcServices()) {
      factory.addService(service);
    }
    return factory;
  }

  @ConditionalOnMissingBean
  @Bean
  public TrpcServerLifecycle trpcServerLifecycle(TrpcServerFactory factory) {
    return new TrpcServerLifecycle(factory);
  }
}