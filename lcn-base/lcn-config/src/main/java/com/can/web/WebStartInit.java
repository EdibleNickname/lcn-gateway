package com.can.web;

import com.can.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: 项目启动时进行加载
 * @author: LCN
 * @date: 2018-11-06 22:56
 */
@Slf4j
@Component
public class WebStartInit implements ApplicationRunner {

	@Resource
	private RedisUtil redisUtil;

	/** 存储在redis中 用于统计用户登录时，输入密码错误 */
	private final static String PW_ERROR_COUNT = "errorCount";

	@Override
	public void run(ApplicationArguments args) throws Exception {

		if(!redisUtil.exists(PW_ERROR_COUNT)) {
			log.info("redis存放用户密码输入错误的对象不存在, 进行创建");
			Map<Integer, Integer> errorCountMap = new HashMap<>(16);
			redisUtil.set(PW_ERROR_COUNT, errorCountMap);
		}

		log.info("redis存放用户密码输入错误的对象已存在, 不进行创建");
	}

}
