package com.can.configuration.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @author: LCN
 * @date: 2018-11-16 15:31
 */

@Configuration
public class RabbitMqConfig {

	@Value("${spring.rabbitmq.addresses}")
	private String addresses;

	@Value("${spring.rabbitmq.port}")
	private int port;

	@Value("${spring.rabbitmq.username}")
	private String userName;

	@Value("${spring.rabbitmq.password}")
	private String password;

	@Value("${spring.rabbitmq.publisher-confirms:false}")
	private boolean publisherConfirms;

	@Value("${spring.rabbitmq.virtual-host:/}")
	private String virtualHost;

	public static final String EXCHANGE_A = "my-mq-exchange_A";
	public static final String EXCHANGE_B = "my-mq-exchange_B";
	public static final String EXCHANGE_C = "my-mq-exchange_C";

	public static final String QUEUE_A = "QUEUE_A";
	public static final String QUEUE_B = "QUEUE_B";
	public static final String QUEUE_C = "QUEUE_C";

	public static final String ROUTINGKEY_A = "spring-boot-routingKey_A";
	public static final String ROUTINGKEY_B = "spring-boot-routingKey_B";
	public static final String ROUTINGKEY_C = "spring-boot-routingKey_C";

	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory(addresses, port);
		connectionFactory.setUsername(userName);
		connectionFactory.setPassword(password);
		connectionFactory.setVirtualHost(virtualHost);
		connectionFactory.setPublisherConfirms(publisherConfirms);
		return connectionFactory;
	}

	@Bean
	public RabbitTemplate rabbitTemplate() {
		RabbitTemplate template = new RabbitTemplate(connectionFactory());
		return template;
	}

	@Bean
	public DirectExchange defaultExchange() {
		return new DirectExchange(EXCHANGE_A);
	}

	@Bean
	public Queue queueA() {
		//队列持久
		return new Queue(QUEUE_A, true);
	}

	@Bean
	public Binding binding() {
		return BindingBuilder.bind(queueA()).to(defaultExchange()).with(ROUTINGKEY_A);
	}

}
