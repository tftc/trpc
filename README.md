# trpc

## compile

### compile-generator

 generator：
 > 依据protobuf文件生成pojo类及interface，支持message、enum、service(any oneof暂不支持)。
 * parser: [protostuff-compile](https://github.com/protostuff/protostuff-compiler)解析protobuf文件
 * generator: 模板格式依据[jprotobuf](https://github.com/jhunters/jprotobuf), 使用StringTemplate

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

## core

### 序列化|反序列化

> [jprotobuf](https://github.com/jhunters/jprotobuf)

```java
public class DefaultMarshaller<T> implements MethodDescriptor.Marshaller<T> {

  private Codec<T> codec;

  public DefaultMarshaller(Class<T> clazz) {
    codec = ProtobufProxy.create(clazz);
  }

  @Override
  public InputStream stream(T value) {
    try {
      return new ByteArrayInputStream(codec.encode(value));
    } catch (IOException e) {
      return null;
    }
  }

  @Override
  public T parse(InputStream stream) {
    try {
      ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
      byte[] buff = new byte[100];
      int rc = 0;
      while ((rc = stream.read(buff, 0, 100)) > 0) {
        swapStream.write(buff, 0, rc);
      }
      return codec.decode(swapStream.toByteArray());
    } catch (IOException e) {
      return null;
    }
  }
}
```

### GrpcEngine

```java
public class GrpcEngine {
  
  //创建客户端
  public <T> T createClient(ClientDefinition clientDefinition) {
    // ...
    return null;
  }
  
  //构建server
  public Server createServer(List<ServiceDefinition> definitionList, int port) {
    // ...
    return null;
  }
  
}
```

### Registry(服务注册发现)

```java
public interface Registry {

  String REGISTRY_KEY = "gRPC";

  /**
   * 注册服务
   * @param port
   */
  void register(int port);

  /**
   * 订阅服务
   * @param group
   * @param listener
   */
  void subscribe(String group, NotifyServiceListener listener);

  /**
   * 取消订阅
   * @param group
   * @param listener
   */
  void unsubscribe(String group, NotifyServiceListener listener);



  /**
   * 查询服务
   */
  Set<ServiceAddress> discover(String group);

}
```

### TrpcClient
 * 熔断支持(HystrixCommand)
    ```
    super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(serviceName))//
            .andCommandKey(HystrixCommandKey.Factory.asKey(serviceName + ":" + methodName))//
            .andCommandPropertiesDefaults(
                HystrixCommandProperties.Setter().withCircuitBreakerRequestVolumeThreshold(20)// 10秒钟内至少19此请求失败，熔断器才发挥起作用
                    .withCircuitBreakerSleepWindowInMilliseconds(30000)// 熔断器中断请求30秒后会进入半打开状态,放部分流量过去重试
                    .withCircuitBreakerErrorThresholdPercentage(50)// 错误率达到50开启熔断保护
                    .withExecutionTimeoutEnabled(false)// 禁用这里的超时
                    .withFallbackEnabled(isEnabledFallBack))//
            .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(100)
                .withAllowMaximumSizeToDivergeFromCoreSize(true).withMaximumSize(Integer.MAX_VALUE)));
    ```
 * 失败重试

## spring-support

 * TrpcServer声明周期管理
 * TrpcClient依赖注入
 * 自定义Interceptor
 * 分布式追踪(sleuth zipkin)
 
## 限流 

## 监控

## gateway

> 协议转换 路由 限流 授权 黑名单 ...