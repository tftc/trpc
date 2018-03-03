package com.itiancai.trpc.core.grpc.annotation;

import java.util.Arrays;
import java.util.List;

public class ClientDefinition<T> {

  private String group;
  private Class<T> clazz;
  private int retries;
  private String[] retryMethods;
  private boolean async;
  private boolean fallback;
  private String[] fallBackMethods;
  private int callTimeout;

  public ClientDefinition(String group, Class<T> clazz, int retries, String[] retryMethods, boolean async, boolean fallback, String[] fallBackMethods, int callTimeout) {
    this.group = group;
    this.clazz = clazz;
    this.retries = retries;
    this.retryMethods = retryMethods;
    this.async = async;
    this.fallback = fallback;
    this.fallBackMethods = fallBackMethods;
    this.callTimeout = callTimeout;
  }

  public String getGroup() {
    return group;
  }

  public Class<T> getClazz() {
    return clazz;
  }

  public int getRetries() {
    return retries;
  }

  public String[] getRetryMethods() {
    return retryMethods;
  }

  public boolean isAsync() {
    return async;
  }

  public boolean isFallback() {
    return fallback;
  }

  public String[] getFallBackMethods() {
    return fallBackMethods;
  }

  public int getCallTimeout() {
    return callTimeout;
  }

  public boolean validFallBack(String methodName) {
    List<String> fallBackMethods = Arrays.asList(this.fallBackMethods);
    return fallback && (fallBackMethods.size() == 0 || fallBackMethods.contains(methodName));
  }

  public int retryCnt(String methodName) {
    List<String> retryMethods = Arrays.asList(this.retryMethods);
    if(retries > 0 && (retryMethods.size() == 0 || retryMethods.contains(methodName))) {
      return retries;
    } else {
      return 0;
    }
  }
}
