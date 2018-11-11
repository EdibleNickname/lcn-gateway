package com.can.configuration.security;

import com.can.configuration.security.filter.JwtAuthorizationTokenFilter;
import com.can.configuration.security.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

import javax.annotation.Resource;

/**
 * @description: Spring-Security配置
 *
 * @author: LCN
 * @date: 2018-06-13 21:02
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Resource
	private UserDetailsService userDetailsService;

	@Autowired
	private AuthenticationEntryPoint authenticationEntryPoint;

	/**
	 * 密码加密器
	 *
	 * @return
	 */
	@Bean
	public PasswordEncoder passwordEncoderBean() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * 验证管理器
	 *
	 * @return
	 * @throws Exception
	 */
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	/**
	 * url过滤器
	 *
	 * @return
	 * @throws Exception
	 */
	@Bean
	public JwtAuthorizationTokenFilter authenticationTokenFilterBean() throws Exception {
		return new JwtAuthorizationTokenFilter();
	}


	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		// 配置了这个，会自动将数据库里面的加密密码和用户输入的密码进行比较
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoderBean());
	}

	/**
	 * jwt工具类
	 * @return
	 */
	@Bean
	public JwtTokenUtil getJwtTokenUtil() {
		return new JwtTokenUtil();
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		// 由于使用的是JWT，我们这里不需要csrf
		httpSecurity.csrf().disable()
			// 认证失败的处理器
			.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint).and()
			// 基于token，所以不需要session
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
			.authorizeRequests()
			// preflight请求的话,直接允许
			.requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
			// 下面的请求也可以直接通过
			.antMatchers(
				"/open/**"
			).permitAll()
			// 其他的请求都需要验证
			.anyRequest().authenticated();

		// 在返回直接先进行过滤
		httpSecurity.addFilterBefore(authenticationTokenFilterBean(),
				UsernamePasswordAuthenticationFilter.class);

		// 禁用缓存
		httpSecurity.headers().cacheControl();
	}

}
