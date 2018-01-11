package com.itiancai.trpc.core.utils;

import com.google.common.collect.Lists;

import java.lang.reflect.Method;
import java.util.List;

public class ReflectUtils {

  public static List<Method> findAllPublicMethods(Class<?> clazz) {
    List<Method> methods = Lists.newLinkedList();
    for (Method method : clazz.getMethods()) {
      if (isLegal(method)) {
        continue;
      }
      methods.add(method);
    }
    return methods;
  }

  public static boolean isLegal(Method method) {
    return isEqualsMethod(method) || isHashCodeMethod(method) || isToStringMethod(method);
  }

  private static boolean isEqualsMethod(Method method) {
    if (method == null || !method.getName().equals("equals")) {
      return false;
    }
    Class<?>[] paramTypes = method.getParameterTypes();
    return (paramTypes.length == 1 && paramTypes[0] == Object.class);
  }

  private static boolean isHashCodeMethod(Method method) {
    return (method != null && method.getName().equals("hashCode")
            && method.getParameterTypes().length == 0);
  }

  public static boolean isToStringMethod(Method method) {
    return (method != null && method.getName().equals("toString")
            && method.getParameterTypes().length == 0);
  }
}
