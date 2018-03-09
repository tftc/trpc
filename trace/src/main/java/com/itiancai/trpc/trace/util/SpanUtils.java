package com.itiancai.trpc.trace.util;

import com.mysql.jdbc.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.MethodDescriptor;

public class SpanUtils {

  private final static Logger log = LoggerFactory.getLogger(SpanUtils.class);

  public static String grpcSpanName(MethodDescriptor methodDescriptor) {
    try {
      String fullName = methodDescriptor.getFullMethodName();

      String[] strs0 = fullName.split("/");
      String className = strs0[0];
      String[] strs1 = className.split("\\.");
      StringBuilder sb = new StringBuilder();
      for(int i=0; i<= strs1.length -1; i++) {
        if(i < strs1.length -1) {
          sb.append(strs1[i].charAt(0)).append(".");
        } else {
          sb.append(strs1[i]).append(":");
        }
      }

      String methodName = strs0[1];
      sb.append(methodName);
      return sb.toString();
    } catch (Throwable t) {
      log.error("grpcSpanName error, method:" + methodDescriptor.getFullMethodName(), t);
      return methodDescriptor.getFullMethodName();
    }
  }



  public static String mysqlSpanName(Connection connection) {
    try {
      String spanName;
      String databaseName = connection.getCatalog();
      if (databaseName != null && !databaseName.isEmpty()) {
        spanName = "mysql-" + databaseName;
      } else {
        spanName = "mysql";
      }
      return spanName;
    } catch (Exception e) {
      return "mysql";
    }
  }

}
