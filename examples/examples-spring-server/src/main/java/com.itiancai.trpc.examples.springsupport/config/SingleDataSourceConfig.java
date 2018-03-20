package com.itiancai.trpc.examples.springsupport.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.itiancai.trpc.examples.springsupport.config.prop.DataSourceProperties;

import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.sql.SQLException;

import javax.annotation.Resource;
import javax.sql.DataSource;

@EnableConfigurationProperties(DataSourceProperties.class)
public class SingleDataSourceConfig {

  @Resource(name = "dataSourceProperties")
  protected DataSourceProperties dataSourceProperties;

  protected DataSource createDataSource() throws SQLException {
    // special
    DruidDataSource datasource = new DruidDataSource();
    datasource.setUrl(dataSourceProperties.getUrl());
    datasource.setUsername(dataSourceProperties.getUsername());
    datasource.setPassword(dataSourceProperties.getPassword());
    // common
    datasource.setDriverClassName(dataSourceProperties.getDriverClassName());
    datasource.setInitialSize(dataSourceProperties.getInitialSize());
    datasource.setMinIdle(dataSourceProperties.getMinIdle());
    datasource.setMaxActive(dataSourceProperties.getMaxActive());
    datasource.setMaxWait(dataSourceProperties.getMaxWait());
    datasource.setTimeBetweenEvictionRunsMillis(dataSourceProperties.getTimeBetweenEvictionRunsMillis());
    datasource.setMinEvictableIdleTimeMillis(dataSourceProperties.getMinEvictableIdleTimeMillis());
    datasource.setValidationQuery(dataSourceProperties.getValidationQuery());
    datasource.setTestWhileIdle(dataSourceProperties.isTestWhileIdle());
    datasource.setTestOnBorrow(dataSourceProperties.isTestOnBorrow());
    datasource.setTestOnReturn(dataSourceProperties.isTestOnReturn());
    datasource.setPoolPreparedStatements(dataSourceProperties.isPoolPreparedStatements());
    datasource.setMaxPoolPreparedStatementPerConnectionSize(dataSourceProperties.getMaxPoolPreparedStatementPerConnectionSize());
    datasource.setFilters(dataSourceProperties.getFilters());
    datasource.setConnectionProperties(dataSourceProperties.getConnectionProperties());
    return datasource;
  }

}