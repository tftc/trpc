package com.itiancai.trpc.core.grpc.annotation;

public class ServiceDefinition<T> {

  private Class<T> clazz;

  private T impl;

  public ServiceDefinition(Class<T> clazz, T impl) {
    this.clazz = clazz;
    this.impl = impl;
  }

  public Class<T> getClazz() {
    return clazz;
  }

  public T getImpl() {
    return impl;
  }
}
