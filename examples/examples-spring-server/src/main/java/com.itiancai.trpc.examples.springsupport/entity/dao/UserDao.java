package com.itiancai.trpc.examples.springsupport.entity.dao;

import com.itiancai.trpc.examples.springsupport.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface UserDao extends JpaRepository<User, Long> {

  @Transactional
  @Override
  User save(User s);
}
