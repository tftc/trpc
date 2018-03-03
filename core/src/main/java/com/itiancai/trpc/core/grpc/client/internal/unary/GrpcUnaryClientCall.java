package com.itiancai.trpc.core.grpc.client.internal.unary;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutionException;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.MethodDescriptor;
import io.grpc.Status;

public interface GrpcUnaryClientCall<Req, Resp> {

  Map<MethodDescriptor, FailOverUnaryFuture> FAILOVER_UNARAY_FUTRURES =
          Collections.synchronizedMap(
                  new WeakHashMap<MethodDescriptor, FailOverUnaryFuture>()
          );

  public  ListenableFuture<Resp> unaryFuture(Req request,
                                               MethodDescriptor<Req, Resp> method);

  public Resp blockingUnaryResult(Req request, MethodDescriptor<Req, Resp> method);

  public static <Req, Resp> GrpcUnaryClientCall create(final Channel channel, final Integer retryOptions) {
    final CallOptions callOptions = CallOptions.DEFAULT;
    return new GrpcUnaryClientCall<Req, Resp>() {

      private FailOverUnaryFuture<Req, Resp> newFailOverUnaryFuture(
          final MethodDescriptor<Req, Resp> method) {
        if (FAILOVER_UNARAY_FUTRURES.containsKey(method)) {
          return FAILOVER_UNARAY_FUTRURES.get(method);
        } else {
          return new FailOverUnaryFuture<Req, Resp>(method);
        }
      }


      @Override
      public ListenableFuture<Resp> unaryFuture(Req request, MethodDescriptor<Req, Resp> method) {
        FailOverUnaryFuture<Req, Resp> retryCallListener = newFailOverUnaryFuture(method);
        retryCallListener.setRequest(request);
        retryCallListener.setMaxRetries(retryOptions);
        retryCallListener.setChannel(channel);
        retryCallListener.setCallOptions(callOptions);
        retryCallListener.run();
        return retryCallListener.getFuture();
      }

      @Override
      public Resp blockingUnaryResult(Req request, MethodDescriptor<Req, Resp> method) {
        FailOverUnaryFuture<Req, Resp> retryCallListener = newFailOverUnaryFuture(method);
        retryCallListener.setRequest(request);
        retryCallListener.setMaxRetries(retryOptions);
        retryCallListener.setChannel(channel);
        retryCallListener.setCallOptions(callOptions);
        try {
          retryCallListener.run();
          return retryCallListener.getFuture().get();
        } catch (InterruptedException e) {
          retryCallListener.cancel();
          throw Status.CANCELLED.withCause(e).asRuntimeException();
        } catch (ExecutionException e) {
          retryCallListener.cancel();
          throw Status.fromThrowable(e).asRuntimeException();
        }
      }
    };
  }

}