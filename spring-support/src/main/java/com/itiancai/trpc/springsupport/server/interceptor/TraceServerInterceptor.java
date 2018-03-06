package com.itiancai.trpc.springsupport.server.interceptor;

import com.itiancai.trpc.springsupport.util.SpanUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.SpanExtractor;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.util.ExceptionUtils;

import io.grpc.ForwardingServerCall;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;

public class TraceServerInterceptor implements ServerInterceptor {

  private final static Logger log = LoggerFactory.getLogger(TraceServerInterceptor.class);

  private Tracer tracer;

  private SpanExtractor<Metadata> spanExtractor;
  private static final String GRPC_COMPONENT = "gRPC";

  public TraceServerInterceptor(Tracer tracer, SpanExtractor<Metadata> spanExtractor) {
    this.tracer = tracer;
    this.spanExtractor = spanExtractor;
  }

  @Override
  public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(final ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {

    //span create
    tracer.continueSpan(spanExtractor.joinTrace(headers));
    Span gRPCSpan = tracer.createSpan(SpanUtils.buildSpanName(call.getMethodDescriptor()));

    //span sr
    gRPCSpan.logEvent(Span.SERVER_RECV);
    gRPCSpan.tag(Span.SPAN_LOCAL_COMPONENT_TAG_NAME, GRPC_COMPONENT);

    return next.startCall(new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {

      @SuppressWarnings("ConstantConditions")
      @Override
      public void close(Status status, Metadata trailers) {
        Status.Code statusCode = status.getCode();
        try {
          if (!status.isOk()) {
            tracer.addTag(Span.SPAN_ERROR_TAG_NAME, ExceptionUtils.getExceptionMessage(status.getCause()));
          }
          super.close(status, trailers);
        } catch (Throwable t) {
          log.warn("call had closed, msg:{}", t.getMessage());
        } finally {
          //span ss
          Span span = tracer.continueSpan(gRPCSpan);
          span.logEvent(Span.SERVER_SEND);
          tracer.addTag("gRPC status code", String.valueOf(statusCode.value()));
          tracer.close(span);
        }
      }
    }, headers);
  }
}