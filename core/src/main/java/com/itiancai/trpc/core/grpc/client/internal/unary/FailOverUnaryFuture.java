package com.itiancai.trpc.core.grpc.client.internal.unary;

import com.google.common.util.concurrent.ListenableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;
import io.grpc.internal.GrpcUtil;

public class FailOverUnaryFuture<Request, Response> extends ClientCall.Listener<Response>
    implements Runnable {

  private final static Logger logger = LoggerFactory.getLogger(FailOverUnaryFuture.class);

  private final ScheduledExecutorService scheduleRetryService = GrpcUtil.TIMER_SERVICE.create();

  private final AtomicInteger currentRetries = new AtomicInteger(0);

  private final MethodDescriptor<Request, Response> method;

  private CompletionFuture<Response> completionFuture;

  private ClientCall<Request, Response> clientCall;

  private Request request;
  private Response response;
  private Integer maxRetries;
  private boolean enabledRetry;
  private CallOptions callOptions;
  private Channel channel;

  public FailOverUnaryFuture(final MethodDescriptor<Request, Response> method) {
    this.method = method;
  }

  public void setCallOptions(CallOptions callOptions) {
    this.callOptions = callOptions;
  }

  public void setChannel(Channel channel) {
    this.channel = channel;
  }

  public void setMaxRetries(Integer maxRetries) {
    this.maxRetries = maxRetries;
    this.enabledRetry = maxRetries > 0 ? true : false;
  }

  public void setRequest(Request request) {
    this.request = request;
  }

  @Override
  public void onMessage(Response message) {
    if (this.response != null && !enabledRetry) {
      throw Status.INTERNAL.withDescription("More than one value received for unary call")
          .asRuntimeException();
    }
    this.response = message;
  }


  @Override
  public void onClose(Status status, Metadata trailers) {
    if (status.isOk()) {
      statusOk(trailers);
    } else {
      statusError(status, trailers);
    }
  }

  private void statusOk(Metadata trailers) {
    if (response == null) {
      completionFuture.setException(Status.INTERNAL
              .withDescription("No value received for unary call").asRuntimeException(trailers));
    }
    completionFuture.set(response);
  }


  private void statusError(Status status, Metadata trailers) {
    if (enabledRetry) {
      boolean retryHaveDone = this.retryHaveDone();
      if (retryHaveDone) {
        completionFuture.setException(status.asRuntimeException(trailers));
      } else {
        scheduleRetryService.execute(this);
        currentRetries.getAndIncrement();
      }
    } else {
      completionFuture.setException(status.asRuntimeException(trailers));
    }

  }

  /**
   * 重试次数验证
   * @return
   */
  private boolean retryHaveDone() {
    return currentRetries.get() >= maxRetries;
  }

  @Override
  public void run() {
    this.clientCall = channel.newCall(method, callOptions);
    this.completionFuture = new CompletionFuture<Response>(this.clientCall);
    this.clientCall.start(this, new Metadata());
    this.clientCall.sendMessage(request);
    this.clientCall.halfClose();
    this.clientCall.request(1);
  }


  public ListenableFuture<Response> getFuture() {
    return completionFuture;
  }

  public void cancel() {
    if (clientCall != null) {
      clientCall.cancel("User requested cancelation.", null);
    }
  }

}