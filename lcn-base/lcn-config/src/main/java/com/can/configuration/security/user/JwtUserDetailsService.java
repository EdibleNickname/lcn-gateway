package com.can.configuration.security.user;

import com.can.dao.UserMapper;
import com.can.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @description:
 *
 * @author: LCN
 * @date: 2018-06-13 21:17
 */
@Service
public class JwtUserDetailsService implements UserDetailsService {

	@Resource
	private UserMapper userMapper;

	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

		User user = userMapper.selectAllUserInfoByUserName(userName);

		if (user == null) {
			throw new UsernameNotFoundException(String.format("用户: %s 不存在！", userName));
		}

		return JwtUserFactory.create(user);
	}
}
