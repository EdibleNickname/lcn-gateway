package com.can.mq;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;

import java.io.IOException;

/**
 * @description:
 * @author: LCN
 * @date: 2018-11-16 19:13
 */

@Slf4j
public abstract class TestConsumer {

	@RabbitHandler
	public void handler(String content, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {

		log.info("收到消息了");

		boolean result = process(content);

		log.info("处理完成了" + result);

		try {
			channel.basicAck(tag,false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	abstract boolean process(String content);


}
