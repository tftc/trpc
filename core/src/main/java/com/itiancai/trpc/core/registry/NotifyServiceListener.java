package com.itiancai.trpc.core.registry;

import java.util.Set;

public interface NotifyServiceListener {

  void notify(Set<ServiceAddress> addressSet);
}