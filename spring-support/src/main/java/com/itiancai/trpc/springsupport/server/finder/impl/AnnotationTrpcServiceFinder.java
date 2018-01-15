package com.itiancai.trpc.springsupport.server.finder.impl;

import com.google.common.collect.Lists;

import com.itiancai.trpc.core.grpc.server.GrpcServerStrategy;
import com.itiancai.trpc.springsupport.annotation.TrpcService;
import com.itiancai.trpc.springsupport.server.finder.TrpcServiceFinder;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import io.grpc.ServerServiceDefinition;

public class AnnotationTrpcServiceFinder implements ApplicationContextAware, TrpcServiceFinder {

  private ApplicationContext applicationContext;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext)
          throws BeansException {
    this.applicationContext = applicationContext;
  }

  public Collection<String> findTrpcServiceBeanNames() {
    String[] beanNames = this.applicationContext.getBeanNamesForAnnotation(TrpcService.class);
    return Arrays.asList(beanNames);
  }

  @Override
  public Collection<ServerServiceDefinition> findTrpcServices() {
    Collection<String> beanNames = findTrpcServiceBeanNames();
    List<ServerServiceDefinition> definitions = Lists.newArrayListWithCapacity(beanNames.size());
    for (String beanName : beanNames) {
      Object trpcService = this.applicationContext.getBean(beanName);
      TrpcService serviceAnnotation = applicationContext.findAnnotationOnBean(beanName, TrpcService.class);
      GrpcServerStrategy grpcServerStrategy = new GrpcServerStrategy(serviceAnnotation.value(), trpcService);
      ServerServiceDefinition serviceDefinition = grpcServerStrategy.getServerDefintion();
      definitions.add(serviceDefinition);
    }
    return definitions;
  }


}
