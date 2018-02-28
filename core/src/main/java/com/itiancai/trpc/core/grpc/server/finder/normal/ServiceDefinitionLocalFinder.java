package com.itiancai.trpc.core.grpc.server.finder.normal;

import com.itiancai.trpc.core.grpc.annotation.ServiceDefinition;
import com.itiancai.trpc.core.grpc.server.finder.ServiceDefinitionFinder;

import java.util.List;

public class ServiceDefinitionLocalFinder implements ServiceDefinitionFinder {

  private List<ServiceDefinition> serviceDefinitionList;

  public ServiceDefinitionLocalFinder(List<ServiceDefinition> serviceDefinitionList) {
    this.serviceDefinitionList = serviceDefinitionList;
  }

  @Override
  public List<ServiceDefinition> findTrpcServices() {
    return serviceDefinitionList;
  }
}
