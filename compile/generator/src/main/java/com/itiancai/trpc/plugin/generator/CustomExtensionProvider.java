package com.itiancai.trpc.plugin.generator;

import com.itiancai.trpc.plugin.generator.model.Protobuf;

import io.protostuff.compiler.model.Field;
import io.protostuff.compiler.model.FieldModifier;
import io.protostuff.compiler.model.Message;
import io.protostuff.compiler.model.ServiceMethod;
import io.protostuff.generator.java.JavaExtensionProvider;
import io.protostuff.generator.java.UserTypeUtil;

public class CustomExtensionProvider extends JavaExtensionProvider {

  public CustomExtensionProvider() {
    super();
    registerProperty(ServiceMethod.class, "javaArgTypeFullName", serviceMethod -> {
      Message argType = serviceMethod.getArgType();
      return UserTypeUtil.getCanonicalName(argType);
    });

    registerProperty(ServiceMethod.class, "javaReturnTypeFullName", serviceMethod -> {
      Message returnType = serviceMethod.getReturnType();
      return UserTypeUtil.getCanonicalName(returnType);
    });

    //判断是否为MapEntry
    registerProperty(Message.class, "isMapEntry", message -> "MapEntry".equals(UserTypeUtil.getClassName(message)));

    //methodType: unary, clientStreaming, serverStreaming, bidiStreaming
    registerProperty(ServiceMethod.class, "unary", serviceMethod -> !serviceMethod.isArgStream() && !serviceMethod.isReturnStream());
    registerProperty(ServiceMethod.class, "clientStreaming", serviceMethod -> serviceMethod.isArgStream() && !serviceMethod.isReturnStream());
    registerProperty(ServiceMethod.class, "serverStreaming", serviceMethod -> !serviceMethod.isArgStream() && serviceMethod.isReturnStream());
    registerProperty(ServiceMethod.class, "bidiStreaming", serviceMethod -> serviceMethod.isArgStream() && serviceMethod.isReturnStream());

    registerProperty(Field.class, "isBytes", field -> "bytes".equals(field.getType().getFullyQualifiedName()));
    registerProperty(Field.class, "javaProtobuf", field -> {
      String fieldType = fieldType(field);
      int order = field.getIndex();
      boolean required = field.getModifier() == FieldModifier.REQUIRED;
      return new Protobuf(fieldType, required, order);
    });
  }
  private String fieldType(Field field) {
    if (field.isMap()) {
      return "MAP";
    } else if (field.isRepeated()) {
      return "OBJECT";
    } else {
      if (field.getType().isMessage()) {
        return "OBJECT";
      } else if (field.getType().isEnum()) {
        return "ENUM";
      } else {
        return field.getType().getFullyQualifiedName().toUpperCase();
      }
    }
  }


}
