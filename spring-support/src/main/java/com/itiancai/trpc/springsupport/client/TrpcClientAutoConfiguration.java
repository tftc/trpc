package com.itiancai.trpc.springsupport.client;

import com.itiancai.trpc.springsupport.annotation.TrpcClient;
import com.itiancai.trpc.springsupport.client.config.TrpcChannelsProperties;
import com.itiancai.trpc.springsupport.client.factory.GrpcChannelFactory;
import com.itiancai.trpc.springsupport.client.factory.TrpcClientBeanPostProcessor;
import com.itiancai.trpc.springsupport.client.factory.normal.AddressChannelFactory;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.grpc.LoadBalancer;
import io.grpc.util.RoundRobinLoadBalancerFactory;


@Configuration
@EnableConfigurationProperties
@ConditionalOnClass({GrpcChannelFactory.class})
public class TrpcClientAutoConfiguration {

  @ConditionalOnMissingBean
  @Bean
  public TrpcChannelsProperties trpcChannelsProperties() {
    return new TrpcChannelsProperties();
  }

  @ConditionalOnMissingBean
  @Bean
  public LoadBalancer.Factory loadBalancerFactory() {
    return RoundRobinLoadBalancerFactory.getInstance();
  }

  @ConditionalOnMissingBean(value = GrpcChannelFactory.class, type = "org.springframework.cloud.client.discovery.DiscoveryClient")
  @Bean
  public GrpcChannelFactory addressChannelFactory(TrpcChannelsProperties channels, LoadBalancer.Factory loadBalancerFactory) {
    return new AddressChannelFactory(channels, loadBalancerFactory);
  }

  @Bean
  @ConditionalOnClass(TrpcClient.class)
  public TrpcClientBeanPostProcessor trpcClientBeanPostProcessor() {
    return new TrpcClientBeanPostProcessor();
  }

}
