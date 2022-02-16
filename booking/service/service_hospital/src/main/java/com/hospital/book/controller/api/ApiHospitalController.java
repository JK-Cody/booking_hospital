package com.hospital.book.controller.api;

import com.alibaba.excel.util.StringUtils;
import com.hospital.book.service.DepartmentService;
import com.hospital.book.service.HospitalService;
import com.hospital.book.service.HospitalSetService;
import com.hospital.book.service.ScheduleService;
import com.hospital.common.Config.helper.HttpRequestHelper;
import com.hospital.common.exception.bookException;
import com.hospital.common.result.Result;
import com.hospital.common.result.ResultCodeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import model.hosp.Department;
import model.hosp.Hospital;
import model.hosp.Schedule;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import vo.hosp.DepartmentQueryVo;
import vo.hosp.ScheduleQueryVo;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Api(tags = "（统一API）提供CRUD方法处理,医院&科室&排班的数据")
@RestController
@RequestMapping("/api/hosp")
public class ApiHospitalController {

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private HospitalSetService hospitalSetService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ScheduleService scheduleService;


    @ApiOperation(value = "保存医院对象")
    @PostMapping("saveHospital")
    public Result saveHospital(HttpServletRequest request) {

        Map<String, Object> requestMap = RequestMap(request);
//将logoData的格式替换为可正常图片格式
        String logoData = (String) requestMap.get("logoData");
        //原本的+被请求转为" "，需改回
        requestMap.put("logoData",logoData.replaceAll(" ", "+"));
        hospitalService.save(requestMap);
        return Result.ok();
    }


    @ApiOperation(value = "获取医院信息")
    @PostMapping("hospital/show")
    public Result hospital(HttpServletRequest request) {

        Map<String, Object> RequestMap = RequestMap(request);
        Hospital hospital = hospitalService.getByHoscode((String) RequestMap.get("hoscode"));
        return Result.ok(hospital);
    }


    @ApiOperation(value = "上传科室")
    @PostMapping("saveDepartment")
    public Result saveDepartment(HttpServletRequest request) {

        Map<String, Object> RequestMap = RequestMap(request);
        departmentService.save(RequestMap);
        return Result.ok();
    }


    @ApiOperation(value = "获取分页列表")
    @PostMapping("department/list")
    public Result department(HttpServletRequest request) {

        Map<String, Object> RequestMap = RequestMap(request);
        //判断页面数据
        int page = StringUtils.isEmpty(RequestMap.get("page")) ? 1 : Integer.parseInt((String)RequestMap.get("page"));
        int limit = StringUtils.isEmpty(RequestMap.get("limit")) ? 5 : Integer.parseInt((String)RequestMap.get("limit"));
        DepartmentQueryVo departmentQueryVo = departmentQuery(RequestMap);
        Page<Department> pageModel = departmentService.selectPage(page, limit, departmentQueryVo);
        return Result.ok(pageModel);
    }


    @ApiOperation(value = "删除科室")
    @PostMapping("department/remove")
    public Result removeDepartment(HttpServletRequest request) {

        Map<String, Object> RequestMap = RequestMap(request);
        departmentQuery(RequestMap);
        departmentService.remove((String) RequestMap.get("hoscode"), (String)RequestMap.get("depcode"));
        return Result.ok();
    }


    @ApiOperation(value = "上传排班")
    @PostMapping("saveSchedule")
    public Result saveSchedule(HttpServletRequest request) {

        Map<String, Object> RequestMap = RequestMap(request);
        ScheduleQueryVo scheduleQuery = scheduleQuery(RequestMap);
        scheduleService.save(RequestMap);
        return Result.ok();
    }


    @ApiOperation(value = "获取排班分页列表")
    @PostMapping("schedule/list")
    public Result schedule(HttpServletRequest request) {

        Map<String, Object> RequestMap = RequestMap(request);
        //判断页面数据
        int page = StringUtils.isEmpty(RequestMap.get("page")) ? 1 : Integer.parseInt((String)RequestMap.get("page"));
        int limit = StringUtils.isEmpty(RequestMap.get("limit")) ? 10 : Integer.parseInt((String)RequestMap.get("limit"));

        ScheduleQueryVo scheduleQuery = scheduleQuery(RequestMap);
        Page<Schedule> pageModel = scheduleService.selectPage(page , limit, scheduleQuery);
        return Result.ok(pageModel);
    }


    @ApiOperation(value = "删除排班")
    @PostMapping("schedule/remove")
    public Result removeSchedule(HttpServletRequest request) {

        Map<String, Object> RequestMap = RequestMap(request);
        ScheduleQueryVo scheduleQuery = scheduleQuery(RequestMap);
        //获取参数数据
        scheduleService.remove((String) RequestMap.get("hoscode"), (String)RequestMap.get("scheduleId"));
        return Result.ok();
    }


//+++++++++++++++++++++++
    /**
     * 将请求的数据与数据库中做校验的统一方法
     * @param request
     * @return
     */
    public Map<String, Object> RequestMap(HttpServletRequest request) {

        //转换请求对象类型
        Map<String, Object> switchtRequestMap = HttpRequestHelper.switchMap(request.getParameterMap());
        String hoscode = (String) switchtRequestMap.get("hoscode");
        //  判断请hoscode为空
        if(StringUtils.isEmpty(hoscode)) {
            throw new bookException(ResultCodeEnum.PARAM_ERROR);
        }
        //查询数据表的sign_key字段，并加密
        String sign = (String) switchtRequestMap.get("sign");
        String signKey = hospitalSetService.getSignKey(hoscode);
        String signKeyByMD5 = DigestUtils.md5Hex(signKey);
//        String signKeyByMD5 = HttpRequestHelper.getSign(switchtRequestMap, signKey);
        //  判断请求的数据和数据库数据的一致
        if (!sign.equals(signKeyByMD5)) {
            throw new bookException(ResultCodeEnum.SIGN_ERROR);
        }
        return switchtRequestMap;
    }

    /**
     * 判断部门对象是否在数据库中
     * @param RequestMap
     * @return
     */
    public DepartmentQueryVo departmentQuery( Map<String, Object> RequestMap){

        String depcode = (String)RequestMap.get("depcode");          //非必要获取
        DepartmentQueryVo departmentQueryVo = new DepartmentQueryVo();
        departmentQueryVo.setHoscode((String) RequestMap.get("hoscode"));
        departmentQueryVo.setDepcode(depcode);
        return departmentQueryVo;
    }

    /**
     *判断时间表对象是否在数据库中
     * @param RequestMap
     * @return
     */
    public ScheduleQueryVo scheduleQuery(Map<String, Object> RequestMap){

        String depcode = (String)RequestMap.get("depcode");     //非必要获取
        ScheduleQueryVo scheduleQueryVo = new ScheduleQueryVo();
        scheduleQueryVo.setHoscode((String) RequestMap.get("hoscode"));
        scheduleQueryVo.setDepcode(depcode);
        return scheduleQueryVo;
    }



}
