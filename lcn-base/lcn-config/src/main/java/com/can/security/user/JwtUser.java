package com.can.security.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;

/**
 * @description:
 * @author: LCN
 * @date: 2018-06-13 21:16
 */
public class JwtUser implements UserDetails {

	/** 用户名 */
	private final String username;

	/** 登录密码 */
	private final String password;

	/** 账号是否可用 */
	private final boolean enabled;

	/** 上次密码重置的日期 */
	private final Date lastPasswordResetDate;

	/** 分配给这个用户的角色列表 */
	private final Collection<? extends GrantedAuthority> authorities;

	public JwtUser(String username, String password, boolean enabled, Date lastPasswordResetDate,
				   Collection<? extends GrantedAuthority> authorities ) {
		this.username = username;
		this.password = password;
		this.enabled = enabled;
		this.lastPasswordResetDate = lastPasswordResetDate;
		this.authorities = authorities;
	}

	/**
	 * 判断账号是否已经过期
	 *
	 * @return
	 */
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	/**
	 * 判断账号是否未锁定
	 *
	 * @return
	 */
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	/**
	 * 判断密码是否未过期
	 *
	 * @return
	 */
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	/**
	 * 获取用户名
	 * @return
	 */
	@Override
	public String getUsername() {
		return username;
	}

	/**
	 * 获取登录密码
	 * @return
	 */
	@Override
	public String getPassword() {
		return password;
	}

	/**
	 * 账号是否激活
	 *
	 * @return
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public Date getLastPasswordResetDate() {
		return lastPasswordResetDate;
	}

	/**
	 * 返回分配给这个用户的角色列表
	 *
	 * @return
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}
}
