package com.hospital.feign.client.hospital;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vo.hosp.ScheduleOrderVo;
import vo.order.SignInfoVo;

@FeignClient(name = "service-hospital")
@Repository
public interface HospitalFeignClient {

    /**
     * 根据排班id获取预约下单数据
     */
    @GetMapping("/api/hosp/client/inner/getScheduleOrder/{scheduleId}")
    ScheduleOrderVo getScheduleOrder(@PathVariable("scheduleId") String scheduleId);

    /**
     * 获取医院签名信息
     */
    @GetMapping("/api/hosp/client/inner/getSignInfo/{hoscode}")
    SignInfoVo getSignInfo(@PathVariable("hoscode") String hoscode);

}
