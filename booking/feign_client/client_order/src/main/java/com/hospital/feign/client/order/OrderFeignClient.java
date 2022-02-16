package com.hospital.feign.client.order;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import vo.order.OrderCountQueryVo;

import java.util.Map;

@FeignClient(name = "service-client-order")
@Repository
public interface OrderFeignClient {

    //调用模块获取所有客户端订单统计数据图表
    @GetMapping("/api/order/inner/getOrderStatistics")
    public Map<String, Object> getOrderStatistics(@RequestBody OrderCountQueryVo orderCountQueryVo);
}
