package com.can.service.security;


import com.can.entity.User;

/**
 * @description: 提供给Spring-Security查询用户的接口实现类
 * @author: LCN
 * @date: 2018-05-20 22:44
 */

public interface UserService {

	@interface UserGrop{
	}

	/**
	 * 通过用户名查询整个用户信息
	 * @param userName 用户名
	 *
	 * @return
	 */
	User findUserByUserName(String userName);

}
