package com.hospital.commom.rabbit.config;

/**
 * Rabbit短信常量
 */
public class RabbitConstant {

    /**
     * 预约下单信息常量
     */
    public static final String EXCHANGE_DIRECT_ORDER  = "exchange.direct.order";
    public static final String ROUTING_ORDER = "order";
    public static final String QUEUE_ORDER  = "queue.order";      //队列

    /**
     * 短信信息常量
     */
    public static final String EXCHANGE_DIRECT_MSM = "exchange.direct.msm";
    public static final String ROUTING_MSM_ITEM = "msm.item";
    public static final String QUEUE_MSM_ITEM  = "queue.msm.item";  //队列

    /**
     * 定时任务常量
     */
    public static final String EXCHANGE_DIRECT_TASK = "exchange.direct.task";
    public static final String ROUTING_TASK_8 = "task.8";
    //队列
    public static final String QUEUE_TASK_8 = "queue.task.8";

}
