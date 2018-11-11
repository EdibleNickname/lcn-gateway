package com.can.configuration.security.filter;

import com.can.configuration.security.utils.JwtTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @description:
 *
 * @author: LCN
 * @date: 2018-06-13 21:06
 */

@Slf4j
public class JwtAuthorizationTokenFilter extends OncePerRequestFilter {

	@Autowired
	@Qualifier("jwtUserDetailsService")
	private UserDetailsService userDetailsService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	/** 相当于key值，从request里面获取对应的token*/
	@Value("${jwt.header}")
	private String header;

	/** 混淆作用，拼接在token的前面*/
	@Value("${jwt.tokenPrefix}")
	private String tokenPrefix;


	@Override
	protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
		log.debug("正在对{}请求进行处理", httpServletRequest.getRequestURL());

		final String requestHeader = httpServletRequest.getHeader(this.header);

		String username = null;
		String authToken = null;

		if (requestHeader != null && requestHeader.startsWith(tokenPrefix)) {
			authToken = requestHeader.substring(tokenPrefix.length());
			try {
				username = jwtTokenUtil.getUsernameFromToken(authToken);
			} catch (IllegalArgumentException e){
				log.error("从token解析用户名是报错--->{}", e);
			} catch (ExpiredJwtException e) {
				log.warn("token过期或者无效的--->{}", e);
			}
		} else {
			log.warn("没有从头部获取到需要的tokenHeader,忽略头部继续处理");
		}

		log.debug("正在验证的用户----->{}", username);

		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			log.debug("SecurityContext为null,开始验证用户");

			// 通过用户名从数据源里面查询这个用户
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);

			// 验证token
			if (jwtTokenUtil.validateToken(authToken, userDetails)) {
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
				log.info("用户--{}--验证通过,为其设置SecurityContext", username);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}
		filterChain.doFilter(httpServletRequest, httpServletResponse);
	}


}
