package com.itiancai.trpc.ratelimit.grpc;

import com.itiancai.trpc.ratelimit.config.RateLimitProperties;
import com.itiancai.trpc.ratelimit.core.generator.RateLimitKeyGenerator;
import com.itiancai.trpc.ratelimit.core.repository.RateLimiter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import io.grpc.ForwardingServerCall;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;

import static com.itiancai.trpc.ratelimit.config.RateLimitProperties.Policy;

public class RateLimiterServerInterceptor implements ServerInterceptor {

  private final static Logger log = LoggerFactory.getLogger(RateLimiterServerInterceptor.class);

  RateLimitKeyGenerator rateLimitKeyGenerator;
  RateLimiter rateLimiter;
  RateLimitProperties properties;

  public RateLimiterServerInterceptor(RateLimitKeyGenerator rateLimitKeyGenerator, RateLimiter rateLimiter, RateLimitProperties properties) {
    this.rateLimitKeyGenerator = rateLimitKeyGenerator;
    this.rateLimiter = rateLimiter;
    this.properties = properties;
  }

  @Override
  public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
    final long start = System.currentTimeMillis();
    GrpcCondition condition = new GrpcCondition(call.getMethodDescriptor(), properties);
    log.debug("RateLimiterServerInterceptor start ...");
    Optional<Policy> policy_o = condition.getPolicy();
    final boolean limitFlag = policy_o.isPresent();
    if (limitFlag) {
      Policy policy = policy_o.get();
      final String key = rateLimitKeyGenerator.key(condition);
      if (!rateLimiter.tryAcquire(policy, key)) {
        call.close(Status.UNAVAILABLE
                .withCause(new RuntimeException("ratelimit close"))
                .withDescription("ratelimit close"), headers);
      }
    }
    return next.startCall(new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {

      @SuppressWarnings("ConstantConditions")
      @Override
      public void close(Status status, Metadata trailers) {
        try {
          if(limitFlag) {
            final String key = rateLimitKeyGenerator.key(condition);
            final long requestTime = System.currentTimeMillis() - start;
            rateLimiter.consume(key, requestTime);
          }
          super.close(status, trailers);
        } catch (Throwable t) {
          log.warn("call had closed, msg:{}", t.getMessage());
        }
        log.debug("RateLimiterServerInterceptor end !");
      }
    }, headers);
  }
}
