package com.can.controller.open;

import com.alibaba.fastjson.JSON;
import com.can.model.CaptchaDto;
import com.can.response.Response;
import com.can.service.open.common.CaptchaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @description:验证码控制器
 *
 * @author: LCN
 * @date: 2018-07-03 19:50
 */

@Slf4j
@RestController
@RequestMapping("/open/captcha")
public class CaptchaController {

	@Resource
	private CaptchaService captchaService;

	/** 默认的验证码过期时间，180s = 3m */
	private final static Long EXPIRE_TIME = 180L;

	/**
	 * 生成验证码
	 *
	 * @return
	 */
	@GetMapping("/generateCaptcha")
	public Response<CaptchaDto> generateCaptcha() throws IOException {

		log.info("请求验证码");
		Response<CaptchaDto> response = captchaService.generateCaptcha(EXPIRE_TIME);

		log.info("请求验证码返回的数据================>{}", JSON.toJSONString(response));
		return response;
	}

}
