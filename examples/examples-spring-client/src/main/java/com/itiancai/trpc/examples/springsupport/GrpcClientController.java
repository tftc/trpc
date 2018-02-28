package com.itiancai.trpc.examples.springsupport;

import com.itiancai.trpc.examples.model.HelloRequest;
import com.itiancai.trpc.examples.service.HelloService;
import com.itiancai.trpc.springsupport.annotation.TrpcClient;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GrpcClientController {

  @TrpcClient(group = "cloud-trpc-server", clazz = HelloService.class)
  private HelloService helloService;

  @RequestMapping("/")
  public String printMessage(@RequestParam(defaultValue = "trpc") String name) {
    HelloRequest request = new HelloRequest();
    request.setName(name);
    return helloService.sayHello(request).getMessage();
  }
}