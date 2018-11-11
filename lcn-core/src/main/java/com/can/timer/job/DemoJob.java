package com.can.timer.job;

import com.can.configuration.quqrtz.model.JobAnno;
import com.can.timer.JobNameConstant;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @description:
 * @author: LCN
 * @date: 2018-11-11 16:39
 */

@JobAnno(jobName = JobNameConstant.DEMO_JOB,  cron = "0 18/1 * * * ? *")
public class DemoJob implements Job {

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println("定时任务报时----->当前时间" + sdf.format(new Date()) );
	}
}
