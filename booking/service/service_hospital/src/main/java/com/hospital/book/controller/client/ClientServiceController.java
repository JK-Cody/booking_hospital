package com.hospital.book.controller.client;

import com.hospital.book.service.DepartmentService;
import com.hospital.book.service.HospitalService;
import com.hospital.book.service.HospitalSetService;
import com.hospital.book.service.ScheduleService;
import com.hospital.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import model.hosp.Hospital;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vo.hosp.DepartmentVo;
import vo.hosp.HospitalQueryVo;
import vo.hosp.ScheduleOrderVo;
import vo.order.SignInfoVo;

import java.util.List;
import java.util.Map;

@Api(tags = "客户端对医院数据的查询方法——医院&科室&排班的数据")
@RestController
@RequestMapping("/api/hosp/client")
public class ClientServiceController {

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private HospitalSetService hospitalSetService;

    @ApiOperation(value = "获取分页列表")
    @GetMapping("clientGetHospitalList/{page}/{limit}")
    public Result clientGetHospitalList(
            @PathVariable Integer page,
            @PathVariable Integer limit,
            HospitalQueryVo hospitalQueryVo) {
        //显示上线的医院
        //hospitalQueryVo.setStatus(0);
        Page<Hospital> hospitalListPage = hospitalService.selectHospitalPage(page, limit, hospitalQueryVo);
        List<Hospital> content = hospitalListPage.getContent();
        int totalPages = hospitalListPage.getTotalPages();
        return Result.ok(hospitalListPage);
    }

    @ApiOperation(value = "根据医院名称获取医院详细")
    @GetMapping("clientGetHospitalDetail/{hosname}")
    public Result clientGetHospitalDetail(
            @ApiParam(name = "hosname", value = "医院名称", required = true)
            @PathVariable String hosname) {
        return Result.ok(hospitalService.getHospitalByHosname(hosname));
    }

    @ApiOperation(value = "根据医院Code获取科室")
    @GetMapping("clientGetDepartmentByHoscode/{hoscode}")
    public Result clientGetDepartmentByHoscode(
            @ApiParam(name = "hoscode", value = "医院code", required = true)
            @PathVariable String hoscode) {
        List<DepartmentVo> departmentList = departmentService.getDepartmentList(hoscode);
        return Result.ok(departmentList);
    }

    @ApiOperation(value = "根据医院Code获取挂号详情")
    @GetMapping("clientGetHospitalByHoscode/{hoscode}")
    public Result clientGetHospitalByHoscode(
            @ApiParam(name = "hoscode", value = "医院code", required = true)
            @PathVariable String hoscode) {
        Map<String, Object> hospitalDetailByHoscode = hospitalService.getHospitalDetailByHoscode(hoscode);
        return Result.ok(hospitalDetailByHoscode);
    }

    @ApiOperation(value = "获取可预约排班数据")
    @GetMapping("auth/getBookingScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public Result getBookingSchedule(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Integer page,
            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Integer limit,
            @ApiParam(name = "hoscode", value = "医院code", required = true)
            @PathVariable String hoscode,
            @ApiParam(name = "depcode", value = "科室code", required = true)
            @PathVariable String depcode) {

        return Result.ok(scheduleService.getBookingScheduleRule(page, limit, hoscode, depcode));
    }

    @ApiOperation(value = "获取单个医院的详细排班数据")
    @GetMapping("auth/getHospitalSchedule/{hoscode}/{depcode}/{workDate}")
    public Result getHospitalSchedule(
            @ApiParam(name = "hoscode", value = "医院code", required = true)
            @PathVariable String hoscode,
            @ApiParam(name = "depcode", value = "科室code", required = true)
            @PathVariable String depcode,
            @ApiParam(name = "workDate", value = "排班日期", required = true)
            @PathVariable String workDate) {

        return Result.ok(scheduleService.getScheduleDetail(hoscode, depcode, workDate));
    }

    @ApiOperation(value = "根据排班id获取排班数据")
    @GetMapping("getSchedule/{scheduleId}")
    public Result getSchedule(
            @ApiParam(name = "scheduleId", value = "排班id", required = true)
            @PathVariable String scheduleId) {
        return Result.ok(scheduleService.getScheduleById(scheduleId));
    }

    @ApiOperation(value = "根据排班id获取预约下单数据,Feign模块的内部访问方法")
    @GetMapping("inner/getScheduleOrder/{scheduleId}")
    public ScheduleOrderVo getScheduleOrder(
            @ApiParam(name = "scheduleId", value = "排班id", required = true)
            @PathVariable("scheduleId") String scheduleId) {
        return scheduleService.getBookingOrder(scheduleId);
    }

    @ApiOperation(value = "获取医院签名信息,Feign模块的内部访问方法")
    @GetMapping("inner/getSignInfo/{hoscode}")
    public SignInfoVo getSignInfo(
            @ApiParam(name = "hoscode", value = "医院code", required = true)
            @PathVariable("hoscode") String hoscode) {
        return hospitalSetService.getSignInformation(hoscode);
    }

}
