package com.can.service.open.common.impl;

import com.alibaba.fastjson.JSON;
import com.can.model.CaptchaDto;
import com.can.redis.util.RedisUtil;
import com.can.response.Response;
import com.can.service.open.common.CaptchaService;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * @description: 验证码服务实现类
 *
 * @author: LCN
 * @date: 2018-07-03 18:10
 */

@Slf4j
@Service
public class CaptchaServiceImpl implements CaptchaService {

	@Resource
	private DefaultKaptcha producer;

	@Resource
	private RedisUtil redisUtil;

	/**
	 * 生成验证码
	 *
	 * @param expireTime 验证码的有效时间(单位：秒)
	 * @return
	 */
	@Override
	public Response<CaptchaDto> generateCaptcha(Long expireTime) throws IOException {

		CaptchaDto captchaDto = new CaptchaDto();

		// 生成文字验证码
		String text = producer.createText();

		log.info("生成的验证码是-------->{}", text);

		// 生成图片验证码
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		BufferedImage image = producer.createImage(text);
		ImageIO.write(image, "jpg", outputStream);

		Base64.Encoder encoder = Base64.getEncoder();

		captchaDto.setExpireTime(expireTime);
		captchaDto.setCaptchaImg(encoder.encodeToString(outputStream.toByteArray()));
		captchaDto.setRedisKey(redisUtil.setNoKey(text, expireTime));

		Response<CaptchaDto> response = new Response<>();
		response.setResult(captchaDto);

		log.info("返回的数据--------->{}", JSON.toJSONString(response));

		return response;
	}

}
