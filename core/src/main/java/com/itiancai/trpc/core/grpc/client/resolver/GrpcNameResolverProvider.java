package com.itiancai.trpc.core.grpc.client.resolver;

import com.itiancai.trpc.core.registry.Registry;

import java.net.URI;

import javax.annotation.Nullable;

import io.grpc.Attributes;
import io.grpc.NameResolver;
import io.grpc.NameResolverProvider;

public class GrpcNameResolverProvider extends NameResolverProvider {

  private Registry registry;

  public GrpcNameResolverProvider(Registry registry) {
    this.registry = registry;
  }

  @Nullable
  @Override
  public NameResolver newNameResolver(URI targetUri, Attributes params) {
    return new GrpcNameResolver(targetUri.toString(), params, registry);
  }

  @Override
  protected boolean isAvailable() {
    return true;
  }

  @Override
  protected int priority() {
    return 5;
  }

  @Override
  public String getDefaultScheme() {
    return "address";
  }

}