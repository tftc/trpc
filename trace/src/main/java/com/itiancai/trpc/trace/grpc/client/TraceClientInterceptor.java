package com.itiancai.trpc.trace.grpc.client;


import com.itiancai.trpc.trace.util.SpanUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.SpanInjector;
import org.springframework.cloud.sleuth.Tracer;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ClientInterceptors;
import io.grpc.ForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;
import io.grpc.StatusException;

public class TraceClientInterceptor implements ClientInterceptor {

  private static final Logger log = LoggerFactory.getLogger(TraceClientInterceptor.class);

  private Tracer tracer;
  private final SpanInjector<Metadata> spanInjector;

  public TraceClientInterceptor(Tracer tracer, SpanInjector<Metadata> spanInjector) {
    this.tracer = tracer;
    this.spanInjector = spanInjector;
  }

  @Override
  public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(final MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
    return new ClientInterceptors.CheckedForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
      @Override
      protected void checkedStart(Listener<RespT> responseListener, Metadata headers)
              throws StatusException {
        //create span
        String spanName = SpanUtils.buildSpanName(method);
        final Span grpcSpan = tracer.createSpan(spanName);

        //span cs
        spanInjector.inject(grpcSpan, headers);
        grpcSpan.logEvent(Span.CLIENT_SEND);

        Listener<RespT> tracingResponseListener = new ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(responseListener) {

          @Override
          public void onClose(Status status, Metadata trailers) {
            try {
              if (status.isOk()) {
                log.debug("Call finish success");
              } else {
                log.warn("Call finish failed", status.getDescription());
              }
              delegate().onClose(status, trailers);
            } finally {
              //span cr
              Span span = tracer.continueSpan(grpcSpan);
              span.logEvent(Span.CLIENT_RECV);
              tracer.close(span);
            }
          }
        };
        delegate().start(tracingResponseListener, headers);
      }
    };
  }
}