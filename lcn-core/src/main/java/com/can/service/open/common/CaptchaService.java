package com.can.service.open.common;

import com.can.model.CaptchaDto;
import com.can.response.Response;

import java.io.IOException;

/**
 * @description: 生成验证码服务接口
 *
 * @author: LCN
 * @date: 2018-07-03 17:28
 */
public interface CaptchaService {

	/**
	 * 生成验证码
	 * @param expireTime 过期时间，单位:秒
	 *
	 * @return
	 * @throws IOException
	 */
	Response<CaptchaDto> generateCaptcha(Long expireTime) throws IOException;

}
