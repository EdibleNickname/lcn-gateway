package com.can.configuration.quqrtz.model;

import lombok.Data;

/**
 * @description:
 * @author: LCN
 * @date: 2018-11-10 17:25
 */
@Data
public class JobConfig {

	/** 必填参数 */

	/**
	 * 任务名
	 */
	private String jobName;

	/**
	 * 执行任务的对象
	 */
	private Class clazz;


	/** 非必填参数 */

	/**
	 * 组名
	 */
	private String groupName;

	/**
	 * 是否需要并发操作
	 */
	private boolean concurrent;

	/**
	 * 触发器的名称
	 */
	private String tiggerName;

	/**
	 * 是否需要启服启动
	 */
	private boolean actived;

	/**
	 * cron表达式
	 */
	private String cron;

	public static class Builder {

		private final String jobName;
		private final Class clazz;

		private String groupName = JobConstant.GROUP_NAME;
		private boolean concurrent = JobConstant.CONCURRENT;
		private String triggerName = JobConstant.TRIGGER_NAME;
		private boolean actived = JobConstant.ACTIVED;
		private String cron = JobConstant.CRON;

		public Builder(String jobName, Class clazz) {
			this.jobName = jobName;
			this.clazz = clazz;
		}

		public Builder setGroupName(String groupName) {
			this.groupName = groupName;
			return this;
		}

		public Builder setConcurrent(boolean concurrent) {
			this.concurrent = concurrent;
			return this;
		}

		public Builder setTriggerName(String triggerName) {
			this.triggerName = triggerName;
			return this;
		}

		public Builder setActived(boolean actived) {
			this.actived = actived;
			return this;
		}

		public Builder setCron(String cron) {
			this.cron = cron;
			return this;
		}

		public JobConfig build() {
			return new JobConfig(this);
		}
	}

	private JobConfig(Builder builder){

		this.jobName = builder.jobName;
		this.clazz = builder.clazz;

		this.groupName = builder.groupName;
		this.concurrent = builder.concurrent;
		this.tiggerName = builder.triggerName;
		this.cron = builder.cron;
	}

}
