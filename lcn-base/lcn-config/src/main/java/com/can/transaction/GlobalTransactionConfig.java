package com.can.transaction;

import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import javax.annotation.Resource;
import java.util.Properties;

/**
 * @description:	全局事务配置01
 *
 * @author: LCN
 * @date: 2018-07-14 09:39
 */


//@Configuration
public class GlobalTransactionConfig {

	@Resource
	private DataSourceTransactionManager transactionManager;

	@Bean(name = "txAdvice")
	public TransactionInterceptor getAdvisor() {
		Properties properties = new Properties();
		properties.setProperty("*", "PROPAGATION_REQUIRED,ISOLATION_DEFAULT,-Exception");
		return new TransactionInterceptor(transactionManager, properties);
	}

	@Bean
	public BeanNameAutoProxyCreator txProxy() {

		BeanNameAutoProxyCreator creator = new BeanNameAutoProxyCreator();
		creator.setInterceptorNames("txAdvice");
		creator.setBeanNames("*Service", "*ServiceImpl");
		creator.setProxyTargetClass(true);
		return creator;
	}

}
