package com.hospital.book.controller.admin;

import com.hospital.book.service.ScheduleService;
import com.hospital.common.result.Result;
import io.swagger.annotations.ApiOperation;
import model.hosp.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 后台提供医院数据的查询方法——排班数据
 */
@RestController
@RequestMapping("/admin/hosp/schedule")
//使用网关配置后不启用
//@CrossOrigin
public class AdminScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @ApiOperation(value ="查询排班")
    @GetMapping("getSchedule/{page}/{limit}/{hoscode}/{depcode}")
    public Result getSchedule(@PathVariable long page,
                                  @PathVariable long limit,
                                  @PathVariable String hoscode,
                                  @PathVariable String depcode) {
        Map<String,Object> map
                = scheduleService.getScheduleList(page,limit,hoscode,depcode);
        return Result.ok(map);
    }

    @ApiOperation(value ="查询排班详细信息")
    @GetMapping("getScheduleDetail/{hoscode}/{depcode}/{workDate}")
    public Result getScheduleDetail(
                              @PathVariable String hoscode,
                              @PathVariable String depcode,
                              @PathVariable String workDate) {
        List<Schedule> list = scheduleService.getScheduleDetail(hoscode,depcode,workDate);
        return Result.ok(list);
    }
}
