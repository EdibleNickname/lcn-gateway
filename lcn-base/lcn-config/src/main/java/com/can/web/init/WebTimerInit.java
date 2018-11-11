package com.can.web.init;

import com.can.configuration.quqrtz.JobManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @description: 项目启动时进行加载 --- 定时任务
 * @author: LCN
 * @date: 2018-11-06 22:56
 */

@Slf4j
@Order(1)
@Component
public class WebTimerInit implements ApplicationRunner {

	@Resource
	private JobManager jobManager;

	@Value("${timer.job.backPackage:com.can.timer.job}")
	private String basePackage;

	@Async
	@Override
	public void run(ApplicationArguments args) {

		int jobNum = jobManager.getTaskList(basePackage);

		if (jobNum == 0) {
			log.info("没有定时任务需要执行");
			return;
		}
		//开启定时任务
		jobManager.startAllJob();
	}

}
