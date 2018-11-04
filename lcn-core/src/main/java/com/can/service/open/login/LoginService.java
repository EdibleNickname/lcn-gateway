package com.can.service.open.login;

import com.can.response.Response;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @description: 用户登录接口
 * @author: LCN
 * @date: 2018-11-04 13:07
 */
public interface LoginService {

	/**
	 * 用户登录
 	 * @param captcha 验证码
	 * @param redisKey	对应的redisKey
	 * @param userName	用户名
	 * @param password	密码
	 * @return
	 */
	Response<Map<String, String>> userLogin(HttpServletResponse httpResponse, String captcha, String redisKey,
		String userName, String password);

}
