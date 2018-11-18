package com.can.mq;

import com.alibaba.fastjson.JSONObject;
import com.can.configuration.mq.BaseConsumer;
import com.can.configuration.mq.Mode.RabbitMsgMode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: LCN
 * @date: 2018-11-17 21:14
 */
@Slf4j
@Component
@RabbitListener(queues = RabbitMsgMode.Queue.DIRECT_QUEUE)
public class DemoDirectConsumer extends BaseConsumer<Demo> {

	@Override
	public boolean process(Demo msg) {

		log.info("消费者--->{}收到消息为---->{}", "direct", JSONObject.toJSONString(msg));

		return true;
	}


}
