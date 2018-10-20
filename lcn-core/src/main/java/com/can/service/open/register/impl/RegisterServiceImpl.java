package com.can.service.open.register.impl;

import com.alibaba.fastjson.JSON;
import com.can.dao.UserMapper;
import com.can.entity.User;
import com.can.model.UserDto;
import com.can.redis.util.RedisUtil;
import com.can.response.Response;
import com.can.security.utils.JwtTokenUtil;
import com.can.service.open.register.RegisterService;
import com.can.util.email.EmailUtil;
import com.can.util.email.model.Email;
import com.can.util.random.RandomUtil;
import com.can.util.validation.AssertUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 用户注册服务
 *
 * @author: LCN
 * @date: 2018-06-27 14:30
 */

@Slf4j
@Service
public class RegisterServiceImpl implements RegisterService {

	@Resource
	private UserMapper userMapper;

	@Resource
	private PasswordEncoder passwordEncoder;

	@Resource
	private RedisUtil redisUtil;

	@Resource
	private EmailUtil emailUtil;

	@Resource
	private JwtTokenUtil jwtTokenUtil;

	/** 邮件验证码过期时间 有效期60s 设为65是考虑到网络可能有限期,所以多加5s*/
	private final static Long EXPIRE_TIME = 65L;

	/** 默认token过期的时间 */
	@Value("${jwt.expiration:2592000}")
	private Long tokenExpireTime;

	/** 验证码的长度 */
	private final static int VERIFICATION_CODE_LENGTH = 6;

	/** 刚注册的用户的角色，默认为ROLE_USER,普通用户 */
	private final static String ROLE_READER = "ROLE_READER";

	@Override
	@Cacheable(value = "queryUserNameIsExist", key = "#userName")
	public Response<Boolean> queryUserNameIsExist(String userName) {

		log.info("查询用户名是否存在方法参数---------->{}", userName);

		// 用户名不能为空
		AssertUtil.isEmpty(userName, "用户名不能为空");

		Response<Boolean> response = new Response<>();

		User user = new User();
		user.setUserName(userName);
		User data = userMapper.selectUserInfo(user);

		if(data == null) {
			log.info("用户名------{}------对应的用户不存在", userName);
			response.setResult(false);
			return response;
		}

		log.info("用户名------{}------对应的用户已存在", userName);
		response.setResult(true);

		log.info("返回结果================>{}", JSON.toJSONString(response));
		return response;
	}

	@Override
	@Cacheable(value = "queryEMailIsExist", key = "#email")
	public Response<Boolean> queryEmailIsExist(String email) {

		log.info("查询邮箱是否存在方法参数---------->{}", email);

		// 判断邮箱格式
		AssertUtil.isEmail(email, "邮箱格式不正确");

		Response<Boolean> response = new Response<>();

		User user = new User();
		user.setEmail(email);
		User data = userMapper.selectUserInfo(user);

		if(data == null) {
			log.info("邮箱------{}------对应的用户不存在", email);
			response.setResult(false);
			return response;
		}

		log.info("邮箱------{}------对应的用户已存在", email);
		response.setResult(true);

		log.info("返回结果================>{}", JSON.toJSONString(response));
		return response;
	}


