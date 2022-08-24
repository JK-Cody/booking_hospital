package com.hospital.client.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.client.order.mapper.WechatPaymentRefundMapper;
import com.hospital.client.order.service.WechatPaymentRefundService;
import enums.RefundStatusEnum;
import model.order.PaymentInfo;
import model.order.RefundInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class WechatPaymentRefundServiceImpl extends ServiceImpl<WechatPaymentRefundMapper, RefundInfo> implements WechatPaymentRefundService {

    @Override
    public RefundInfo saveRefundInfo(PaymentInfo paymentInfo) {

//查询单个订单
        QueryWrapper<RefundInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", paymentInfo.getOrderId());
        queryWrapper.eq("payment_type", paymentInfo.getPaymentType());
        RefundInfo refundInfo = baseMapper.selectOne(queryWrapper);

        if (null != refundInfo){
            return refundInfo;
        }
//无数据时添加资料
        refundInfo = new RefundInfo();
        refundInfo.setCreateTime(new Date());
        refundInfo.setOrderId(paymentInfo.getOrderId());
        refundInfo.setPaymentType(paymentInfo.getPaymentType());
        refundInfo.setOutTradeNo(paymentInfo.getOutTradeNo());
        refundInfo.setRefundStatus(RefundStatusEnum.UNREFUND.getStatus());
        refundInfo.setSubject(paymentInfo.getSubject());
        refundInfo.setTotalAmount(paymentInfo.getTotalAmount());

        baseMapper.insert(refundInfo);
        return refundInfo;
    }



}
