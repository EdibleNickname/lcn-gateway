package com.can.mq;

import com.can.configuration.mq.RabbitMqConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: LCN
 * @date: 2018-11-16 15:29
 */
@Slf4j
@Component
@RabbitListener(queues = RabbitMqConfig.QUEUE_A)
public class DemoMqConsumer extends TestConsumer {

	@Override
	boolean process(String content) {
		log.info("客户端收到消息了----->{}", content);
		return false;
	}
}
