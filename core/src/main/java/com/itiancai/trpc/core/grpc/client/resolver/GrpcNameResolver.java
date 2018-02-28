package com.itiancai.trpc.core.grpc.client.resolver;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.itiancai.trpc.core.registry.NotifyServiceListener;
import com.itiancai.trpc.core.registry.Registry;
import com.itiancai.trpc.core.registry.ServiceAddress;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;

import io.grpc.Attributes;
import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;
import io.grpc.Status;
import io.grpc.internal.GrpcUtil;
import io.grpc.internal.SharedResourceHolder;

public class GrpcNameResolver extends NameResolver {

  private Listener listener;
  private String group;
  private Registry registry;

  private boolean shutdown;
  private boolean resolving;

  private ScheduledExecutorService timerService;
  private ExecutorService executor;

  private Set<ServiceAddress> serviceSet;

  private NotifyServiceListener serviceListener = (addressSet) -> {
    notifyLoadBalance(addressSet);
  };

  public GrpcNameResolver(String group, Attributes params, Registry registry) {
    this.group = group;
    this.registry = registry;
    this.serviceSet = Sets.newHashSet();
    this.timerService = SharedResourceHolder.get(GrpcUtil.TIMER_SERVICE);
    this.executor = SharedResourceHolder.get(GrpcUtil.SHARED_CHANNEL_EXECUTOR);
  }

  @Override
  public String getServiceAuthority() {
    return group;
  }

  @Override
  public void start(Listener listener) {
    Preconditions.checkState(this.listener == null, "already started");
    this.listener = listener;
    this.listener = Preconditions.checkNotNull(listener, "listener");
    resolve();
  }

  @Override
  public void shutdown() {
    if (shutdown) {
      return;
    }
    shutdown = true;
    registry.unsubscribe(group, serviceListener);
  }

  @Override
  public final synchronized void refresh() {
    Preconditions.checkState(listener != null, "not started");
    resolve();
  }

  private void resolve() {
    if (resolving || shutdown) {
      return;
    }
    executor.execute(resolutionRunnable);
  }

  private final Runnable resolutionRunnable = new Runnable() {
    @Override
    public void run() {
      synchronized (GrpcNameResolver.this) {
        if (shutdown) {
          return;
        }
        resolving = true;
      }
      try {
        notifyLoadBalance(registry.discover(group));
        registry.subscribe(group, serviceListener);
      } finally {
        synchronized (GrpcNameResolver.this) {
          resolving = false;
        }
      }
    }
  };

  private void notifyLoadBalance(Set<ServiceAddress> addressSet) {
    if(!addressSet.isEmpty()) {
      if(serviceSet.size() != addressSet.size() || !serviceSet.containsAll(addressSet)) {
        List<EquivalentAddressGroup> equivalentAddressGroups = Lists.newArrayList();
        for (ServiceAddress serviceAddress : addressSet) {
          EquivalentAddressGroup addressGroup = new EquivalentAddressGroup(new InetSocketAddress(serviceAddress.getHost(), serviceAddress.getPort()), Attributes.EMPTY);
          equivalentAddressGroups.add(addressGroup);
        }
        GrpcNameResolver.this.listener.onAddresses(equivalentAddressGroups, Attributes.EMPTY);
        serviceSet = addressSet; //update serviceSet
      }
    } else {
      GrpcNameResolver.this.listener.onError(Status.UNAVAILABLE.withCause(new RuntimeException("UNAVAILABLE: NameResolver returned an empty list")));
      serviceSet = addressSet; //update serviceSet
    }
  }
}
