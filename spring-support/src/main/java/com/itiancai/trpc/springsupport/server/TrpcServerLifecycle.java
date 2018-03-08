package com.itiancai.trpc.springsupport.server;

import com.google.common.collect.Lists;

import com.itiancai.trpc.core.grpc.GrpcEngine;
import com.itiancai.trpc.core.grpc.annotation.ServiceDefinition;
import com.itiancai.trpc.springsupport.annotation.TrpcService;
import com.itiancai.trpc.springsupport.server.interceptor.GlobalServerInterceptorRegistry;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.SmartLifecycle;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import io.grpc.Server;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TrpcServerLifecycle implements SmartLifecycle, ApplicationContextAware {

  private ApplicationContext applicationContext;

  private static AtomicInteger serverCounter = new AtomicInteger(-1);

  private volatile Server server;
  private volatile int phase = Integer.MAX_VALUE;
  private int port;
  private GrpcEngine grpcEngine;

  public TrpcServerLifecycle(GrpcEngine grpcEngine, TrpcServerProperties serverProperties) {
    this.grpcEngine = grpcEngine;
    this.port = serverProperties.getPort();
  }

  @Override
  public void start() {
    try {
      createAndStartGrpcServer();
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public void stop() {
    stopAndReleaseGrpcServer();
  }

  @Override
  public void stop(Runnable callback) {
    this.stop();
    callback.run();
  }

  @Override
  public boolean isRunning() {
    return this.server == null ? false : !this.server.isShutdown();
  }

  @Override
  public int getPhase() {
    return this.phase;
  }

  @Override
  public boolean isAutoStartup() {
    return true;
  }

  protected void createAndStartGrpcServer() throws IOException {
    Server localServer = this.server;
    if (localServer == null) {
      this.server = grpcEngine.createServer(findTrpcServices(), port);
      this.server.start();
      log.info("gRPC Server started, listening on address: " + ", port: " + this.port);

      Thread awaitThread = new Thread("container-" + (serverCounter.incrementAndGet())) {

        @Override
        public void run() {
          try {
            TrpcServerLifecycle.this.server.awaitTermination();
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          }
        }

      };
      awaitThread.setDaemon(false);
      awaitThread.start();
    }
  }

  protected void stopAndReleaseGrpcServer() {
    Server localServer = this.server;
    if (localServer != null) {
      localServer.shutdown();
      this.server = null;
      log.info("gRPC server stopped");
    }
  }

  public List<ServiceDefinition> findTrpcServices() {
    String[] beanNames = this.applicationContext.getBeanNamesForAnnotation(TrpcService.class);

    List<ServiceDefinition> definitions = Lists.newArrayListWithCapacity(beanNames.length);
    for (String beanName : beanNames) {
      Object trpcService = this.applicationContext.getBean(beanName);
      TrpcService serviceAnnotation = applicationContext.findAnnotationOnBean(beanName, TrpcService.class);
      ServiceDefinition serviceDefinition = new ServiceDefinition(serviceAnnotation.value(), trpcService);
      GlobalServerInterceptorRegistry globalServerInterceptorRegistry = applicationContext.getBean(GlobalServerInterceptorRegistry.class);
      serviceDefinition.addInterceptors(globalServerInterceptorRegistry.getServerInterceptors());
      definitions.add(serviceDefinition);
    }
    return definitions;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }
}