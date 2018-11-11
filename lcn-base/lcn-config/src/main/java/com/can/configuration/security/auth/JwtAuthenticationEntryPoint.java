package com.can.configuration.security.auth;

import com.alibaba.fastjson.JSON;
import com.can.response.Response;
import com.can.response.enums.ResponseEnum;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @description: 当用户请求了一个受保护的资源，但是用户没有通过认证的处理类
 *
 * @author: LCN
 * @date: 2018-06-14 23:08
 */

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	/**
	 * 以json的格式响应
	 *
	 * @param httpServletRequest
	 * @param httpServletResponse
	 * @param authException
	 * @throws IOException
	 * @throws ServletException
	 */
	@Override
	public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException authException) throws IOException, ServletException {

		httpServletResponse.setCharacterEncoding("UTF-8");
		httpServletResponse.setContentType("application/json");

		Response<Void> response = new Response<>();
		response.setCode(ResponseEnum.UNAUTHORIZED.getCode());
		response.setMessage(ResponseEnum.UNAUTHORIZED.getMessage());

		httpServletResponse.getWriter().println(JSON.toJSONString(response));
	}


}
