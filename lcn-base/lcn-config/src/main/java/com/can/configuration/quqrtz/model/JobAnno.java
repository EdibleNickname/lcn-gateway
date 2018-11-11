package com.can.configuration.quqrtz.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description:
 * @author: LCN
 * @date: 2018-11-10 13:47
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JobAnno {

	/**
	 * 任务名
	 * @return
	 */
	String jobName();

	/**
	 * 这个定时器是否需要启服启动
	 * @return
	 */
	boolean actived() default JobConstant.ACTIVED;

	/**
	 * 定时任务的组名
	 * @return
	 */
	String groupName() default JobConstant.GROUP_NAME;

	/**
	 * 多个job会不会并发 一个任务每5s执行一次，如果5s过了，上个任务还没执行完，新的任务能立即开始吗
	 * false:不能
	 * @return
	 */
	boolean concurrent() default JobConstant.CONCURRENT;

	/**
	 * 触发器的名称
	 * @return
	 */
	String triggerName() default JobConstant.TRIGGER_NAME;

	/**
	 * cron表达式: 执行频率 默认每10分钟执行一次
	 * @return
	 */
	String cron() default JobConstant.CRON;

}
