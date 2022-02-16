package com.hospital.client.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import model.order.PaymentInfo;
import model.order.RefundInfo;

public interface WechatPaymentRefundService extends IService<RefundInfo> {

    /**
     * 保存退款记录
     * @param paymentInfo
     */
    RefundInfo saveRefundInfo(PaymentInfo paymentInfo);

}
