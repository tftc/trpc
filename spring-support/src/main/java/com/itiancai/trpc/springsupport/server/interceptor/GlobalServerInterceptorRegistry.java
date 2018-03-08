package com.itiancai.trpc.springsupport.server.interceptor;

import com.google.common.collect.Lists;

import com.itiancai.trpc.springsupport.util.KeyComparator;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;

import io.grpc.ServerInterceptor;

public class GlobalServerInterceptorRegistry implements ApplicationContextAware {

  private final Map<Integer, List<ServerInterceptor>> serverInterceptorMap = new TreeMap(new KeyComparator());
  private ApplicationContext applicationContext;

  @PostConstruct
  public void init() {
    Map<String, GlobalServerInterceptorConfigurerAdapter> map = applicationContext.getBeansOfType(GlobalServerInterceptorConfigurerAdapter.class);
    for (GlobalServerInterceptorConfigurerAdapter globalServerInterceptorConfigurerAdapter : map.values()) {
      globalServerInterceptorConfigurerAdapter.addServerInterceptors(this);
    }
  }

  public GlobalServerInterceptorRegistry addServerInterceptors(ServerInterceptor interceptor, int order) {
    List<ServerInterceptor> serverInterceptorList = serverInterceptorMap.get(order);
    if(serverInterceptorList == null) {
      serverInterceptorList = Lists.newArrayList();
      serverInterceptorMap.put(order, serverInterceptorList);
    }
    serverInterceptorList.add(interceptor);
    return this;
  }

  public List<ServerInterceptor> getServerInterceptors() {
    List<ServerInterceptor> serverInterceptorList = new ArrayList<>();
    for (Integer key : serverInterceptorMap.keySet()) {
      serverInterceptorList.addAll(serverInterceptorMap.get(key));
    }
    return serverInterceptorList;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }
}