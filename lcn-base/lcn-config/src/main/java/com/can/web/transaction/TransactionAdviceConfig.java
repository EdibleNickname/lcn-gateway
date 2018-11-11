package com.can.web.transaction;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import javax.annotation.Resource;

/**
 * @description: 全局事务配置02
 *
 * @author: LCN
 * @date: 2018-07-14 10:45
 */

@Aspect
@Configuration
public class TransactionAdviceConfig {

	/** 切点 */
	private static final String AOP_POINTCUT_EXPRESSION = "execution(* com.can.service..*.*(..))";

	@Resource
	private DataSourceTransactionManager transactionManager;

	@Bean
	public TransactionInterceptor txAdvice() {

		DefaultTransactionAttribute txAttrRequired = new DefaultTransactionAttribute();
		txAttrRequired.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

	/*	DefaultTransactionAttribute txAttrRequiredReadonly = new DefaultTransactionAttribute();
		txAttrRequiredReadonly.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		txAttrRequiredReadonly.setReadOnly(true);*/

		NameMatchTransactionAttributeSource source = new NameMatchTransactionAttributeSource();

		source.addTransactionalMethod("*", txAttrRequired);
		return new TransactionInterceptor(transactionManager, source);
	}

	@Bean
	public Advisor txAdviceAdvisor() {
		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		pointcut.setExpression(AOP_POINTCUT_EXPRESSION);
		return new DefaultPointcutAdvisor(pointcut, txAdvice());
	}

}
