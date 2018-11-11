package com.can.quqrtz;

import com.can.quqrtz.model.JobAnno;
import com.can.quqrtz.model.JobConfig;
import com.can.quqrtz.model.JobConstant;
import com.can.util.reflex.ReflexUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: LCN
 * @date: 2018-11-10 13:55
 */

@Slf4j
@Component
public class JobManager {

	@Resource
	private Scheduler scheduler;

	private Set<JobConfig> jobConfigSet;

	public JobManager() {
		jobConfigSet = new HashSet<>();
	}

	/**
	 * 获取定时任务列表
	 * @param packageName 需要扫描的包路径
	 * @return	需要执行的定时任务个数
	 */
	public int getTaskList(String packageName) {

		long startTime = System.currentTimeMillis();

		Set<Class<?>> clazzSet = ReflexUtil.getClazzes(packageName, true);

		long scanEndTime = System.currentTimeMillis();

		log.info("从包名下扫描获取全部类对象耗时---->{}ms", scanEndTime - startTime);

		jobConfigSet = clazzSet.stream().filter(item -> {
			// 筛选
			JobAnno jobAnno = item.getAnnotation(JobAnno.class);
			if(jobAnno == null) {
				return false;
			}

			// 定时器不需要激活
			if(!jobAnno.actived()) {
				return false;
			}

			log.info("获取到需要的定时器任务---->{}", item.getName());
			return true;
		}).map(item -> {
			// 拼装为 Set<JobConfig>

			JobAnno jobAnno = item.getAnnotation(JobAnno.class);

			JobConfig config = new JobConfig.Builder(jobAnno.jobName(), item)
					.setGroupName(jobAnno.groupName())
					.setConcurrent(jobAnno.concurrent())
					.setTriggerName(jobAnno.triggerName())
					.setActived(jobAnno.actived())
					.setCronExpression(jobAnno.cronExpression()).build();

			return config;
		}).collect(Collectors.toSet());

		log.info("需要执行的定时任务个数为------>{}", jobConfigSet.size());

		long filterEndTime = System.currentTimeMillis();

		log.info("从包里面扫描对象到过滤对象总共耗时---->{}ms", filterEndTime - startTime);

		return jobConfigSet.size();
	}

