package com.can.web.config;

import com.can.page.PagingPlugin;
import com.can.util.email.EmailUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @description: 加载工具类
 * @author: LCN
 * @date: 2018-05-23 16:49
 */

// extends WebMvcConfigurationSupport

@Configuration
public class MyWebConfig {

	/**
	 * 邮件发送工具
	 * @return
	 */
	@Bean
	public EmailUtil getEmailUtil() {
		return new EmailUtil();
	}


	/** 注册分页插件 */
	@Bean
	public PagingPlugin getPagingPlugin() {

		PagingPlugin pagePlugin = new PagingPlugin();
		Properties properties = new Properties();
		properties.setProperty("dbType", "mysql");
		properties.setProperty("pageSqlId", ".*Page$");
		properties.setProperty("pageSize", "10");
		properties.setProperty("pageNum", "1");
		pagePlugin.setProperties(properties);
		return pagePlugin;
	}

}