	@Override
	public Response<Map<String, String>> validCaptcha(String captcha, String eMail, String redisKey) {

		log.info("用户输入的验证码答案---------->{}", captcha);
		log.info("用户输入的redis的key--------->{}", redisKey);

		String captchaAnswer = (String)redisUtil.get(redisKey);
		log.info("从redis中获取的结果---------->{}", captchaAnswer);

		Map<String, String> result = new HashMap<>(16);
		Response<Map<String, String>> response = new Response<>();

		// 验证码答案不能为空
		if(StringUtils.isEmpty(captchaAnswer)) {
			result.put("answer", "error");
			response.setResult(result);
			log.info("返回结果------------>{}", JSON.toJSONString(response));
			return response;
		}

		// 验证码答案不正确
		if(!captchaAnswer.equals(captcha)) {
			result.put("answer", "error");
			response.setResult(result);
			log.info("返回结果------------>{}", JSON.toJSONString(response));
			return response;
		}

		// 验证码正确
		String verificationCode = RandomUtil.generateNumStr(VERIFICATION_CODE_LENGTH);
		log.info("生成的验证码为------------>{}", verificationCode);

		// 存入redis
		String key = redisUtil.setNoKey(verificationCode, EXPIRE_TIME);


		Map<String, String> emailContent = new HashMap<>(4);
		// 邮件的内容
		emailContent.put("verificationCode", verificationCode);
		// 发送邮件
		Email<Map<String, String>> email = new Email<>();
		email.setReceiverEmail(eMail);
		email.setContent(emailContent);
		email.setSubject("来自于LCN的注册验证码");
		email.setTempleName("eMailTemplate.ftl");
		emailUtil.sendHtmlEmail(email);


		result.put("answer", "success");
		result.put("eMailRedisKey", key);
		response.setResult(result);

		log.info("返回结果------------>{}", JSON.toJSONString(response));
		return response;
	}


	@Override
	public Response<Map<String, String>> registerUser(HttpServletResponse httpResponse, UserDto userDto) {

		log.info("用户注册的信息--------->{}", JSON.toJSONString(userDto));

		Response<Map<String, String>> response = new Response<>();
		Map<String, String> map = new HashMap<>(16);

		String emailAnswer = (String)redisUtil.get(userDto.getEmailRedisKey());


		log.info("从redis中获取的结果---------->{}", emailAnswer);

		// 验证码已过期
		if(StringUtils.isEmpty(emailAnswer)) {
			map.put("answer", "error");
			map.put("hint", "验证码已过期");
			response.setResult(map);
			log.info("返回结果------------>{}", JSON.toJSONString(response));
			return response;
		}

		// 验证码答案不正确
		if(!emailAnswer.equals(userDto.getAuthCode())) {
			map.put("answer", "error");
			map.put("hint", "验证码错误");
			response.setResult(map);
			log.info("返回结果------------>{}", JSON.toJSONString(response));
			return response;
		}

		User user = new User();
		BeanUtils.copyProperties(userDto, user);
		List<User> list = userMapper.queryUserUnique(user);


		// 用户名已被注册
		if(list != null && list.size() > 0) {
			map.put("answer", "error");
			map.put("hint", "邮箱或者用户名已被注册了");
			response.setResult(map);
			log.info("返回结果------------>{}", JSON.toJSONString(response));
			return response;
		}

		user.setPassword(passwordEncoder.encode(userDto.getPassword()));
		user.setRegistDate(new Date());
		user.setLastPasswordResetDate(new Date());

		// 新增用户
		int result = userMapper.userInsertSelective(user);

		// 新增失败
		if (result != 1) {
			map.put("answer", "error");
			map.put("hint", "新增用户失败");
			response.setResult(map);
			log.info("返回结果------------>{}", JSON.toJSONString(response));
			return response;
		}

		//为用户分配角色
		result = userMapper.addUserRole(user.getUserName(), ROLE_READER);

		if (result != 1) {
			map.put("answer", "error");
			map.put("hint", "为用户分配角色失败");
			response.setResult(map);
			log.info("返回结果------------>{}", JSON.toJSONString(response));
			return response;
		}

		// 生成token,并把token放到响应头
		String token = jwtTokenUtil.generateToken(user.getUserName(), tokenExpireTime);

		log.info("用户生成的token为---------->{}", token);

		// 设置浏览器可以访问自定义的响应头: Authorization
		httpResponse.addHeader("Access-Control-Expose-Headers","Authorization");
		httpResponse.setHeader("Authorization", token);

		String userId = userMapper.queryUserIdByUserName(user.getUserName());
		log.info("注册用户的id---------->{}", userId);

		// 注册成功
		map.put("answer", "success");
		map.put("userId", userId);
		map.put("hint", "注册成功");
		response.setResult(map);
		log.info("返回结果------------>{}", JSON.toJSONString(response));

		return response;

	}
}
