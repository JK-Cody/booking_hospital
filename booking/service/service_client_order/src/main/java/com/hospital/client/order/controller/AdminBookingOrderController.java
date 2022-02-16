package com.hospital.client.order.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.client.order.service.ClientOrderService;
import com.hospital.common.result.Result;
import enums.OrderStatusEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import model.order.OrderInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vo.order.OrderQueryVo;

@Api(tags = "后台订单接口")
@RestController
@RequestMapping("/admin/order")
public class AdminBookingOrderController {

    @Autowired
    private ClientOrderService clientOrderService;

    @ApiOperation(value = "获取用户订单列表")
    @GetMapping("getOrderList/{page}/{limit}")
    public Result getOrderList(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,
            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit,
            @ApiParam(name = "orderCountQueryVo", value = "查询对象", required = false) OrderQueryVo orderQueryVo) {
        Page<OrderInfo> pageParam = new Page<>(page, limit);
        IPage<OrderInfo> pageModel = clientOrderService.getOrderListPage(pageParam, orderQueryVo);
        return Result.ok(pageModel);
    }


    @ApiOperation(value = "获取用户订单状态列表")
    @GetMapping("getOrderStatusList")
    public Result getOrderStatusList() {
        return Result.ok(OrderStatusEnum.getStatusList());
    }


    @ApiOperation(value = "获取用户名下就诊人列表")
    @GetMapping("getPatientList/{orderId}")
    public Result getPatientList(
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @PathVariable Long id) {
        return Result.ok(clientOrderService.getPatientListByOrderId(id));
    }

}
