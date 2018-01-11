package com.itiancai.trpc.core.grpc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.grpc.MethodDescriptor;

@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface GrpcMethodType {

  MethodDescriptor.MethodType methodType() default MethodDescriptor.MethodType.UNARY;

  Class<?> requestClass();

  Class<?> responseClass();

}