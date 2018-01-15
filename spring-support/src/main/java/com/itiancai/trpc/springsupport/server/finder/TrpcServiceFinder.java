package com.itiancai.trpc.springsupport.server.finder;

import java.util.Collection;

import io.grpc.ServerServiceDefinition;

public interface TrpcServiceFinder {

  Collection<ServerServiceDefinition> findTrpcServices();

}