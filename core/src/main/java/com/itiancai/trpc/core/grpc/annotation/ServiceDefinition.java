package com.itiancai.trpc.core.grpc.annotation;

import java.util.ArrayList;
import java.util.List;

import io.grpc.ServerInterceptor;

public class ServiceDefinition<T> {

  private Class<T> clazz;

  private T impl;

  private List<ServerInterceptor> interceptorList = new ArrayList<>();

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

  public void addInterceptor(ServerInterceptor interceptor) {
    this.interceptorList.add(interceptor);
  }

  public void addInterceptors(List<ServerInterceptor> interceptor) {
    this.interceptorList.addAll(interceptor);
  }

  public List<ServerInterceptor> getInterceptorList() {
    return interceptorList;
  }
}
