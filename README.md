# trpc

## compile

### compile-generator

> generator：依据protobuf文件生成pojo类及interface，支持message、enum、service(any oneof暂不支持)。

proto文件：
``` proto
message HelloRequest {
    string name = 1;
}

message HelloReply {
    string message = 1;
}

enum PhoneType {
    MOBILE = 0;
    HOME = 1;
    WORK = 2;
}

service HelloService {
    rpc sayHello (com.itiancai.trpc.examples.model.HelloRequest) returns (com.itiancai.trpc.examples.model.HelloReply) {}
}

```

生成：
```java
public final class HelloRequest {
  @Protobuf(fieldType = FieldType.STRING, required = false, order = 1)
  private String name;


  public String getName() {
      return this.name;
  }

  public void setName(String name) {
      this.name = name;
  }


}

public enum PhoneType implements EnumReadable {

  MOBILE(0),
  HOME(1),
  WORK(2),
  UNRECOGNIZED(-1);

  private final int value;

  private PhoneType(int n) {
    this.value = n;
  }

  @Override
  public int value() {
    return value;
  }
}

public interface HelloService {

  @GrpcMethodType(methodType = MethodDescriptor.MethodType.UNARY, requestClass=com.itiancai.trpc.examples.model.HelloRequest.class, responseClass=com.itiancai.trpc.examples.model.HelloReply.class)
  com.itiancai.trpc.examples.model.HelloReply sayHello(com.itiancai.trpc.examples.model.HelloRequest request);

}

```

### compile-plugin-maven

> generator-maven插件：
> 读取`src/main/proto`目录下所有proto文件，解析生成pojo类及interface到`target/generated-sources/proto`目录。

### 序列化&反序列化

> 详见[jprotobuf](https://github.com/jhunters/jprotobuf)

## core



## gateway

> 协议转换 路由 限流 授权 黑名单 ...

## 分布式追踪 zipkin

## 监控