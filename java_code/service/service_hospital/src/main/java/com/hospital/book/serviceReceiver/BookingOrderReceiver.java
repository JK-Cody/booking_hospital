package com.hospital.book.serviceReceiver;

import com.hospital.book.service.ScheduleService;
import com.hospital.commom.rabbit.RabbitService;
import com.hospital.commom.rabbit.config.RabbitConstant;
import model.hosp.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import vo.msm.MsmVo;
import vo.order.OrderMqVo;

import java.io.IOException;

@Component
public class BookingOrderReceiver {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private RabbitService rabbitService;

    /**
     * 获取预约下单信息常量
     * @param orderMqVo
     * @param message
     * @param channel
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = RabbitConstant.QUEUE_ORDER, durable = "true"),
            exchange = @Exchange(value = RabbitConstant.EXCHANGE_DIRECT_ORDER),
            key = {RabbitConstant.ROUTING_ORDER}
    ))

    /**
     * 预约下单成功的短信发送
     */
    public void receiver(OrderMqVo orderMqVo, Message message, Channel channel) throws IOException {

        if(null!=orderMqVo.getAvailableNumber()) {
            //预约订单时
            Schedule schedule = scheduleService.getScheduleById(orderMqVo.getScheduleId());
            schedule.setReservedNumber(orderMqVo.getReservedNumber());
            schedule.setAvailableNumber(orderMqVo.getAvailableNumber());
            scheduleService.updateTimeForSchedule(schedule);
        }else{
            //取消订单时
            Schedule schedule = scheduleService.getScheduleById(orderMqVo.getScheduleId());
            int availableNumber = schedule.getAvailableNumber().intValue() + 1;  //当预约取消一次后，可预约数加1
            schedule.setAvailableNumber(availableNumber);
            scheduleService.updateTimeForSchedule(schedule);
        }
        //发送短信
        MsmVo msmVo = orderMqVo.getMsmVo();
        if(null != msmVo) {
            rabbitService.sendMessage(RabbitConstant.EXCHANGE_DIRECT_MSM, RabbitConstant.ROUTING_MSM_ITEM, msmVo);
        }
    }

}