	/**
	 * 开启所有定时任务
	 */
	public void startAllJob(){

		try {

			for (JobConfig item : jobConfigSet) {

				TriggerKey triggerKey = TriggerKey.triggerKey(item.getJobName(), item.getGroupName());
				CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);

				//表达式调度构建器
				CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(item.getCronExpression());

				if (null == trigger) {
					startJob(item);
				} else {
					resetJobCron(item.getJobName(), item.getGroupName(), item.getCronExpression());
				}
				log.info("定时器--{}--启动成功", item.getClazz().getName());
			}

			log.info("全部定时器启动成功 ----------> {}", new Date());

		} catch (SchedulerException e) {
			log.info("启动定时器过程出现异常------->{}", e);
		}

	}

	/**
	 * 新建定时任务
	 *
	 * @param jobClass 定时任务
	 * @return
	 */
	public boolean addJob(Class<?> jobClass) {

		JobAnno jobAnno = jobClass.getAnnotation(JobAnno.class);

		if(jobAnno == null) {
			log.info("对象没有注解");
			return false;
		}

		//组装配置对象
		JobConfig config = new JobConfig.Builder(jobAnno.jobName(), jobClass)
				.setGroupName(jobAnno.groupName())
				.setConcurrent(jobAnno.concurrent())
				.setTriggerName(jobAnno.triggerName())
				.setActived(true)
				.setCronExpression(jobAnno.cronExpression()).build();

		for (JobConfig item : jobConfigSet) {
			if(item.getJobName().equals(config.getJobName())
					&& item.getGroupName().equals(config.getGroupName())) {

				log.info("该定时任务已存在");
				return false;
			}
		}

		// 添加进任务配置类
		jobConfigSet.add(config);
		return startJob(config);
	}

	/**
	 * 重置定时任务的执行时间
	 * @param jobName 任务名
	 * @param cronExpression 新的cron表达式
	 * @return
	 */
	public boolean resetJob(String jobName, String cronExpression) {
		return resetJob(jobName, JobConstant.GROUP_NAME, cronExpression);
	}

	/**
	 * 重置定时任务的执行时间
	 * @param jobName 任务名
	 * @param jobGroupName 任务组名
	 * @param cronExpression 新的cron表达式
	 * @return
	 */
	public boolean resetJob(String jobName, String jobGroupName, String cronExpression) {

		for (JobConfig config : jobConfigSet) {
			if(config.getJobName().equals(jobName) && config.getGroupName().equals(jobGroupName)
					&& !config.getCronExpression().equals(cronExpression)) {

				return resetJobCron(jobName, jobGroupName, cronExpression);
			}
		}

		log.info("对应的定时任务没有找到或者需要重置的时间和原来的一样,跳过了");
		return false;
	}

	/**
	 * 移除某个任务
	 * @param jobName 任务名
	 * @return
	 */
	public boolean removeJob(String jobName) {
		return removeJob(jobName, JobConstant.GROUP_NAME);
	}

	/**
	 * 移除某个任务
	 * @param jobName		任务名
	 * @param groupName		任务组名
	 * @return
	 */
	public boolean removeJob(String jobName, String groupName) {

		boolean removeResult =  false;

		for (JobConfig config : jobConfigSet) {
			if(config.getJobName().equals(jobName) && config.getGroupName().equals(groupName)) {
				removeResult = jobConfigSet.remove(config);
				break;
			}
		}

		if(!removeResult) {
			log.info("找不到需要删除的任务{}：{}", jobName, groupName);
			return false;
		}

		JobKey jobKey = new JobKey(jobName, groupName);

		try {
			JobDetail jobDetail = scheduler.getJobDetail(jobKey);

			if(jobDetail == null) {
				log.info("定时任务{}:{} 不存在", groupName, jobName);
				return false;
			}
			scheduler.deleteJob(jobKey);
			return true;
		} catch (SchedulerException e) {
			log.warn("获取{}:{}任务的详情失败---->{}", e);
		}
		return false;
	}


	/**
	 * 暂停某个任务
	 * @param jobName 任务名
	 * @return
	 */
	public boolean pauseJob(String jobName) {
		return pauseJob(jobName, JobConstant.GROUP_NAME);
	}

	/**
	 * 暂停某个任务
	 * @param jobName 任务名
	 * @param groupName 任务组名
	 * @return
	 */
	public boolean pauseJob(String jobName, String groupName) {

		JobKey jobKey = new JobKey(jobName, groupName);

		try {
			JobDetail jobDetail = scheduler.getJobDetail(jobKey);
			if(jobDetail == null) {
				log.info("定时任务{}:{} 不存在", groupName, jobName);
				return false;
			}
			scheduler.pauseJob(jobKey);
			return true;
		} catch (SchedulerException e) {
			log.warn("获取{}:{}任务的详情失败---->{}", e);
		}
		return false;
	}

	/**
	 * 恢复某个任务
	 * @param jobName 任务名
	 * @return
	 */
	public boolean resumeJob(String jobName) {
		return resumeJob(jobName, JobConstant.GROUP_NAME);
	}

	/**
	 * 恢复某个任务
	 * @param jobName 任务名
	 * @param groupName 任务组名
	 * @return
	 */
	public boolean resumeJob(String jobName, String groupName) {

		JobKey jobKey = new JobKey(jobName, groupName);

		try {
			JobDetail jobDetail = scheduler.getJobDetail(jobKey);

			if(jobDetail == null) {
				log.info("定时任务{}:{} 不存在", groupName, jobName);
				return false;
			}
			scheduler.resumeJob(jobKey);
			return true;
		} catch (SchedulerException e) {
			log.warn("获取{}:{}任务的详情失败---->{}", e);
		}
		return false;
	}

	/**
	 * 暂停所有的定时器任务
	 * @return
	 */
	public boolean pauseAllJob(){
		try {
			scheduler.pauseAll();
			return true;
		} catch (SchedulerException e) {
			log.warn("暂停所有定时任务失败------->{}", e);
		}

		return false;
	}

	/**
	 * 重启所有任务
	 * @return
	 */
	public boolean resumeAll() {
		try {
			scheduler.resumeAll();
			return true;
		} catch (SchedulerException e) {
			log.warn("重启所有定时任务失败------->{}", e);
		}
		return false;
	}

	/**
	 * 创建并启动一个新的任务
	 * @param config
	 * @return
	 */
	private boolean startJob(JobConfig config) {

		// 创建job的详情描述类
		JobDetail jobDetail = JobBuilder.newJob(config.getClazz()).withIdentity(config.getJobName(),
				config.getGroupName()).build();

		//如果有需要传递参数到定时任务里面的,可以通过jobDetail.getJobDataMap().put(key, value) 添加数据
		// 在实现Job接口的类中，可以通过 (强转)jobExecutionContext.getMergedJobDataMap().get(key)获取

		//基于表达式构建触发器
		CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(config.getCronExpression());

		//按cronExpression表达式构建一个新的trigger
		CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(config.getJobName(), config.getGroupName())
				.withSchedule(scheduleBuilder).build();

		try {
			scheduler.scheduleJob(jobDetail, trigger);
			return true;
		} catch (SchedulerException e) {
			log.warn("启动定时器{}:{}失败----->{}", config.getGroupName(), config.getJobName(), e);
		}
		return false;
	}

	/**
	 * 重置定时任务
	 * @param jobName	任务名
	 * @param groupName 组名
	 * @param cronExpression cron表达式
	 * @return
	 */
	private boolean resetJobCron(String jobName, String groupName, String cronExpression) {

		TriggerKey triggerKey = TriggerKey.triggerKey(jobName, groupName);

		//表达式调度构建器
		CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
		try {

			CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);

			// 如果cronExpression为某天的某个时间点执行，而当前时间和表达式里面的时间点相差2个小时
			//以上，这个定时任务会立即执行一次，解决：加上startAt()
			trigger = trigger.getTriggerBuilder().startAt(new Date()).withIdentity(triggerKey)
					.withSchedule(scheduleBuilder).build();

			// 需要有参数，并且需要修改的记得 trigger.getJobDataMap().put(key, value)进行修改

			//按新的trigger重新设置job执行
			scheduler.rescheduleJob(triggerKey, trigger);

			return true;
		} catch (SchedulerException e) {
			log.warn("定时任务{}:{}重置失败------>{}", groupName, jobName, e);
			e.printStackTrace();
		}
		return false;
	}
}
