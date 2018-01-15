package com.itiancai.trpc.springsupport.client.factory.normal.resolver;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import com.itiancai.trpc.springsupport.client.config.TrpcChannelProperties;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.annotation.concurrent.GuardedBy;

import io.grpc.Attributes;
import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;
import io.grpc.Status;
import io.grpc.internal.SharedResourceHolder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AddressChannelNameResolver extends NameResolver {

  private final String name;
  private final TrpcChannelProperties properties;
  private final Attributes attributes;

  private final SharedResourceHolder.Resource<ExecutorService> executorResource;
  @GuardedBy("this")
  private boolean shutdown;
  @GuardedBy("this")
  private ExecutorService executor;
  @GuardedBy("this")
  private boolean resolving;
  @GuardedBy("this")
  private Listener listener;

  public AddressChannelNameResolver(String name, TrpcChannelProperties properties, Attributes attributes, SharedResourceHolder.Resource<ExecutorService> executorResource) {
    this.name = name;
    this.properties = properties;
    this.attributes = attributes;
    this.executorResource = executorResource;
  }

  @Override
  public String getServiceAuthority() {
    return name;
  }

  @Override
  public final synchronized void start(Listener listener) {
    Preconditions.checkState(this.listener == null, "already started");
    this.listener = listener;
    executor = SharedResourceHolder.get(executorResource);
    this.listener = Preconditions.checkNotNull(listener, "listener");
    resolve();
  }

  @SuppressWarnings("unchecked")
  private void replace(List list, int max, Object defaultValue) {
    for (int i = 0; i < list.size(); i++) {
      if (list.get(i) == null) {
        list.set(i, defaultValue);
      }
    }
    for (int i = list.size(); i < max; i++) {
      list.add(defaultValue);
    }
  }

  @Override
  public final synchronized void refresh() {
    Preconditions.checkState(listener != null, "not started");
    resolve();
  }

  private final Runnable resolutionRunnable = new Runnable() {
    @Override
    public void run() {
      Listener savedListener;
      synchronized (AddressChannelNameResolver.this) {
        if (shutdown) {
          return;
        }
        savedListener = listener;
        resolving = true;
      }
      try {
        int max = Math.max(properties.getHost().size(), properties.getPort().size());
        replace(properties.getHost(), max, TrpcChannelProperties.DEFAULT_HOST);
        replace(properties.getPort(), max, TrpcChannelProperties.DEFAULT_PORT);

        if (properties.getHost().size() != properties.getPort().size()) {
          log.error("config gRPC server {} error, hosts length isn't equals ports length,hosts [{}], ports [{}]", properties.getHost(), properties.getPort());
          savedListener.onError(Status.UNAVAILABLE.withCause(new RuntimeException("gRPC config error")));
          return;
        }

        List<EquivalentAddressGroup> equivalentAddressGroups = Lists.newArrayList();
        for (int i = 0; i < properties.getHost().size(); i++) {
          String host = properties.getHost().get(i);
          Integer port = properties.getPort().get(i);
          log.info("Found gRPC server {} {}:{}", name, host, port);
          EquivalentAddressGroup addressGroup = new EquivalentAddressGroup(new InetSocketAddress(host, port), Attributes.EMPTY);
          equivalentAddressGroups.add(addressGroup);
        }
        savedListener.onAddresses(equivalentAddressGroups, Attributes.EMPTY);
      } finally {
        synchronized (AddressChannelNameResolver.this) {
          resolving = false;
        }
      }
    }
  };

  @GuardedBy("this")
  private void resolve() {
    if (resolving || shutdown) {
      return;
    }
    executor.execute(resolutionRunnable);
  }

  @Override
  @GuardedBy("this")
  public void shutdown() {
    if (shutdown) {
      return;
    }
    shutdown = true;
    if (executor != null) {
      executor = SharedResourceHolder.release(executorResource, executor);
    }
  }
}
