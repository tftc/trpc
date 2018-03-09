package com.itiancai.trpc.trace.mysql;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.ResultSetInternalMethods;
import com.mysql.jdbc.Statement;
import com.mysql.jdbc.StatementInterceptorV2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

import java.sql.SQLException;
import java.util.Properties;

public class TracingStatementInterceptor implements StatementInterceptorV2 {

  private final static Logger logger = LoggerFactory.getLogger(TracingStatementInterceptor.class);

  private static Tracer tracer;

  public static void setTracer(Tracer tracer) {
    TracingStatementInterceptor.tracer = tracer;
  }

  @Override
  public ResultSetInternalMethods preProcess(String sql, Statement interceptedStatement,
                                             Connection connection) throws SQLException {
    if(tracer == null) return null;

    Span span = tracer.createSpan(getSpanName(connection));
    if (interceptedStatement instanceof PreparedStatement) {
      sql = ((PreparedStatement) interceptedStatement).getPreparedSql();
    }
    span.logEvent(Span.CLIENT_SEND);
    span.tag("sql.query", sql);
    return null;
  }

  @Override
  public ResultSetInternalMethods postProcess(String sql, Statement interceptedStatement,
      ResultSetInternalMethods originalResultSet, Connection connection, int warningCount,
      boolean noIndexUsed, boolean noGoodIndexUsed, SQLException statementException)
      throws SQLException {
    if(tracer == null) return null;
    Span span = tracer.getCurrentSpan();
    if (span == null) return null;

    if (statementException != null) {
      span.tag("error", Integer.toString(statementException.getErrorCode()));
    }
    span.logEvent(Span.CLIENT_RECV);
    tracer.close(span);
    return null;
  }

  private String getSpanName(Connection connection) {
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

  @Override public boolean executeTopLevelOnly() {
    return true; // True means that we don't get notified about queries that other interceptors issue
  }

  @Override public void init(Connection conn, Properties props) throws SQLException {
    // Don't care
  }

  @Override public void destroy() {
    // Don't care
  }
}