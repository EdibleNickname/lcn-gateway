package com.can.web;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * @description: 线程配置类
 * @author: LCN
 * @date: 2018-05-23 16:44
 */

@Configuration
public class WebAnsycConfigurer implements AsyncConfigurer {

	/**
	 * 自定义线程池
	 *
	 * @return
	 */
	@Override
	public Executor getAsyncExecutor() {

		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();

		// 当前线程数
		taskExecutor.setCorePoolSize(5);

		// 最大线程是
		taskExecutor.setMaxPoolSize(10);

		// 线程池所使用的缓冲队列
		taskExecutor.setQueueCapacity(25);

		//等待任务在关机时完成--表明等待所有线程执行完
		taskExecutor.setWaitForTasksToCompleteOnShutdown(true);

		// 任务等待多少秒后没有执行停止(默认为0)
		taskExecutor.setAwaitTerminationSeconds(60 * 15);

		// 设置线程的前缀
		taskExecutor.setThreadNamePrefix("MyAsync-");

		//初始化
		taskExecutor.initialize();

		return taskExecutor;
	}

	/**
	 * 异常处理类
	 *
	 * @return
	 */
	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return new SimpleAsyncUncaughtExceptionHandler();
	}


}
