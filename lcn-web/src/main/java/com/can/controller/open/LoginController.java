package com.can.controller.open;

import com.alibaba.fastjson.JSON;
import com.can.response.Response;
import com.can.service.open.login.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @description: 登录接口
 * @author: LCN
 * @date: 2018-11-04 12:56
 */

@Slf4j
@RestController
@RequestMapping("/open/login")
public class LoginController {

	@Resource
	private LoginService loginService;

	/**
	 * 此处不用参数的非空判断，因为如果前端传递的参数少了，是无法映射到这个url的
	 */
	@GetMapping("/userLogin/{captcha}/{redisKey}/{userName}/{password}")
	public Response<Map<String, String>> userLogin(HttpServletResponse httpResponse, @PathVariable("captcha")String captcha,
		@PathVariable("redisKey")String redisKey, @PathVariable("userName")String userName,
		@PathVariable("password")String password) {

		log.info("用户登录请求参数:用户名---->{}, 验证码---->{}", userName, captcha);
		Response<Map<String, String>> response = loginService.userLogin(httpResponse, captcha, redisKey, userName, password);

		log.info("用户登录返回结果================>{}", JSON.toJSONString(response));
		return response;
	}
}
