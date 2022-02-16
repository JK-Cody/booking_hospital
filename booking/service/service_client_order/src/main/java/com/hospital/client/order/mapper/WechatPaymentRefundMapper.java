package com.hospital.client.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import model.order.RefundInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;

@Mapper
public interface WechatPaymentRefundMapper extends BaseMapper<RefundInfo> {
}
