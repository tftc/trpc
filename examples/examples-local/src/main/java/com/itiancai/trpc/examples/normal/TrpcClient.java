package com.itiancai.trpc.examples.normal;

import com.itiancai.trpc.core.grpc.client.GrpcClientStrategy;
import com.itiancai.trpc.examples.model.HelloReply;
import com.itiancai.trpc.examples.model.HelloRequest;
import com.itiancai.trpc.examples.service.HelloService;

import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class TrpcClient {

  public static void main(String[] args) throws InterruptedException {
    ManagedChannel channel = ManagedChannelBuilder.forAddress("192.168.101.138", 9090)
            .usePlaintext(true)
            .build();
    GrpcClientStrategy gcs = new GrpcClientStrategy(HelloService.class, channel);
    HelloService helloService = (HelloService) gcs.getGrpcClient();

    HelloRequest request = new HelloRequest();
    request.setName("bao");
    HelloReply reply = helloService.sayHello(request);
    System.out.println(reply.getMessage());
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }
}
