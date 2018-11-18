package com.can.configuration.mq.Mode;

/**
 * @description: 消息转发模式
 * @author: LCN
 * @date: 2018-11-17 12:50
 */
public class RabbitMsgMode {

	/**
	 * 需要建立的交换机
	 */
	public interface Exchange {
		String DIRECT_EXCHANGE = "direct-exchange";
	}

	/**
	 * 需要建立的队列
	 */
	public interface Queue {
		String DIRECT_QUEUE = "direct-queue";
	}

	/**
	 * Direct模式的路由key
	 */
	public interface RoutingKey {
		String DIRECT_ROUTINGKEY = "direct-routingKey";
	}
}
