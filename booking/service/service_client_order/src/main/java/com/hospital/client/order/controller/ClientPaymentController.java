package com.hospital.client.order.controller;

import com.hospital.client.order.service.ClientPaymentService;
import com.hospital.client.order.service.WechatPaymentService;
import com.hospital.common.result.Result;
import enums.PaymentTypeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Api(tags = "用户预约订单支付客户端接口")
@RestController
@RequestMapping("/api/order/payment")
public class ClientPaymentController {

    @Autowired
    private WechatPaymentService wechatPaymentService;

    @Autowired
    private ClientPaymentService clientPaymentService;

    @ApiOperation(value = "生成订单支付二维码链接")
    @GetMapping("/createWechatPayment/{orderId}")
    public Result createWechatPayment(
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @PathVariable("orderId") Long orderId) {
        return Result.ok(wechatPaymentService.createWechatPaymentNative(orderId));
    }

    @ApiOperation(value = "查询订单支付状态")
    @GetMapping("/getWechatPaymentStatus/{orderId}")
    public Result getWechatPaymentStatus(
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @PathVariable("orderId") Long orderId) {
        //调用查询接口
        Map<String, String> WechatPaymentListStatus = wechatPaymentService.getWechatPaymentStatus(orderId, PaymentTypeEnum.WEIXIN.name());
        if (WechatPaymentListStatus == null) {
            return Result.fail().message("支付出错");
        }
        //如果当支付成功，更新数据表
        if ("SUCCESS".equals(WechatPaymentListStatus.get("trade_state"))) {
            //更改订单状态，处理支付结果
            String out_trade_no = WechatPaymentListStatus.get("out_trade_no");
            clientPaymentService.paymentToSuccess(out_trade_no, PaymentTypeEnum.WEIXIN.getStatus(), WechatPaymentListStatus);
            return Result.ok().message("支付成功");
        }
        return Result.ok().message("支付中");
    }


}
