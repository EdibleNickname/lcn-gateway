package com.can.service.open.login.impl;

import com.can.dao.UserMapper;
import com.can.entity.User;
import com.can.response.Response;
import com.can.security.utils.JwtTokenUtil;
import com.can.service.open.common.CaptchaService;
import com.can.service.open.login.LoginService;
import com.can.utils.enums.CaptchaValidateEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: 用户登录实现类
 *
 * @author: LCN
 * @date: 2018-11-04 13:10
 */

@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

	@Resource
	private UserMapper userMapper;

	@Resource
	private PasswordEncoder passwordEncoder;

	@Resource
	private CaptchaService captchaService;

	@Resource
	private JwtTokenUtil jwtTokenUtil;

	/** 默认token过期的时间 */
	@Value("${jwt.expiration:2592000}")
	private Long tokenExpireTime;

	@Override
	public Response<Map<String, String>> userLogin(HttpServletResponse httpResponse, String captcha, String redisKey,
		String userName, String password) {

		Map<String, String> result = new HashMap<>(4);
		Response<Map<String, String>> response = new Response<>();
		result.put("answer", "error");
		response.setResult(result);

		if(captchaService.validCaptcha(captcha, redisKey) != CaptchaValidateEnum.CORRECT) {
			log.info("用户登录失败-------->验证码错误");
			return response;
		}

		User user = userMapper.selectAllUserInfoByUserName(userName);
		if(user == null) {
			log.info("用户登录失败-------->用户名对应的用户不存在");
			return response;
		}

		String encodePw = passwordEncoder.encode(password);
		if(!encodePw.equals(user.getPassword())) {
			log.info("用户登录失败-------->密码错误");
			return response;
		}

		// 生成token,并把token放到响应头
		String token = jwtTokenUtil.generateToken(user.getUserName(), tokenExpireTime);

		log.info("用户生成的token为---------->{}", token);

		// 设置浏览器可以访问自定义的响应头: Authorization
		httpResponse.addHeader("Access-Control-Expose-Headers","Authorization");
		httpResponse.setHeader("Authorization", token);

		result.put("answer", "success");
		response.setResult(result);
		log.info("用户-------->{}登录成功", userName);
		return response;
	}

}
