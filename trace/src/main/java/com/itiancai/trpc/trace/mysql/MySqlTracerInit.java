package com.itiancai.trpc.trace.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Tracer;

import javax.annotation.PostConstruct;

public class MySqlTracerInit {

  @Autowired
  private Tracer tracer;

  @PostConstruct
  public void init() {
    TracingStatementInterceptor.setTracer(tracer);
  }
}