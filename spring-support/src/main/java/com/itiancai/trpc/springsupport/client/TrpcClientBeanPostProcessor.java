package com.itiancai.trpc.springsupport.client;

import com.google.common.collect.Maps;

import com.itiancai.trpc.core.grpc.GrpcEngine;
import com.itiancai.trpc.springsupport.annotation.TrpcClient;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TrpcClientBeanPostProcessor implements org.springframework.beans.factory.config.BeanPostProcessor {

  private Map<String, List<Class>> beansToProcess = Maps.newHashMap();

  private Map<String, Object> trpcClientMap = Maps.newHashMap();

  @Autowired
  private GrpcEngine grpcEngine;

  public TrpcClientBeanPostProcessor() {
  }

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    Class clazz = bean.getClass();
    do {
      for (Field field : clazz.getDeclaredFields()) {
        if (field.isAnnotationPresent(TrpcClient.class)) {
          if (!beansToProcess.containsKey(beanName)) {
            beansToProcess.put(beanName, new ArrayList<Class>());
          }
          beansToProcess.get(beanName).add(clazz);
        }
      }
      clazz = clazz.getSuperclass();
    } while (clazz != null);
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    if (beansToProcess.containsKey(beanName)) {
      Object target = getTargetBean(bean);
      for (Class clazz : beansToProcess.get(beanName)) {
        for (Field field : clazz.getDeclaredFields()) {
          TrpcClient annotation = AnnotationUtils.getAnnotation(field, TrpcClient.class);
          if (null != annotation) {

            Object trpcClient = getPrpcClient(annotation);
            ReflectionUtils.makeAccessible(field);
            ReflectionUtils.setField(field, target, trpcClient);
          }
        }
      }
    }
    return bean;
  }

  private Object getTargetBean(Object bean) {
    Object target = bean;
    while (AopUtils.isAopProxy(target)) {
      try {
        target = ((Advised) target).getTargetSource().getTarget();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return target;
  }

  private Object getPrpcClient(TrpcClient annotation) {
    Object grpcClient;
    if (trpcClientMap.containsKey(annotation.group())) {
      grpcClient = trpcClientMap.get(annotation.group());
    } else {
      grpcClient = grpcEngine.createClient(annotation.group(), annotation.clazz());
      trpcClientMap.put(annotation.group(), grpcClient);
    }
    return grpcClient;
  }


}