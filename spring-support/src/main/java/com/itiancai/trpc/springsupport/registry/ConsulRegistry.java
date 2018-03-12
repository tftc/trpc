package com.itiancai.trpc.springsupport.registry;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.health.model.Check;
import com.ecwid.consul.v1.health.model.HealthService;
import com.itiancai.trpc.core.registry.NotifyServiceListener;
import com.itiancai.trpc.core.registry.Registry;
import com.itiancai.trpc.core.registry.ServiceAddress;
import com.itiancai.trpc.core.utils.GrpcUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.event.HeartbeatEvent;
import org.springframework.cloud.client.discovery.event.HeartbeatMonitor;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties;
import org.springframework.cloud.consul.serviceregistry.ConsulAutoRegistration;
import org.springframework.cloud.consul.serviceregistry.ConsulAutoServiceRegistration;
import org.springframework.context.event.EventListener;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

public class ConsulRegistry implements Registry {

  private final static Logger logger = LoggerFactory.getLogger(ConsulRegistry.class);

  @Autowired
  private ConsulClient consulClient;
  @Autowired
  private ConsulAutoServiceRegistration consulAutoServiceRegistration;
  @Autowired
  private ConsulDiscoveryProperties consulDiscoveryProperties;
  @Autowired
  private ConsulAutoRegistration consulAutoRegistration;

  public ConsulRegistry() {
  }

  @Autowired
  private DiscoveryClient discoveryClient;

  private HeartbeatMonitor monitor = new HeartbeatMonitor();

  private final ConcurrentMap<String, Set<NotifyServiceListener>> subscribed = Maps.newConcurrentMap();

  @Override
  public void register(int port) {
    consulAutoRegistration.getService().getTags().add(Registry.REGISTRY_KEY + "=" + port);
    consulDiscoveryProperties.setRegister(true);
    consulAutoServiceRegistration.start();
  }

  @Override
  public void subscribe(String group, NotifyServiceListener listener) {
    Set<NotifyServiceListener> listenerSet = subscribed.get(group);
    if(listenerSet == null) {
      listenerSet = new HashSet<>();
      subscribed.put(group, listenerSet);
    }
    listenerSet.add(listener);
  }

  @Override
  public void unsubscribe(String group, NotifyServiceListener listener) {
    Set<NotifyServiceListener> listenerSet = subscribed.get(group);
    if(listenerSet != null) {
      listenerSet.remove(listener);
    }
  }

  @Override
  public Set<ServiceAddress> discover(String group) {

    clearUnPassingService(group);

    Set<ServiceAddress> addressSet = Sets.newHashSet();
    List<ServiceInstance> serviceInstanceList = discoveryClient.getInstances(group);
    for (ServiceInstance serviceInstance : serviceInstanceList) {
      Map<String, String> metadata = serviceInstance.getMetadata();
      if (metadata.get(REGISTRY_KEY) != null) {
        Integer port = Integer.valueOf(metadata.get(REGISTRY_KEY));
        if(GrpcUtils.isIP(serviceInstance.getHost())) {

        } else {

        }
        addressSet.add(new ServiceAddress(serviceInstance.getHost(), port));
      }
    }
    return addressSet;
  }

  @EventListener(HeartbeatEvent.class)
  public void heartbeat(HeartbeatEvent event) {

    if(monitor.update(event.getValue())) {
      for(String group : subscribed.keySet()) {

        Set<ServiceAddress> addressSet = discover(group);
        for (NotifyServiceListener listener : subscribed.get(group)) {
          listener.notify(addressSet);
        }
      }
    }
  }

  /**
   * 清理无用的服务
   * @param id
   */
  private void clearUnPassingService(String id) {
    try {
      List<HealthService> response = consulClient.getHealthServices(id, false, null).getValue();
      for (HealthService service : response) {
        // 创建一个用来剔除无效实例的ConsulClient，连接到无效实例注册的agent
        ConsulClient clearClient = new ConsulClient(service.getNode().getAddress(), 8500);
        service.getChecks().forEach(check -> {
          if (check.getStatus() != Check.CheckStatus.PASSING) {
            logger.info("unregister group:{}, serviceID:{}", id, check.getServiceId());
            clearClient.agentServiceDeregister(check.getServiceId());
          }
        });
      }
    } catch (Throwable t) {
      logger.warn("agentServiceDeregister error, group:"+id, t);
    }

  }

}
