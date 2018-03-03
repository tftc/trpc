package com.itiancai.trpc.core.grpc;

import io.grpc.MethodDescriptor;

public class GrpcRequest<Req, Resp> {

  private MethodDescriptor<Req, Resp> methodDescriptor;

  private Class<Req> requestClass;
  private Class<Resp> responseClass;

  private Req requestParam;

  private int callType;
  private int callTimeOut;

  public GrpcRequest(MethodDescriptor<Req, Resp> methodDescriptor, Class<Req> requestClass, Class<Resp> responseClass, Req requestParam, int callType, int callTimeOut) {
    this.methodDescriptor = methodDescriptor;
    this.requestClass = requestClass;
    this.responseClass = responseClass;
    this.requestParam = requestParam;
    this.callType = callType;
    this.callTimeOut = callTimeOut;
  }

  public MethodDescriptor<Req, Resp> getMethodDescriptor() {
    return methodDescriptor;
  }

  public void setMethodDescriptor(MethodDescriptor<Req, Resp> methodDescriptor) {
    this.methodDescriptor = methodDescriptor;
  }

  public Class<Req> getRequestClass() {
    return requestClass;
  }

  public void setRequestClass(Class<Req> requestClass) {
    this.requestClass = requestClass;
  }

  public Class<Resp> getResponseClass() {
    return responseClass;
  }

  public void setResponseClass(Class<Resp> responseClass) {
    this.responseClass = responseClass;
  }

  public Req getRequestParam() {
    return requestParam;
  }

  public void setRequestParam(Req requestParam) {
    this.requestParam = requestParam;
  }

  public int getCallType() {
    return callType;
  }

  public void setCallType(int callType) {
    this.callType = callType;
  }

  public int getCallTimeOut() {
    return callTimeOut;
  }

  public void setCallTimeOut(int callTimeOut) {
    this.callTimeOut = callTimeOut;
  }
}
