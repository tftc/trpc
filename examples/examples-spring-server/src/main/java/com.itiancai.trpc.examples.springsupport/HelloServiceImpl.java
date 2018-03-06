package com.itiancai.trpc.examples.springsupport;

import com.itiancai.trpc.examples.model.HelloReply;
import com.itiancai.trpc.examples.model.HelloRequest;
import com.itiancai.trpc.examples.service.HelloService;
import com.itiancai.trpc.springsupport.annotation.TrpcService;

import io.grpc.stub.StreamObserver;

@TrpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {

    @Override
    public HelloReply sayHello(HelloRequest request) {
      System.out.println("sayHello:"+request.getName());
      try {
        Thread.sleep(80);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      HelloReply reply = new HelloReply();
      reply.setMessage("hello " + request.getName());
      return reply;
    }

    @Override
    public void sayHelloServerStream(HelloRequest request, StreamObserver<HelloReply> response) {
//      try {
//        for (int i = 0; i < 10; i++) {
//          HelloReply reply = new HelloReply();
//          reply.setMessage(request.getName());
//          response.onNext(reply);
//        }
//      } catch (Exception e) {
//        response.onError(e);
//      }
//      response.onCompleted();
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