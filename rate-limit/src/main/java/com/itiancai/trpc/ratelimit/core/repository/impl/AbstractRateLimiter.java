package com.itiancai.trpc.ratelimit.core.repository.impl;

import com.itiancai.trpc.ratelimit.core.repository.RateLimiter;
import com.itiancai.trpc.ratelimit.core.repository.model.Rate;
import static com.itiancai.trpc.ratelimit.config.RateLimitProperties.Policy;
import java.util.Date;
import static java.util.concurrent.TimeUnit.SECONDS;

public abstract class AbstractRateLimiter implements RateLimiter {

  protected abstract Rate getRate(String key);

  protected abstract void saveRate(Rate rate);

  @Override
  public synchronized Rate consume(final Policy policy, final String key, final Long requestTime) {
    Rate rate = this.create(policy, key);
    updateRate(policy, rate, requestTime);
    saveRate(rate);
    return rate;
  }

  private Rate create(final Policy policy, final String key) {
    Rate rate = this.getRate(key);

    if (!isExpired(rate)) {
      return rate;
    }

    Long limit = policy.getLimit();
    Long quota = policy.getQuota() != null ? SECONDS.toMillis(policy.getQuota()) : null;
    Long refreshInterval = SECONDS.toMillis(policy.getRefreshInterval());
    Date expiration = new Date(System.currentTimeMillis() + refreshInterval);

    return new Rate(key, limit, quota, refreshInterval, expiration);
  }

  private void updateRate(final Policy policy, final Rate rate, final Long requestTime) {
    if (rate.getReset() > 0) {
      Long reset = rate.getExpiration().getTime() - System.currentTimeMillis();
      rate.setReset(reset);
    }
    if (policy.getLimit() != null && requestTime == null) {
      rate.setRemaining(Math.max(-1, rate.getRemaining() - 1));
    }
    if (policy.getQuota() != null && requestTime != null) {
      rate.setRemainingQuota(Math.max(-1, rate.getRemainingQuota() - requestTime));
    }
  }

  private boolean isExpired(final Rate rate) {
    return rate == null || (rate.getExpiration().getTime() < System.currentTimeMillis());
  }
}