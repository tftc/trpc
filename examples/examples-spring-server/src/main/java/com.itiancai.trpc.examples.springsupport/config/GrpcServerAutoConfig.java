package com.itiancai.trpc.examples.springsupport.config;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import java.sql.SQLException;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@EnableJpaRepositories(
        basePackages = "com.itiancai.trpc.examples.springsupport.entity.dao",
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager"
)
@EnableTransactionManagement(proxyTargetClass = true)
@EnableApolloConfig
@Configuration
public class GrpcServerAutoConfig extends SingleDataSourceConfig implements TransactionManagementConfigurer {


  @RefreshScope
  @Bean(name = "datasource")
  public DataSource datasource() {
    try {
      return createDataSource();
    } catch (SQLException e) {
      return null;
    }
  }

  @Bean
  public EntityManagerFactory entityManagerFactory() {

    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    vendorAdapter.setGenerateDdl(true);
    vendorAdapter.setShowSql(true);

    LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
    factory.setJpaVendorAdapter(vendorAdapter);
    factory.setPackagesToScan("com.itiancai.trpc.examples.springsupport.entity");
    factory.setDataSource(datasource());
    factory.afterPropertiesSet();

    return factory.getObject();
  }

  @Bean(name = "transactionManager")
  @Override
  public PlatformTransactionManager annotationDrivenTransactionManager() {
    JpaTransactionManager txManager = new JpaTransactionManager();
    txManager.setEntityManagerFactory(entityManagerFactory());
    return txManager;
  }
}
