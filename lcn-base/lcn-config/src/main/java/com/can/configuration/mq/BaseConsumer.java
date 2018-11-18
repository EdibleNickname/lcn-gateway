package com.can.configuration.mq;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: RabbitMq 处理基类
 * 在消息处理失败超过3次时，会对消息进行丢弃
 *
 * @author: LCN
 * @date: 2018-11-17 19:31
 */

@Slf4j
public abstract class BaseConsumer<T> {

	private volatile Map<Long, Integer> retryTime = new ConcurrentHashMap<>();

	/** 消息重发最大次数 */
	private final static int MAX_RETRY_TIME = 3;

	@RabbitHandler
	public void handler(T msg, Channel channel, @Headers Map<String,Object> map) {

		log.info("map的结果------->{}", JSONObject.toJSONString(map));

		log.info("RabbitMq消息处理类收到消息---->{}", JSONObject.toJSONString(msg));

		// 消息业务处理
		boolean result = process(msg);

		// 消息标识
		Long tag = (Long)map.get(AmqpHeaders.DELIVERY_TAG);

		try {

			if(!result) {
				log.info("消息--->{}，业务处理失败,进行重发", tag);
				msgNack(channel, tag, true);
			}

			log.info("消息--->{}，处理成功,进行消费确认", tag);
			msgAck(channel, tag, false);

		}catch (IOException e) {
			log.info("对消息进行处理失败");
		}

	}

	/**
	 * 消息消费确认
	 *
	 * @param channel		消息通道
	 * @param deliveryTag	消息的索引
	 * @param multiple		是否批量
	 * @throws IOException
	 */
	private void msgAck(Channel channel, Long deliveryTag, boolean multiple) throws IOException {

		Integer time = retryTime.get(deliveryTag);

		if(time != null && time > MAX_RETRY_TIME) {
			log.info("消息---->{}超过最大重发次数, 进行消息丢弃", deliveryTag);
			msgReject(channel, deliveryTag);
			return;
		}

		// 第二次，第三次 消息重发
		if(time != null) {
			time ++ ;
			retryTime.put(deliveryTag, time);
		}

		// 第一次 消息重发
		if(time == null) {
			time = 1;
			retryTime.put(deliveryTag, time);
		}

		try {
			channel.basicAck(deliveryTag,multiple);
		} catch (IOException e) {
			log.info("rabbitmq消息确认消费出现异常---->{}", e);
			throw e;
		}
	}

	/**
	 *	消息重发
	 *
	 * @param channel		消息通道
	 * @param deliveryTag	消息的索引
	 * @param multiple 		是否批量		true:将一次性拒绝所有小于deliveryTag的消息
	 * @throws IOException
	 */
	private void msgNack(Channel channel, Long deliveryTag, boolean multiple) throws IOException {
		try {
			channel.basicNack(deliveryTag, multiple, true);
		} catch (IOException e) {
			log.info("rabbitmq要求消息重发出现异常---->{}", e);
			throw e;
		}
	}

	/**
	 * 消息拒绝，丢弃该消息
	 *
	 * @param channel		消息通道
	 * @param deliveryTag	消息的索引
	 * @throws IOException
	 */
	private void msgReject(Channel channel, Long deliveryTag) throws IOException {
		log.warn("消息---->{}进行消息丢弃", deliveryTag);
		try {
			channel.basicReject(deliveryTag, false);
		} catch (IOException e) {
			log.info("rabbitmq消息拒绝出现异常---->{}", e);
			throw e;
		}
	}

	/**
	 * 消息的具体处理
	 *
	 * @param msg
	 * @return
	 */
	public abstract boolean process(T msg);
}
