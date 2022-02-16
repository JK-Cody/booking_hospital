package com.hospital.statistics.controller;

import com.hospital.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vo.order.OrderCountQueryVo;

@Api(tags = "客户端数据生成统计图表接口")
@RestController
@RequestMapping("/admin/statistics")
public class StatisticsController {

    @Autowired
    private OrderFeignClient orderFeignClient;

    @ApiOperation(value = "获取订单统计数据")
    @GetMapping("getOrderStatistics")
    public Result getOrderStatistics(
            @ApiParam(name = "orderCountQueryVo", value = "查询对象", required = false)
                    OrderCountQueryVo orderCountQueryVo) {
        return Result.ok(orderFeignClient.getOrderStatistics(orderCountQueryVo));
    }
}

