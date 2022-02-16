package com.hospital.book.controller.admin;

import com.hospital.book.service.HospitalService;
import com.hospital.common.result.Result;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import model.hosp.Hospital;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import vo.hosp.HospitalQueryVo;

import java.util.Map;

/**
 * 后台提供医院数据的查询方法——医院数据
 */
@RestController
@RequestMapping("/admin/hosp/hospital")
//使用网关配置后不启用
//@CrossOrigin
public class AdminHospitalController {

    @Autowired
    private HospitalService hospitalService;

    @ApiOperation(value = "获取医院列表")
    @GetMapping("list/{page}/{limit}")
    public Result listHospital(@PathVariable Integer page,
                               @PathVariable Integer limit,
                               HospitalQueryVo hospitalQueryVo){

        Page<Hospital> hospitals = hospitalService.selectHospitalPage(page, limit, hospitalQueryVo);
        return Result.ok(hospitals);
    }

    @ApiOperation(value = "更新上线状态")
    @GetMapping("updateStatus/{id}/{status}")
    public Result updateStatus(@ApiParam(name = "id", value = "医院id", required = true)
                    @PathVariable("id") String id,
                    @ApiParam(name = "status", value = "状态（0：未上线 1：已上线）", required = true)
                    @PathVariable("status") Integer status){

            hospitalService.updateHospital(id, status);
            return Result.ok();
        }

    @ApiOperation(value = "获取医院详情")
    @GetMapping("showHospitalDetail/{id}")
    public Result showHospitalDetail(
            @ApiParam(name = "id", value = "医院id", required = true)
            @PathVariable String id) {
        Map<String, Object> hospitalDetail = hospitalService.getHospitalDetail(id);
        return Result.ok(hospitalDetail);
    }

}
