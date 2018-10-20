package com.can.captcha;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @description: kaptcha配置设置
 *
 * @author: LCN
 * @date: 2018-07-03 17:52
 */

@Configuration
public class KaptchaConfig {


	@Bean
	public DefaultKaptcha producer() {

		Properties properties = new Properties();

		properties.setProperty("kaptcha.border", "yes");
		properties.setProperty("kaptcha.border.color", "139,126,102");

		properties.put("kaptcha.textproducer.font.color", "black");
		properties.setProperty("kaptcha.textproducer.font.size", "30");
		properties.setProperty("kaptcha.textproducer.font.names", "宋体,楷体,微软雅黑");

		properties.put("kaptcha.textproducer.char.space", "2");
		properties.setProperty("kaptcha.textproducer.char.length", "4");

		properties.setProperty("kaptcha.image.width", "120");
		properties.setProperty("kaptcha.image.height", "40");

		properties.put("kaptcha.noise.impl","com.google.code.kaptcha.impl.NoNoise");
		//properties.setProperty("kaptcha.obscurificator.impl", "com.google.code.kaptcha.impl.ShadowGimpy");

		Config config = new Config(properties);
		DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
		defaultKaptcha.setConfig(config);
		return defaultKaptcha;
	}

}
