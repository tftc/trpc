package com.itiancai.trpc.springsupport.server.factory;

import io.grpc.Server;
import io.grpc.ServerServiceDefinition;

public interface TrpcServerFactory {

  Server createServer();

  String getAddress();

  int getPort();

  void addService(ServerServiceDefinition service);

}