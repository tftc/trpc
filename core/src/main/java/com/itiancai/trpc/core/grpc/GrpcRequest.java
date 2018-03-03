package com.itiancai.trpc.core.grpc;

import io.grpc.MethodDescriptor;

public class GrpcRequest<Req, Resp> {

  private MethodDescriptor<Req, Resp> methodDescriptor;

  private Req requestParam;

  private int callType;
  private int callTimeOut;

  public GrpcRequest(MethodDescriptor methodDescriptor, Req requestParam, int callType, int callTimeOut) {
    this.methodDescriptor = methodDescriptor;
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
