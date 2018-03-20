package com.itiancai.trpc.springsupport.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryProperties;
import org.springframework.cloud.zookeeper.discovery.ZookeeperServiceDiscovery;
import org.springframework.cloud.zookeeper.serviceregistry.ZookeeperAutoServiceRegistration;

public class ZookeeperRegistry extends AbstractRegistry {

  private final static Logger logger = LoggerFactory.getLogger(ConsulRegistry.class);

  @Autowired
  private ZookeeperDiscoveryProperties properties;
  @Autowired
  private ZookeeperAutoServiceRegistration zookeeperAutoServiceRegistration;
  @Autowired
  private ZookeeperServiceDiscovery zookeeperServiceDiscovery;

  @Override
  public void register(int port) {
    properties.getMetadata().put("gRPC", String.valueOf(port));
    properties.setRegister(true);
    zookeeperServiceDiscovery.setRegister(true);
    zookeeperServiceDiscovery.setPort(Integer.parseInt("1" + port));
    zookeeperAutoServiceRegistration.start();
    logger.info("ZookeeperRegistry port:{} start...", port);
  }
}
