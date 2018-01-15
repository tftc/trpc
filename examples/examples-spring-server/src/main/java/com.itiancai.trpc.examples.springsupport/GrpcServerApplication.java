package com.itiancai.trpc.examples.springsupport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GrpcServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(GrpcServerApplication.class, args);
  }
}