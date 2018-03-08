package com.itiancai.trpc.trace;

import com.itiancai.trpc.springsupport.annotation.TrpcClient;
import com.itiancai.trpc.springsupport.annotation.TrpcService;
import com.itiancai.trpc.springsupport.client.interceptor.GlobalClientInterceptorConfigurerAdapter;
import com.itiancai.trpc.springsupport.client.interceptor.GlobalClientInterceptorRegistry;
import com.itiancai.trpc.springsupport.server.interceptor.GlobalServerInterceptorConfigurerAdapter;
import com.itiancai.trpc.springsupport.server.interceptor.GlobalServerInterceptorRegistry;
import com.itiancai.trpc.springsupport.util.InterceptorOrder;
import com.itiancai.trpc.trace.grpc.client.MetadataInjector;
import com.itiancai.trpc.trace.grpc.client.TraceClientInterceptor;
import com.itiancai.trpc.trace.grpc.server.MetadataExtractor;
import com.itiancai.trpc.trace.grpc.server.TraceServerInterceptor;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TraceAutoConfiguration {

  //Grpc Client分布式追踪拦截器配置
  @Configuration
  @ConditionalOnClass(value = {Tracer.class, TrpcClient.class})
  @ConditionalOnProperty(value = "spring.sleuth.scheduled.enabled", matchIfMissing = true)
  protected static class TraceClientAutoConfiguration {
    @Bean
    public GlobalClientInterceptorConfigurerAdapter globalTraceClientInterceptorConfigurerAdapter(final Tracer tracer) {
      return new GlobalClientInterceptorConfigurerAdapter() {
        @Override
        public void addClientInterceptors(GlobalClientInterceptorRegistry registry) {
          registry.addClientInterceptors(new TraceClientInterceptor(tracer, new MetadataInjector()), InterceptorOrder.FIRST);
        }
      };
    }
  }

  //Grpc Server分布式追踪拦截器配置
  @Configuration
  @ConditionalOnProperty(value = "spring.sleuth.scheduled.enabled", matchIfMissing = true)
  @ConditionalOnClass(value = {Tracer.class, TrpcService.class})
  protected static class TraceServerAutoConfiguration {

    @Bean
    public GlobalServerInterceptorConfigurerAdapter globalTraceServerInterceptorConfigurerAdapter(final Tracer tracer) {
      return new GlobalServerInterceptorConfigurerAdapter() {
        @Override
        public void addServerInterceptors(GlobalServerInterceptorRegistry registry) {
          registry.addServerInterceptors(new TraceServerInterceptor(tracer, new MetadataExtractor()), InterceptorOrder.FIRST);
        }
      };
    }

  }

}
