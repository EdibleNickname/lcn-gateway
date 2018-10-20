package com.can.controller.open;

import com.can.model.CaptchaDto;
import com.can.response.Response;
import com.can.service.open.common.CaptchaService;
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

@RestController
@RequestMapping("/open/captcha")
public class CaptchaController {

	@Resource
	private CaptchaService captchaService;

	/** 默认的验证码过期时间，600s = 10m */
	private final static Long EXPIRE_TIME = 600L;

	/**
	 * 生成验证码
	 *
	 * @return
	 */
	@GetMapping("/generateCaptcha")
	public Response<CaptchaDto> generateCaptcha() throws IOException {

		Response<CaptchaDto> response = captchaService.generateCaptcha(EXPIRE_TIME);
		return response;
	}

}
