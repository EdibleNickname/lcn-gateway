package com.can.service.open.register;

import com.can.model.UserDto;
import com.can.response.Response;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;


/**
 * @description: 用户注册服务接口
 *
 * @author: LCN
 * @date: 2018-06-27 14:29
 */
public interface RegisterService {

	/** 注册校验组 */
	@interface RegisterGroup{
	}

	/**
	 * 通过用户名查询用户是否存在
	 * @param userName
	 * @return
	 */
	Response<Boolean> queryUserNameIsExist(String userName);

	/**
	 * 通过邮箱查询用户是否存在
	 * @param eMail
	 * @return
	 */
	Response<Boolean> queryEmailIsExist(String eMail);

	/**
	 * 验证验证码是否正确，正确发送验证码对应的邮箱
	 *
	 * @param captcha	用户输入的验证码答案
	 * @param eMail		用户注册的邮箱
	 * @param redisKey	验证码答案在redis中的key
	 * @return
	 */
	Response<Map<String, String>> validCaptcha(String captcha, String eMail, String redisKey);


	/**
	 * 注册用户
	 *
	 * @param httpResponse http响应对象
	 * @param userDto 用户信息
	 * @return
	 */
	Response<Map<String, String>> registerUser(HttpServletResponse httpResponse, UserDto userDto);

}
