package com.itiancai.trpc.springsupport.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.grpc.stub.AbstractStub;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface TrpcClient {

  String group();

  Class<?> clazz();

  int retries() default 0;

  String[] retryMethods() default {};

  boolean async() default true;

  boolean fallback() default false;

  String[] fallBackMethods() default {};

  int callTimeout() default 5000;
}