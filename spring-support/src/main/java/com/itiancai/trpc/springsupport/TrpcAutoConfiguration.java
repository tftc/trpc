package com.itiancai.trpc.springsupport;

import com.itiancai.trpc.core.grpc.GrpcEngine;
import com.itiancai.trpc.core.registry.Registry;
import com.itiancai.trpc.springsupport.annotation.TrpcClient;
import com.itiancai.trpc.springsupport.client.TrpcClientBeanPostProcessor;
import com.itiancai.trpc.springsupport.client.interceptor.GlobalClientInterceptorRegistry;
import com.itiancai.trpc.springsupport.registry.ConsulRegistry;
import com.itiancai.trpc.springsupport.server.TrpcServerLifecycle;
import com.itiancai.trpc.springsupport.server.TrpcServerProperties;
import com.itiancai.trpc.springsupport.server.interceptor.GlobalServerInterceptorRegistry;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.consul.ConditionalOnConsulEnabled;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties;
import org.springframework.cloud.consul.discovery.HeartbeatProperties;
import org.springframework.cloud.consul.serviceregistry.ConsulAutoRegistration;
import org.springframework.cloud.consul.serviceregistry.ConsulServiceRegistryAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.grpc.Server;

@Configuration
public class TrpcAutoConfiguration {

  /**
   * ConsulRegistry配置
   */
  @Configuration
  @ConditionalOnConsulEnabled
  @AutoConfigureAfter(ConsulServiceRegistryAutoConfiguration.class)
  public static class ConsulAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ConsulAutoRegistration consulRegistration(ConsulDiscoveryProperties properties,
                                                     ApplicationContext applicationContext,
                                                     HeartbeatProperties heartbeatProperties) {
      return ConsulAutoRegistration.registration(properties, applicationContext, null, heartbeatProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public Registry registry() {
      return new ConsulRegistry();
    }

  }

  @Bean
  @ConditionalOnBean(Registry.class)
  public GrpcEngine grpcEngine(Registry registry) {
    return new GrpcEngine(registry);
  }

  /**
   * TrpcClient配置
   */
  @Configuration
  @ConditionalOnClass(value = {TrpcClient.class})
  protected static class TrpcClientAutoConfiguration {

    @Bean
    public GlobalClientInterceptorRegistry globalClientInterceptorRegistry() {
      return new GlobalClientInterceptorRegistry();
    }

    @Bean
    public TrpcClientBeanPostProcessor trpcClientBeanPostProcessor() {
      return new TrpcClientBeanPostProcessor();
    }

  }

  @Configuration
  @EnableConfigurationProperties
  @ConditionalOnProperty(prefix = "trpc.server", name = "port")
  @ConditionalOnClass({Server.class})
  @AutoConfigureAfter(TrpcAutoConfiguration.class)
  protected static class TrpcServerAutoConfiguration {

    @Bean
    public GlobalServerInterceptorRegistry globalServerInterceptorRegistry() {
      return new GlobalServerInterceptorRegistry();
    }

    @Bean
    @ConditionalOnMissingBean
    public TrpcServerProperties trpcServerProperties() {
      return new TrpcServerProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public TrpcServerLifecycle trpcServerLifecycle(GrpcEngine grpcEngine, TrpcServerProperties serverProperties) {
      return new TrpcServerLifecycle(grpcEngine, serverProperties);
    }
  }

}
