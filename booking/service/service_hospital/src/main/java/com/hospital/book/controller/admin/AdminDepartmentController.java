package com.hospital.book.controller.admin;

import com.hospital.book.service.DepartmentService;
import com.hospital.common.result.Result;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vo.hosp.DepartmentVo;

import java.util.List;
import java.util.Map;

/**
 * 后台提供医院数据的查询方法——科室数据
 */
@RestController
@RequestMapping("/admin/hosp/department")
//使用网关配置后不启用
//@CrossOrigin
public class AdminDepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @ApiOperation(value = "获取科室列表")
    @GetMapping("list/{hoscode}")
    public Result listDepartment( @PathVariable String hoscode) {

        List<DepartmentVo> departmentList=departmentService.getDepartmentList(hoscode);
        return Result.ok(departmentList);
    }
}
