package com.can.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 验证码
 *
 * @author: LCN
 * @date: 2018-07-03 18:11
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaDto {

	/** 从redis里面获取验证码答案的key */
	private String redisKey;

	/** 验证码图片的base64编码字符串*/
	private String captchaImg;

	/** 有效时间, 单位：秒*/
	private Long expireTime;
}
