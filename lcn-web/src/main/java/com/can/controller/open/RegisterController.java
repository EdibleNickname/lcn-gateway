package com.can.controller.open;

import com.can.model.UserDto;
import com.can.response.Response;
import com.can.service.open.register.RegisterService;
import com.can.service.open.register.RegisterService.RegisterGroup;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @description:
 * @author: LCN
 * @date: 2018-06-26 17:08
 */

@RestController
@RequestMapping("/open/register")
public class RegisterController {

	@Resource
	private RegisterService registerService;

	@GetMapping("/queryUserNameIsExist/{userName}")
	public Response<Boolean> queryUserNameIsExist(@PathVariable("userName") String userName) {

		Response<Boolean> response = registerService.queryUserNameIsExist(userName);
		return response;
	}

	/**
	 * 把邮箱参数放到前面的原因：邮箱多以com结尾，拼接在后面会导致spring-boot截取后面的.com然后处理完
	 * 跳转到邮箱.com前面的页面，但是项目中没有这个页面，所以报错了
	 * @param email
	 * @return
	 */
	@GetMapping("{email}/queryEmailIsExist")
	public Response<Boolean> queryEMailIsExist(@PathVariable("email") String email) {

		Response<Boolean> response = registerService.queryEmailIsExist(email);
		return response;
	}

	/**
	 * 验证验证码是否正确
	 *
	 * @param captcha
	 * @param email
	 * @param redisKey
	 * @return
	 */
	@GetMapping("/validCaptcha/{captcha}/{email}/{redisKey}")
	public Response<Map<String, String>> validCaptcha(@PathVariable("captcha")String captcha,
		@PathVariable("email")String email, @PathVariable("redisKey")String redisKey) {

		Response<Map<String, String>> response = registerService.validCaptcha(captcha, email, redisKey);
		return response;
	}

	/**
	 * 开始注册
	 *
	 * @param userDto 用户信息
	 * @return
	 */
	@PostMapping("/registerUser")
	public Response<Map<String, String>> registerUser(HttpServletResponse httpResponse, @Validated({RegisterGroup.class})@RequestBody UserDto userDto) {
		Response<Map<String, String>> response = registerService.registerUser(httpResponse, userDto);
		return response;
	}

}
