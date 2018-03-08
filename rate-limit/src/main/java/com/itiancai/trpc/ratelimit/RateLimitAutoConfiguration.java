package com.itiancai.trpc.ratelimit;

import com.itiancai.trpc.ratelimit.config.RateLimitProperties;
import com.itiancai.trpc.ratelimit.core.generator.RateLimitKeyGenerator;
import com.itiancai.trpc.ratelimit.core.repository.RateLimiter;
import com.itiancai.trpc.ratelimit.core.repository.impl.InMemoryRateLimiter;
import com.itiancai.trpc.ratelimit.grpc.GrpcRateLimitKeyGenerator;
import com.itiancai.trpc.ratelimit.grpc.RateLimiterServerInterceptor;
import com.itiancai.trpc.springsupport.server.interceptor.GlobalServerInterceptorConfigurerAdapter;
import com.itiancai.trpc.springsupport.server.interceptor.GlobalServerInterceptorRegistry;
import com.itiancai.trpc.springsupport.util.InterceptorOrder;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.itiancai.trpc.ratelimit.config.RateLimitProperties.PREFIX;

@Configuration
@EnableConfigurationProperties(RateLimitProperties.class)
@ConditionalOnProperty(prefix = PREFIX, name = "enabled", havingValue = "true")
public class RateLimitAutoConfiguration {

  @Configuration
  @ConditionalOnMissingBean(RateLimiter.class)
  protected static class RepositoryConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "repository", havingValue = "IN_MEMORY", matchIfMissing = true)
    public RateLimiter inMemoryRateLimiter() {
      return new InMemoryRateLimiter();
    }

  }

  @Configuration
  @ConditionalOnProperty(prefix = PREFIX, name = "server", havingValue = "GRPC")
  protected static class GrpcConfiguration {

    @Bean
    @ConditionalOnMissingBean(RateLimitKeyGenerator.class)
    public RateLimitKeyGenerator ratelimitKeyGenerator(final RateLimitProperties properties) {
      return new GrpcRateLimitKeyGenerator(properties);
    }

    @Bean
    public GlobalServerInterceptorConfigurerAdapter globalRateLimitServerInterceptorConfigurerAdapter(
            RateLimitKeyGenerator rateLimitKeyGenerator, RateLimiter rateLimiter, RateLimitProperties properties
    ) {
      return new GlobalServerInterceptorConfigurerAdapter() {
        @Override
        public void addServerInterceptors(GlobalServerInterceptorRegistry registry) {
          registry.addServerInterceptors(new RateLimiterServerInterceptor(rateLimitKeyGenerator, rateLimiter, properties), InterceptorOrder.SECOND);
        }
      };
    }

  }

}
