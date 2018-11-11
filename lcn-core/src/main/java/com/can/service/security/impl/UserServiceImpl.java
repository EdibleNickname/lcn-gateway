package com.can.service.security.impl;

import com.alibaba.fastjson.JSON;
import com.can.dao.UserMapper;
import com.can.entity.User;
import com.can.service.security.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @description: 提供给Spring-Security查询用户的接口实现类
 *
 * @author: LCN
 * @date: 2018-05-20 22:45
 */

@Slf4j
@Service
public class UserServiceImpl implements UserService {

	@Resource
	private UserMapper userMapper;

	@Transactional
	@Cacheable(value = "role")
	@Override
	public User findUserByUserName(String userName) {
		User user = userMapper.selectAllUserInfoByUserName(userName);
		log.info("查询结果--------------->", JSON.toJSONString(user));
		return user;
	}
	
}
