package com.hospital.client.login.controller.client;

import com.hospital.client.login.service.PatientService;
import com.hospital.common.Config.utils.AuthContextHolder;
import com.hospital.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import model.user.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(tags = "用户名下就诊人管理客户端接口")
@RestController
@RequestMapping("/api/client/patient")
public class ClientPatientController {

    @Autowired
    private PatientService patientService;

    @ApiOperation(value = "获取就诊人列表")
    @GetMapping("auth/getPatientList")
    public Result findPatientList(HttpServletRequest request) {
        //获取当前登录用户id
        Long userId = AuthContextHolder.getUserId(request);
        List<Patient> list = patientService.getPatientListById(userId);
        return Result.ok(list);
    }

    @ApiOperation(value = "根据id获取就诊人信息")
    @GetMapping("auth/getPatient/{id}")
    public Result getPatient(@PathVariable Long id) {
        Patient patient = patientService.getPatientById(id);
        return Result.ok(patient);  //返回结果类
    }

    @ApiOperation(value = "添加就诊人")
    @PostMapping("auth/savePatient")
    public Result savePatient(@RequestBody Patient patient, HttpServletRequest request) {
        //获取当前登录用户id
        Long userId = AuthContextHolder.getUserId(request);
        patient.setUserId(userId);
        patientService.save(patient);
        return Result.ok();
    }

    @ApiOperation(value = "修改就诊人")
    @PostMapping("auth/updatePatient")
    public Result updatePatient(@RequestBody Patient patient) {
        patientService.updateById(patient);
        return Result.ok();
    }

    @ApiOperation(value = "删除就诊人")
    @DeleteMapping("auth/removePatient/{id}")
    public Result removePatient(@PathVariable Long id) {
        patientService.removeById(id);  //自有方法
        return Result.ok();
    }


    @ApiOperation(value = "根据id获取就诊人信息,Feign模块的内部访问方法")
    @GetMapping("inner/getPatientById/{id}")
    public Patient getPatientById(
            @ApiParam(name = "id", value = "就诊人id", required = true)
            @PathVariable("id") Long id) {
        //获取单个就诊人信息
        return patientService.getPatientById(id);
    }
}
