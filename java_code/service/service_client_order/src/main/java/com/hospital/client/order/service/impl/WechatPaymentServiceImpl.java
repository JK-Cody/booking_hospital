package com.hospital.client.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import com.hospital.client.order.service.ClientOrderService;
import com.hospital.client.order.service.ClientPaymentService;
import com.hospital.client.order.service.WechatPaymentRefundService;
import com.hospital.client.order.service.WechatPaymentService;
import com.hospital.client.order.utils.ConstantPropertiesForWechatPaymentUtils;
import com.hospital.client.order.utils.HttpClientForWechatPayment;
import enums.PaymentTypeEnum;
import enums.RefundStatusEnum;
import model.order.OrderInfo;
import model.order.PaymentInfo;
import model.order.RefundInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class WechatPaymentServiceImpl implements WechatPaymentService {

        @Autowired
        private ClientOrderService clientOrderService;

        @Autowired
        private ClientPaymentService clientPaymentService;

        @Autowired
        private WechatPaymentRefundService wechatPaymentRefundService;

        @Autowired
        private RedisTemplate redisTemplate;

        /**
         * 根据订单号下单，生成支付链接
         */
        @Override
        public Map createWechatPaymentNative(Long orderId) {
//获取订单数据
            try {
                //获取redis的订单数据
                Map payMapInRedis = (Map) redisTemplate.opsForValue().get(orderId.toString());
                if(null != payMapInRedis) {
                    return payMapInRedis;  //有则直接返回
                }
                //根据id获取订单信息
                OrderInfo orderInfo = clientOrderService.getById(orderId);
                // 保存支付记录
                clientPaymentService.savePaymentInfo(orderInfo, PaymentTypeEnum.WEIXIN.getStatus());

                Map paramMap = new HashMap();
                //获取配置文件参数
                paramMap.put("appid", ConstantPropertiesForWechatPaymentUtils.APPID);
                paramMap.put("mch_id", ConstantPropertiesForWechatPaymentUtils.PARTNER);
                paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
                //获取订单信息
                String body = orderInfo.getReserveDate() + "就诊"+ orderInfo.getDepname();
                paramMap.put("body", body);
                paramMap.put("out_trade_no", orderInfo.getOutTradeNo());
                //订单费用
                paramMap.put("total_fee", orderInfo.getAmount().multiply(new BigDecimal("100")).longValue()+"");
//                paramMap.put("total_fee", "1");
//                paramMap.put("spbill_create_ip", "127.0.0.1");
//                paramMap.put("notify_url", "http://guli.shop/api/order/weixinPay/weixinNotify");
//                paramMap.put("trade_type", "NATIVE");

//发送参数到微信支付接口
                HttpClientForWechatPayment client = new HttpClientForWechatPayment("https://api.mch.weixin.qq.com/pay/unifiedorder");
                //设置参数
                client.setXmlParam(WXPayUtil.generateSignedXml(paramMap, ConstantPropertiesForWechatPaymentUtils.PARTNERKEY));
                client.setHttps(true);
                client.post();

//返回微信支付单的数据
                String paymentDataXml = client.getContent();  //返回的是xml格式数据
                Map<String, String> paymentDataMap = WXPayUtil.xmlToMap(paymentDataXml);  //转为map数据
                //补充数据到微信支付单
                Map map = new HashMap<>();
                map.put("orderId", orderId);
                map.put("totalFee", orderInfo.getAmount());
                map.put("resultCode", paymentDataMap.get("result_code"));
                map.put("codeUrl", paymentDataMap.get("code_url"));  //支付二维码地址

//将微信支付单添加到Redis集合
                if(null != paymentDataMap.get("result_code")) {
                //设置微信支付单的缓存时间（分钟）
                    redisTemplate.opsForValue().set(orderId.toString(), map, 15, TimeUnit.MINUTES);
                }
                return map;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

    @Override
    public Map getWechatPaymentStatus(Long orderId, String name) {

        try {
//获取订单数据
            OrderInfo orderInfo = clientOrderService.getById(orderId);
//封装参数
            Map paramMap = new HashMap<>();
            paramMap.put("appid", ConstantPropertiesForWechatPaymentUtils.APPID);
            paramMap.put("mch_id", ConstantPropertiesForWechatPaymentUtils.PARTNER);
            paramMap.put("out_trade_no", orderInfo.getOutTradeNo());
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
//发送请求的地址到微信官方
            HttpClientForWechatPayment client = new HttpClientForWechatPayment("https://api.mch.weixin.qq.com/pay/orderquery");
            //转为xml格式
            client.setXmlParam(WXPayUtil.generateSignedXml(paramMap, ConstantPropertiesForWechatPaymentUtils.PARTNERKEY));
            client.setHttps(true);
            client.post();
            //返回微信支付单的数据，转成Map
            String xml = client.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);

            return resultMap;
        } catch (Exception e) {
            return null;
        }
    }


    @Override
    public Boolean getWechatPaymentRefund(Long orderId) {

//获取订单
        try {
            PaymentInfo paymentInfo = clientPaymentService.getPaymentInfo(orderId, PaymentTypeEnum.WEIXIN.getStatus());
//保存订单的退款信息
            RefundInfo refundInfo = wechatPaymentRefundService.saveRefundInfo(paymentInfo);
            //订单符合已退款处理时，不再继续
            if(refundInfo.getRefundStatus().intValue() == RefundStatusEnum.REFUND.getStatus().intValue()) {
                return true;
            }
            //否则添加退款信息
            Map<String,String> paramMap = new HashMap<>(8);
            paramMap.put("appid", ConstantPropertiesForWechatPaymentUtils.APPID);       //公众账号ID
            paramMap.put("mch_id", ConstantPropertiesForWechatPaymentUtils.PARTNER);   //商户编号
            paramMap.put("nonce_str",WXPayUtil.generateNonceStr());
            paramMap.put("transaction_id",paymentInfo.getTradeNo()); //微信订单号
            paramMap.put("out_trade_no",paymentInfo.getOutTradeNo()); //商户订单编号
            paramMap.put("out_refund_no","tk"+paymentInfo.getOutTradeNo()); //商户退款单号
//       paramMap.put("total_fee",paymentInfoQuery.getTotalAmount().multiply(new BigDecimal("100")).longValue()+"");
//       paramMap.put("refund_fee",paymentInfoQuery.getTotalAmount().multiply(new BigDecimal("100")).longValue()+"");
            paramMap.put("total_fee","1");
            paramMap.put("refund_fee","1");
//发送退款数据到微信平台
            String paramXml = WXPayUtil.generateSignedXml(paramMap, ConstantPropertiesForWechatPaymentUtils.PARTNERKEY);
            //微信官方地址
            HttpClientForWechatPayment client = new HttpClientForWechatPayment("https://api.mch.weixin.qq.com/secapi/pay/refund");
            client.setXmlParam(paramXml);
            client.setHttps(true);
            client.setCert(true);
            client.setCertPassword(ConstantPropertiesForWechatPaymentUtils.PARTNER);
            client.post();
//从微信获取退款的数据
            String xml = client.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            if (null != resultMap && WXPayConstants.SUCCESS.equalsIgnoreCase(resultMap.get("result_code"))) {
                refundInfo.setCallbackTime(new Date());   //返回信息
                refundInfo.setTradeNo(resultMap.get("refund_id"));
                refundInfo.setRefundStatus(RefundStatusEnum.REFUND.getStatus());
                refundInfo.setCallbackContent(JSONObject.toJSONString(resultMap));
                wechatPaymentRefundService.updateById(refundInfo);
                return true;
            }
            return false;
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
