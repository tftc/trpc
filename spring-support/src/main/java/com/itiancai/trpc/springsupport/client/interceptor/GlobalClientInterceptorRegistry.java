package com.itiancai.trpc.springsupport.client.interceptor;

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

import io.grpc.ClientInterceptor;

public class GlobalClientInterceptorRegistry implements ApplicationContextAware {

  private final Map<Integer, List<ClientInterceptor>> clientInterceptorMap = new TreeMap(new KeyComparator());
  private ApplicationContext applicationContext;

  @PostConstruct
  public void init() {
    Map<String, GlobalClientInterceptorConfigurerAdapter> map = applicationContext.getBeansOfType(GlobalClientInterceptorConfigurerAdapter.class);
    for (GlobalClientInterceptorConfigurerAdapter globalClientInterceptorConfigurerAdapter : map.values()) {
      globalClientInterceptorConfigurerAdapter.addClientInterceptors(this);
    }
  }

  public GlobalClientInterceptorRegistry addClientInterceptors(ClientInterceptor interceptor, int order) {
    List<ClientInterceptor> clientInterceptorList = clientInterceptorMap.get(order);
    if(clientInterceptorList == null) {
      clientInterceptorList = Lists.newArrayList();
      clientInterceptorMap.put(order, clientInterceptorList);
    }
    clientInterceptorList.add(interceptor);
    return this;
  }

  public List<ClientInterceptor> getClientInterceptors() {
    List<ClientInterceptor> clientInterceptorList = new ArrayList<>();
    for (Integer key : clientInterceptorMap.keySet()) {
      clientInterceptorList.addAll(clientInterceptorMap.get(key));
    }
    return clientInterceptorList;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }
}