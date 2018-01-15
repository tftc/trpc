package com.itiancai.trpc.springsupport.client.factory.normal.resolver;

import com.itiancai.trpc.springsupport.client.config.TrpcChannelsProperties;

import java.net.URI;

import javax.annotation.Nullable;

import io.grpc.Attributes;
import io.grpc.NameResolver;
import io.grpc.NameResolverProvider;
import io.grpc.internal.GrpcUtil;

public class AddressChannelResolverFactory extends NameResolverProvider {

  private final TrpcChannelsProperties properties;

  public AddressChannelResolverFactory(TrpcChannelsProperties properties) {
    this.properties = properties;
  }

  @Nullable
  @Override
  public NameResolver newNameResolver(URI targetUri, Attributes params) {
    return new AddressChannelNameResolver(targetUri.toString(), properties.getChannel(targetUri.toString()), params, GrpcUtil.SHARED_CHANNEL_EXECUTOR);
  }

  @Override
  public String getDefaultScheme() {
    return "address";
  }

  @Override
  protected boolean isAvailable() {
    return true;
  }

  @Override
  protected int priority() {
    return 5;
  }
}
