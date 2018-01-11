package com.itiancai.trpc.examples.normal;

import com.itiancai.trpc.core.grpc.server.GrpcServerStrategy;
import com.itiancai.trpc.examples.model.HelloReply;
import com.itiancai.trpc.examples.model.HelloRequest;
import com.itiancai.trpc.examples.service.HelloService;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerServiceDefinition;
import io.grpc.stub.StreamObserver;

public class TrpcServer {

  public static void main(String[] args) throws Exception {
    GrpcServerStrategy gss = new GrpcServerStrategy(HelloService.class, new HelloServiceImpl());
    ServerServiceDefinition ssd = gss.getServerDefintion();

    Server server = ServerBuilder.forPort(50051)
            .addService(ssd)
            .build()
            .start();
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        // Use stderr here since the logger may have been reset by its JVM shutdown hook.
        System.err.println("*** shutting down gRPC server since JVM is shutting down");
        server.shutdown();
        System.err.println("*** server shut down");
      }
    });
    server.awaitTermination();

  }

  public static class HelloServiceImpl implements HelloService {

    @Override
    public HelloReply sayHello(HelloRequest request) {
      System.out.println("sayHello:"+request.getName());
      HelloReply reply = new HelloReply();
      reply.setMessage("hello " + request.getName());
      return reply;
    }

    @Override
    public void sayHelloServerStream(HelloRequest request, StreamObserver<HelloReply> response) {
      try {
        for (int i = 0; i < 10; i++) {
          HelloReply reply = new HelloReply();
          reply.setMessage(request.getName());
          response.onNext(reply);
        }
      } catch (Exception e) {
        response.onError(e);
      }
      response.onCompleted();
    }

    @Override
    public StreamObserver<HelloRequest> sayHelloClientStream(HelloReply response) {
      return null;
    }

    @Override
    public StreamObserver<HelloRequest> sayHelloBidiStream(StreamObserver<HelloReply> response) {
      return null;
    }
  }
}
