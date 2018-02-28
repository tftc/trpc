package com.itiancai.trpc.core.grpc.server.finder;

import com.itiancai.trpc.core.grpc.annotation.ServiceDefinition;

import java.util.List;

public interface ServiceDefinitionFinder {

  List<ServiceDefinition> findTrpcServices();

}