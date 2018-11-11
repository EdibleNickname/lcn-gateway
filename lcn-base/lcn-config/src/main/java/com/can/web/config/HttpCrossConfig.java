package com.can.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @description: http跨域处理类
 *
 * @author: LCN
 * @date: 2018-06-28 13:53
 */


@EnableWebMvc
@Configuration
public class HttpCrossConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		//设置允许跨域的路径
		registry.addMapping("/**")
				//设置允许跨域请求的域名
				.allowedOrigins("http://120.79.69.125", "http://localhost:8080")
				//是否允许证书 不再默认开启
				.allowCredentials(true)
				//设置允许的方法
				.allowedMethods("GET", "POST", "PUT", "DELETE")
				//允许的请求头
				.allowedHeaders("Authorization","Content-Type")
				//跨域允许时间
				.maxAge(5000);
	}

}
