package com.hospital.client.order.service;

import java.util.Map;

public interface WechatPaymentService {

    /**
     * 根据订单号下单，生成支付链接
     */
    Map createWechatPaymentNative(Long orderId);

    /**
     * 获取订单支付状态
     * @param orderId
     * @param name
     * @return
     */
    Map getWechatPaymentStatus(Long orderId, String name);

    /***
     * 退款
     * @param orderId
     * @return
     */
    Boolean getWechatPaymentRefund(Long orderId);
}

