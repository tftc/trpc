package com.itiancai.trpc.core.grpc.server;

import com.itiancai.trpc.core.grpc.server.internal.DefaultProxyExporter;

import io.grpc.ServerServiceDefinition;

public class GrpcServerStrategy {

  private final GrpcProtocolExporter exporter;

  private final Class<?> protocolClass;

  private final Object protocolImpl;

  public GrpcServerStrategy(Class<?> protocolClass, Object protocolImpl) {
    this.protocolClass = protocolClass;
    this.exporter = new DefaultProxyExporter();
    this.protocolImpl = protocolImpl;
  }

  public ServerServiceDefinition getServerDefintion() {
    return exporter.export(protocolClass, protocolImpl);
  }
}
