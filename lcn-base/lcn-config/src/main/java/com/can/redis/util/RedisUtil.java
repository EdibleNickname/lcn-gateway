package com.can.redis.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @description: Redis工具
 *
 * @author: LCN
 * @date: 2018-05-22 15:14
 */

@Slf4j
@Component
public class RedisUtil {

	private RedisTemplate redisTemplate;

	private ValueOperations<Serializable, Object> operations;

	/** 生成唯一id */
	private RedisAtomicLong counter;

	/** redis中存放这个自增id的值的key*/
	private final static String COUNTER_KEY = "CounterKey";

	@Autowired
	public RedisUtil(RedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
		this.operations = redisTemplate.opsForValue();
		this.counter = new RedisAtomicLong(COUNTER_KEY, redisTemplate.getConnectionFactory());
	}

	/**
	 * 写入缓存
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean set(final String key, Object value) {
		boolean result = false;
		try {
			operations.set(key, value);
			result = true;
		} catch (Exception e) {
			log.error("redis写入缓存失败----->{}", e);
		}
		return result;
	}

	/**
	 * 写入缓存，指定缓存时间
	 * @param key
	 * @param value
	 * @param expireTime
	 * @return
	 */
	public boolean set(final String key, Object value, Long expireTime) {
		boolean result = false;
		try {
			operations.set(key, value);
			redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
			result = true;
		} catch (Exception e) {
			log.error("redis写入有超时的缓存失败----->{}", e);
		}
		return result;
	}

	/**
	 * 写入一个自动生成key的值
	 *
	 * @param value
	 * @param expireTime
	 * @return
	 */
	public String setNoKey (Object value, Long expireTime) {

		final String uniqueKey = String.valueOf(counter.incrementAndGet());

		try {
			operations.set(uniqueKey, value);
			redisTemplate.expire(uniqueKey, expireTime, TimeUnit.SECONDS);
		} catch (Exception e) {
			log.error("redis写入有超时的缓存失败----->{}", e);
		}
		return uniqueKey;
	}

	/**
	 * 判断缓存中是否有对应的value
	 *
	 * @param key
	 * @return
	 */
	public boolean exists(final String key) {
		return redisTemplate.hasKey(key);
	}

	/**
	 * 读取缓存
	 * @param key
	 * @return
	 */
	public Object get(final String key) {
		Object result = result = operations.get(key);
		return result;
	}

	/**
	 * 删除缓存
	 * @param key
	 */
	public boolean remove(final String key) {

		if (exists(key)) {
			redisTemplate.delete(key);
		}
		return true;
	}

	/**
	 * 批量删除对应的key
	 * @param pattern
	 * @return
	 */
	public boolean removeKey(final String pattern) {
		Set<Serializable> keys = redisTemplate.keys(pattern);
		if (keys.size() > 0){
			redisTemplate.delete(keys);
		}
		return true;
	}

	/**
	 * 批量删除对应的value
	 * @param keys
	 */
	public boolean remove(final String... keys) {
		for (String key : keys) {
			remove(key);
		}
		return true;
	}

	/**
	 * 生成唯一id
	 * @return
	 */
	public Long generateId(){
		return counter.incrementAndGet();
	}
}
