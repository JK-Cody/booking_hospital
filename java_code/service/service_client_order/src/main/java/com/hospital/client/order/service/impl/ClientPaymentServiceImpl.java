package com.hospital.client.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.client.order.mapper.WechatPaymentMapper;
import com.hospital.client.order.service.ClientOrderService;
import com.hospital.client.order.service.ClientPaymentService;
import com.hospital.feign.client.hospital.HospitalFeignClient;
import com.hospital.common.Config.helper.HttpRequestHelper;
import com.hospital.common.exception.bookException;
import com.hospital.common.result.ResultCodeEnum;
import enums.OrderStatusEnum;
import enums.PaymentStatusEnum;
import model.order.OrderInfo;
import model.order.PaymentInfo;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vo.order.SignInfoVo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class ClientPaymentServiceImpl extends
        ServiceImpl<WechatPaymentMapper, PaymentInfo> implements ClientPaymentService {

    @Autowired
    ClientOrderService clientOrderService;

    @Autowired
    HospitalFeignClient hospitalFeignClient;

    @Override
    public void savePaymentInfo(OrderInfo orderInfo, Integer paymentType) {

//查询payment_info表的数据数量
        Integer count = this.queryPaymentInfoByOrderInfo(orderInfo, paymentType);
//保存支付数据
        //有结果表示不需处理
        if(count >0) {
            return;
        }
        // 否则保存支付记录
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setOrderId(orderInfo.getId());
        paymentInfo.setPaymentType(paymentType);
        paymentInfo.setOutTradeNo(orderInfo.getOutTradeNo());
        paymentInfo.setPaymentStatus(PaymentStatusEnum.UNPAID.getStatus());  //支付中
        String subject = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd")+"|"+orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle();
        paymentInfo.setSubject(subject);
        paymentInfo.setTotalAmount(orderInfo.getAmount());
        baseMapper.insert(paymentInfo); //保存到表
    }


    @Override
    public void paymentToSuccess(String outTradeNo, Integer paymentType, Map<String, String> paramMap) {

//查询数据库获取支付订单
        PaymentInfo paymentInfo = this.queryPaymentInfoByOutTradeNo(outTradeNo, paymentType);
        if (null == paymentInfo) {
            throw new bookException(ResultCodeEnum.PARAM_ERROR);
        }
        //已支付则不处理
        if (paymentInfo.getPaymentStatus() != PaymentStatusEnum.UNPAID.getStatus()) {
            return;
        }
//修改支付订单
        paymentInfo.setPaymentStatus(PaymentStatusEnum.PAID.getStatus()); //设置状态为已支付
        paymentInfo.setTradeNo(paramMap.get("transaction_id"));
        paymentInfo.setCallbackTime(new Date());
        paymentInfo.setCallbackContent(paramMap.toString());
        //更新支付订单
        baseMapper.updateById(paymentInfo);
//修改OrderInfo表的订单状态为已支付
        OrderInfo orderInfo = clientOrderService.getById(paymentInfo.getOrderId());
        orderInfo.setOrderStatus(OrderStatusEnum.PAID.getStatus());
        clientOrderService.updateById(orderInfo);
//获取service_hospital模块的签名信息
        SignInfoVo signInfoVo = hospitalFeignClient.getSignInfo(orderInfo.getHoscode());
        if(null == signInfoVo) {
            throw new bookException(ResultCodeEnum.PARAM_ERROR);
        }

        Map<String, Object> orderUpdateMap = new HashMap<>();
        orderUpdateMap.put("hoscode",orderInfo.getHoscode());
        orderUpdateMap.put("recordId",orderInfo.getBookingRecordId());
        orderUpdateMap.put("timestamp", HttpRequestHelper.getTimestamp());
        //获取签名信息的医院签名
        String sign = HttpRequestHelper.getSign(orderUpdateMap, signInfoVo.getSignKey());
        orderUpdateMap.put("sign", sign);
//通过签名信息的Url（http://localhost:9998），发送请求到manage_hospital模块更新订单
        JSONObject result = HttpRequestHelper.sendRequest(orderUpdateMap, signInfoVo.getApiUrl()+"/order/updatePayStatus");
        if(result.getInteger("code") != 200) {
            throw new bookException(result.getString("message"), ResultCodeEnum.FAIL.getCode());
        }
    }


    @Override
    public PaymentInfo getPaymentInfo(Long orderId, Integer paymentType) {

        return this.queryPaymentInfoByOrderId(orderId,paymentType);
    }


    //+++++++++++++++
     //查询单个支付单
     private PaymentInfo queryPaymentInfoByOutTradeNo(String outTradeNo, Integer paymentType) {

        QueryWrapper<PaymentInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("out_trade_no", outTradeNo);
        queryWrapper.eq("payment_type", paymentType);
        return baseMapper.selectOne(queryWrapper);   //保存到表
    }

    //查询单个支付单
    private PaymentInfo queryPaymentInfoByOrderId(Long orderId, Integer paymentType) {

        QueryWrapper<PaymentInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("out_trade_no", orderId);
        queryWrapper.eq("payment_type", paymentType);
        return baseMapper.selectOne(queryWrapper);   //保存到表
    }

    //查询支付单数量
    private Integer queryPaymentInfoByOrderInfo(OrderInfo orderInfo, Integer paymentType) {

        QueryWrapper<PaymentInfo> queryWrapper = new QueryWrapper<>();
        QueryWrapper<PaymentInfo> order_id = queryWrapper.eq("order_id", orderInfo.getId());
        QueryWrapper<PaymentInfo> payment_type = queryWrapper.eq("payment_type", paymentType);
        Integer count = baseMapper.selectCount(queryWrapper);  //保存到表
        return count;
    }

}
