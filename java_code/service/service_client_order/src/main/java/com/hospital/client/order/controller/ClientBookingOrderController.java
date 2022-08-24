package com.hospital.client.order.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.client.order.service.ClientOrderService;
import com.hospital.common.Config.utils.AuthContextHolder;
import com.hospital.common.result.Result;
import enums.OrderStatusEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import model.order.OrderInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vo.order.OrderCountQueryVo;
import vo.order.OrderQueryVo;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Api(tags = "用户预约订单管理客户端接口")
@RestController
@RequestMapping("/api/order")
public class ClientBookingOrderController {

    @Autowired
    private ClientOrderService clientOrderService;

    @ApiOperation(value = "创建订单")
    @PostMapping("auth/submitOrder/{scheduleId}/{patientId}")
    public Result submitOrder(
            @ApiParam(name = "scheduleId", value = "排班id", required = true)
            @PathVariable String scheduleId,
            @ApiParam(name = "patientId", value = "就诊人id", required = true)
            @PathVariable Long patientId) {
        return Result.ok(clientOrderService.saveOrder(scheduleId, patientId));
    }

    @ApiOperation(value = "根据id查询订单详情")
    @GetMapping("auth/getOrder/{orderId}")
    public Result getOrder(@PathVariable String orderId) {
        OrderInfo orderInfo = clientOrderService.getOrder(orderId);
        return Result.ok(orderInfo);
    }


    @ApiOperation(value = "条件查询带分页")
    @GetMapping("auth/getOrderList/{page}/{limit}")
    public Result getOrderList(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,
            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit,
            @ApiParam(name = "orderQueryVo", value = "查询对象", required = false)
            OrderQueryVo orderQueryVo,
            HttpServletRequest request) {
        //设置当前用户id
        orderQueryVo.setUserId(AuthContextHolder.getUserId(request));
        Page<OrderInfo> pageParam = new Page<>(page,limit);
        IPage<OrderInfo> pageModel =
                clientOrderService.getOrderListPage(pageParam,orderQueryVo);
        return Result.ok(pageModel);
    }


    @ApiOperation(value = "获取订单状态")
    @GetMapping("auth/getOrderStatusList")
    public Result getStatusList() {
        return Result.ok(OrderStatusEnum.getStatusList());
    }


    @ApiOperation(value = "取消订单")
    @GetMapping("auth/cancelOrder/{orderId}")
    public Result cancelOrder(
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @PathVariable("orderId") Long orderId) {
        return Result.ok(clientOrderService.cancelOrder(orderId));
    }


    @ApiOperation(value = "获取所有客户端订单统计数据图表,Feign模块的内部访问方法")
    @PostMapping("inner/getOrderStatistics")
    public Map<String, Object> getOrderStatistics(@RequestBody OrderCountQueryVo orderCountQueryVo) {
        return clientOrderService.getClientOrderStatisticsData(orderCountQueryVo);
    }

}
