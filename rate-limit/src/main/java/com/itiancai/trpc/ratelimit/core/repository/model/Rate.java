package com.itiancai.trpc.ratelimit.core.repository.model;

import java.util.Date;

public class Rate {

  private String key;
  private Long remaining;
  private Long remainingQuota;
  private Long reset;
  private Date expiration;

  public Rate() {
  }

  public Rate(String key, Long remaining, Long remainingQuota, Long reset, Date expiration) {
    this.key = key;
    this.remaining = remaining;
    this.remainingQuota = remainingQuota;
    this.reset = reset;
    this.expiration = expiration;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public Long getRemaining() {
    return remaining;
  }

  public void setRemaining(Long remaining) {
    this.remaining = remaining;
  }

  public Long getRemainingQuota() {
    return remainingQuota;
  }

  public void setRemainingQuota(Long remainingQuota) {
    this.remainingQuota = remainingQuota;
  }

  public Long getReset() {
    return reset;
  }

  public void setReset(Long reset) {
    this.reset = reset;
  }

  public Date getExpiration() {
    return expiration;
  }

  public void setExpiration(Date expiration) {
    this.expiration = expiration;
  }
}