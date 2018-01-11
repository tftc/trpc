package com.itiancai.trpc.core.grpc.server;

import io.grpc.ServerServiceDefinition;

public interface GrpcProtocolExporter {
  ServerServiceDefinition export(Class<?> protocol, Object protocolImpl);
}
