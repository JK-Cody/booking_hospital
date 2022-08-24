package com.hospital.task.preSchedule;

import com.hospital.commom.rabbit.RabbitService;
import com.hospital.commom.rabbit.config.RabbitConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务设置接口
 */
@Component
@EnableScheduling  //计划任务注解
public class ScheduledTask {

    @Autowired
    private RabbitService rabbitService;

    /**
     * 每天8点执行 提醒就诊
     */
    //@Scheduled(cron = "0 0 1 * * ?")  //每天1点时候
    @Scheduled(cron = "0/30 * * * * ?") //每隔30秒
    public void task1() {
        rabbitService.sendMessage(RabbitConstant.EXCHANGE_DIRECT_TASK, RabbitConstant.ROUTING_TASK_8, "");
    }
}

