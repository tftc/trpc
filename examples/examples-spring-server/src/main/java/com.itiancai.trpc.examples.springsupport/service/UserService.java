package com.itiancai.trpc.examples.springsupport.service;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfig;
import com.itiancai.trpc.examples.springsupport.entity.User;
import com.itiancai.trpc.examples.springsupport.entity.dao.UserDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class UserService {

  private final static Logger logger = LoggerFactory.getLogger(UserService.class);

  @Autowired
  private UserDao userDao;

  @ApolloConfig
  private Config apolloConfig;

  @Value("${test.name:bao}")
  private String name;

  public User save(User user) {
    int age = apolloConfig.getIntProperty("test.age", 28);
    logger.info("test.name: {}", name);
    logger.info("test.age: {}", age);
    return userDao.save(user);
  }
}
