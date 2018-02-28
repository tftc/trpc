package com.itiancai.trpc.core.registry;

import java.util.Set;

public interface Registry {

  String REGISTRY_KEY = "gRPC";

  /**
   * 注册服务
   * @param port
   */
  void register(int port);

  /**
   * 订阅服务
   * @param group
   * @param listener
   */
  void subscribe(String group, NotifyServiceListener listener);

  /**
   * 取消订阅
   * @param group
   * @param listener
   */
  void unsubscribe(String group, NotifyServiceListener listener);



  /**
   * 查询服务
   */
  Set<ServiceAddress> discover(String group);

}
