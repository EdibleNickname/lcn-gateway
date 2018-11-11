package com.can.quqrtz;

import org.quartz.Scheduler;
import org.quartz.ee.servlet.QuartzInitializerListener;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.AdaptableJobFactory;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.annotation.Resource;

/**
 * @description: Quartz配置
 * @author: LCN
 * @date: 2018-11-10 19:07
 */

@Configuration
public class QuartzConfig extends AdaptableJobFactory {

	@Resource
	private AutowireCapableBeanFactory capableBeanFactory;

	/**
	 * 作用：让实现类（就是一个继承job的类）可以注入其他service或者其他东西。
	 */
	@Override
	protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {

		// 调用父类的方法
		Object jobInstance = super.createJobInstance(bundle);
		// 进行注入
		capableBeanFactory.autowireBean(jobInstance);
		return jobInstance;
	}

	/**
	 * 监听器 : 可以监听到工程的启动，在工程停止再启动时可以让已有的定时任务继续进行
	 * @return
	 */
	@Bean
	public QuartzInitializerListener executorListener() {
		return new QuartzInitializerListener();
	}

	/**
	 * 定义quartz调度工厂
	 * @return
	 */
	@Bean
	public SchedulerFactoryBean schedulerFactoryBean() {

		SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();

		// 让job实现类可以注入其他对象
		schedulerFactoryBean.setJobFactory(this);

		// 用于quartz集群,QuartzScheduler 启动时更新己存在的Job
		schedulerFactoryBean.setOverwriteExistingJobs(true);

		// 延时启动，应用启动1秒后
		schedulerFactoryBean.setStartupDelay(1);
		return schedulerFactoryBean;
	}

	/**
	 * 调度器
	 * @return
	 */
	@Bean
	public Scheduler scheduler(){
		return schedulerFactoryBean().getScheduler();
	}

}
