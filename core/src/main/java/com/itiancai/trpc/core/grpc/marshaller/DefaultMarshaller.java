package com.itiancai.trpc.core.grpc.marshaller;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.grpc.MethodDescriptor;

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
