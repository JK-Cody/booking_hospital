package com.hospital.client.order.serviceReceiver;


import com.hospital.client.order.service.ClientOrderService;
import com.hospital.commom.rabbit.config.RabbitConstant;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.channels.Channel;

@Component
public class MsmReceiver {

    @Autowired
    private ClientOrderService clientOrderService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = RabbitConstant.QUEUE_TASK_8, durable = "true"),
            exchange = @Exchange(value = RabbitConstant.EXCHANGE_DIRECT_TASK),
            key = {RabbitConstant.ROUTING_TASK_8}
    ))
    public void patientTips(Message message, Channel channel) throws IOException {
        clientOrderService.sendPatientNotice();
    }


}
