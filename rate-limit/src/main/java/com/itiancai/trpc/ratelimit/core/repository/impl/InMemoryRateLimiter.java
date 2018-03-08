package com.itiancai.trpc.ratelimit.core.repository.impl;

import com.itiancai.trpc.ratelimit.core.repository.model.Rate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryRateLimiter extends AbstractRateLimiter {

  private Map<String, Rate> repository = new ConcurrentHashMap<>();

  @Override
  protected Rate getRate(String key) {
    return this.repository.get(key);
  }

  @Override
  protected void saveRate(Rate rate) {
    this.repository.put(rate.getKey(), rate);
  }

}