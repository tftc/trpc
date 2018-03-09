package com.itiancai.trpc.examples.springsupport.service;

import com.itiancai.trpc.examples.springsupport.entity.User;
import com.itiancai.trpc.examples.springsupport.entity.dao.UserDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class UserService {

  @Autowired
  private UserDao userDao;

  public User save(User user) {
    return userDao.save(user);
  }
}
