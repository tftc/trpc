package com.itiancai.trpc.examples.springsupport.config.refresh;

import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.stereotype.Component;

@Component
public class ConfigRefresh {

  private final static Logger logger = LoggerFactory.getLogger(ConfigRefresh.class);

  @Autowired
  private RefreshScope refreshScope;

  @ApolloConfigChangeListener
  public void onChange(ConfigChangeEvent changeEvent) {
    boolean datasourceChanged = false;
    for (String changedKey : changeEvent.changedKeys()) {
      if (changedKey.startsWith("spring.datasource")) {
        datasourceChanged = true;
        break;
      }
    }
    if (!datasourceChanged) {
      return;
    }

    logger.info("before refresh");
    refreshScope.refresh("dataSourceProperties");
    refreshScope.refresh("datasource");
    logger.info("after refresh");
  }

}
