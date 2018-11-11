package com.can.configuration.quqrtz.model;

/**
 * @description:
 * @author: LCN
 * @date: 2018-11-11 11:47
 */
public final class JobConstant {

	/** 默认的定时任务组名 */
	public final static String GROUP_NAME = "lcn";

	/** 定时任务是否可以并发 */
	public final static boolean CONCURRENT = false;

	/** 默认的触发器名 */
	public final static String TRIGGER_NAME = "lcn-trigger";

	public final static boolean ACTIVED = true;

	/** cron表达式: 执行频率 默认每10分钟执行一次 */
	public final static String CRON = "0 0/10 * * * ?";
}
