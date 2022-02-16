package com.hospital.client.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import model.order.PaymentInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface  WechatPaymentMapper extends BaseMapper<PaymentInfo> {
}
