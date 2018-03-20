package com.itiancai.trpc.springsupport.registry;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.health.model.Check;
import com.ecwid.consul.v1.health.model.HealthService;
import com.itiancai.trpc.core.registry.Registry;
import com.itiancai.trpc.core.registry.ServiceAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties;
import org.springframework.cloud.consul.serviceregistry.ConsulAutoRegistration;
import org.springframework.cloud.consul.serviceregistry.ConsulAutoServiceRegistration;

import java.util.List;
import java.util.Set;

public class ConsulRegistry extends AbstractRegistry {

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

  @Override
  public void register(int port) {
    consulAutoRegistration.getService().getTags().add(Registry.REGISTRY_KEY + "=" + port);
    consulDiscoveryProperties.setRegister(true);
    consulAutoServiceRegistration.start();
  }

  @Override
  public Set<ServiceAddress> discover(String group) {
    clearUnPassingService(group);
    return super.discover(group);
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
