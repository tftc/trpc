package com.itiancai.trpc.core.grpc.client;

import io.grpc.Channel;

public interface GrpcProtocolClient<T> {

  T getGrpcClient(Channel channel, int callType, int callTimeout);
}
