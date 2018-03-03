package com.itiancai.trpc.core.grpc.client.internal.unary;

import com.google.common.util.concurrent.AbstractFuture;

import io.grpc.ClientCall;

public class CompletionFuture<T> extends AbstractFuture<T> {

  private final ClientCall<?, T> call;

  CompletionFuture(ClientCall<?, T> call) {
    this.call = call;
  }

  @Override
  protected void interruptTask() {
    call.cancel("CompletionFuture was cancelled", null);
  }

  @Override
  protected boolean set(T resp) {
    return super.set(resp);
  }

  @Override
  protected boolean setException(Throwable throwable) {
    return super.setException(throwable);
  }

}
