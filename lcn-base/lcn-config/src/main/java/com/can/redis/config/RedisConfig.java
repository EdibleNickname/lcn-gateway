package com.can.redis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.reflect.Method;
import java.time.Duration;

/**
 * @description:
 * @author: LCN
 * @date: 2018-05-22 11:20
 */

@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

	/**
	 * 定义缓存数据 key 生成策略的bean  包名+类名+方法名+所有参数
	 *
	 * @return
	 */
	@Bean
	public KeyGenerator wiselyKeyGenerator() {
		return new KeyGenerator() {
			@Override
			public Object generate(Object target, Method method, Object... params) {
				StringBuilder sb = new StringBuilder();
				sb.append(target.getClass().getName());
				sb.append(method.getName());
				for (Object obj : params) {
					sb.append(obj.toString());
				}
				return sb.toString();
			}
		};
	}

	/**
	 * 缓存管理者
	 *
	 * @param connectionFactory
	 * @return
	 */
	@Bean
	public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {

		RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory);

		RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig();

		/**
		 	这样设计，默认的过期时间不起作用
			//设置默认超过期时间是30秒
		 	defaultCacheConfig.entryTtl(Duration.ofSeconds(30));

		 	RedisCacheManager cacheManager = new RedisCacheManager(redisCacheWriter, defaultCacheConfig);

		 	return cacheManager;
		*/


		/** 把配置改成这样，默认的过期时间才会起作用 */
		RedisCacheConfiguration configuration = defaultCacheConfig.entryTtl(Duration.ofSeconds(30));
		RedisCacheManager cacheManager = new RedisCacheManager(redisCacheWriter, configuration);

		return cacheManager;

	}

	/**
	 * 设置序列化
	 *
	 * @param factory
	 * @return
	 */
	@Bean
	public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {

		StringRedisTemplate template = new StringRedisTemplate(factory);
		Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		jackson2JsonRedisSerializer.setObjectMapper(om);

		// value值的序列化采用fastJsonRedisSerializer
		template.setValueSerializer(jackson2JsonRedisSerializer);
		template.setValueSerializer(jackson2JsonRedisSerializer);

		// key的序列化采用StringRedisSerializer
		template.setKeySerializer(new StringRedisSerializer());
		template.setHashKeySerializer(new StringRedisSerializer());

		template.afterPropertiesSet();
		return template;
	}

	/**
	 * 注入 redisTemple
	 *
	 * @param redisConnectionFactory
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean(StringRedisTemplate.class)
	public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
		StringRedisTemplate template = new StringRedisTemplate();
		template.setConnectionFactory(redisConnectionFactory);
		return template;
	}

}
