package com.itiancai.trpc.ratelimit.core.repository.impl;

import com.itiancai.trpc.ratelimit.config.RateLimitProperties.Policy;
import com.itiancai.trpc.ratelimit.core.repository.RateLimiter;

import org.springframework.util.Assert;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.concurrent.TimeUnit.SECONDS;

public class InMemoryRateLimiter implements RateLimiter {

  private Map<String, Rate> cache = new ConcurrentHashMap<>();

  @Override
  public boolean tryAcquire(Policy policy, String key) {
    Assert.notNull(policy, "policy 不能为空");
    Assert.notNull(key, "limit key 不能为空");
    Rate rate = cache.get(key);
    if(rate != null) {
      if(rate.isExpiration()) {
        rate = new Rate(policy, key);
        cache.put(key, rate);
      }
    } else {
      rate = new Rate(policy, key);
      cache.put(key, rate);
    }
    //重置
    return rate.tryAcquire();
  }

  @Override
  public void consume(String key, long requestTime) {
    Assert.notNull(key, "limit key 不能为空");
    Assert.isTrue(requestTime > 0, "请求耗时不能小于0");
    Rate rateExt = cache.get(key);
    if(rateExt != null) {
      rateExt.consume(requestTime);
    }
  }

  private class Rate {

    private String key;
    private AtomicLong remaining; //次数
    private AtomicLong remainingQuota; //时间
    private long reset; //间隔
    private Date expiration; //期限

    public Rate(Policy policy, String key) {
      this.key = key;
      this.remaining = new AtomicLong(policy.getLimit());
      if (policy.getQuota() != null) {
        this.remainingQuota = new AtomicLong(SECONDS.toMillis(policy.getQuota()));
      } else {
        this.remainingQuota = null;
      }
      this.reset = SECONDS.toMillis(policy.getRefreshInterval());
      this.expiration = new Date(System.currentTimeMillis() + reset);
    }

    public boolean tryAcquire() {
      if(remainingQuota != null) {
        return remainingQuota.get() > 0 && this.remaining.decrementAndGet() >= 0;
      } else {
        return this.remaining.decrementAndGet() >= 0;
      }
    }

    public void consume(long requestTime) {
      if(remainingQuota != null) {
        remainingQuota.addAndGet(requestTime * -1);
      }
    }

    public boolean isExpiration() {
      return expiration.getTime() < System.currentTimeMillis();
    }

  }



}