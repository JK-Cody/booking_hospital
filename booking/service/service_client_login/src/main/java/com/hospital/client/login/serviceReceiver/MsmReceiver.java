package com.hospital.client.login.serviceReceiver;

import com.hospital.client.login.service.MsmService;
import com.hospital.commom.rabbit.config.RabbitConstant;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vo.msm.MsmVo;

@Component
public class MsmReceiver {

    @Autowired
    private MsmService msmService;

    /**
     * 消息接收代码块
     * @param msmVo
     * @param message
     * @param channel
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = RabbitConstant.QUEUE_MSM_ITEM, durable = "true"),
            exchange = @Exchange(value = RabbitConstant.EXCHANGE_DIRECT_MSM),
            key = {RabbitConstant.ROUTING_MSM_ITEM}
    ))

    /**
     * 发送短信
     */
    public void sendBackMsm(MsmVo msmVo, Message message, Channel channel) {
        msmService.sendMsmVo(msmVo);
    }
}
