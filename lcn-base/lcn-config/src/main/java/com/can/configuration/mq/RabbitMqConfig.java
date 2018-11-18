package com.can.configuration.mq;

import com.can.configuration.mq.Mode.RabbitMsgMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @description:
 * @author: LCN
 * @date: 2018-11-16 15:31
 */

@Configuration
public class RabbitMqConfig {


	@Resource
	private ConnectionFactory connectionFactory;

	@Bean
	public RabbitTemplate rabbitTemplate() {
		RabbitTemplate template = new RabbitTemplate(connectionFactory);
		return template;
	}

	/**
	 * Direct 模式
	 * @return
	 */
	@Bean
	public DirectExchange defaultExchange() {
		return new DirectExchange(RabbitMsgMode.Exchange.DIRECT_EXCHANGE);
	}
	@Bean
	public Queue queue() {
		//队列持久
		return new Queue(RabbitMsgMode.Queue.DIRECT_QUEUE, true);
	}
	@Bean
	public Binding binding() {
		return BindingBuilder.bind(queue()).to(defaultExchange()).with(RabbitMsgMode.RoutingKey.DIRECT_ROUTINGKEY);
	}

}
