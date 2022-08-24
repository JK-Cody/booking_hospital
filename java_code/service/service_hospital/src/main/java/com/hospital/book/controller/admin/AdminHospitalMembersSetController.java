package com.hospital.book.controller.admin;

import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.book.service.HospitalSetService;
import com.hospital.common.exception.bookException;
import com.hospital.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import model.hosp.HospitalSet;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vo.hosp.HospitalSetQueryVo;

import java.util.List;
import java.util.Random;

@Api(tags = "后台提供所有医院成员数据的CRUD方法——医院的数据")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
//使用网关配置后不启用
//@CrossOrigin
public class AdminHospitalMembersSetController {

    @Autowired
    private HospitalSetService hospitalSetService;

    //1 查询医院设置表所有信息
    @ApiOperation(value = "列表获取所有医院")
    @GetMapping("findAll")
    public Result findAllHospitalSet() {
        //调用service的方法
        List<HospitalSet> list = hospitalSetService.list();
        return Result.ok(list);
    }

    //2 根据id获取医院设置
    @ApiOperation(value = "根据id获取医院")
    @GetMapping("getHospitalSet/{id}")
    public Result getHospitalSet(@PathVariable Long id) {

        //调用自定义异常响应自定义状态码
        HospitalSet hospitalSet = null;
        try {
            hospitalSet = hospitalSetService.getById(id);
        } catch (Exception e) {
            throw new bookException("状态码",111);
        }
        return Result.ok(hospitalSet);
    }

    //3 条件查询——带分页
    @ApiOperation(value = "条件查询")
    @PostMapping("findPageHospitalSet/{current}/{limit}")
    public Result findPageHospitalSet(@PathVariable long current,    //当前页
                                  @PathVariable long limit,      //每页额定数量
                                  @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo) {
        //创建page对象，传递当前页，每页记录数
        Page<HospitalSet> page = new Page<>(current, limit);
//        查询条件
        QueryWrapper<HospitalSet> hospitalSetQueryWrapper = new QueryWrapper<>();
//        获取已有医院名和代号
        String hosname = hospitalSetQueryVo.getHosname();
        String hoscode = hospitalSetQueryVo.getHoscode();

        if(!StringUtils.isEmpty(hosname)) {
            hospitalSetQueryWrapper.like("hosname", hosname);  //字段模糊查询
        }
        if(!StringUtils.isEmpty(hoscode)) {
            hospitalSetQueryWrapper.like("hoscode", hoscode);
        }
        Page<HospitalSet> hospitalSetPage = hospitalSetService.page(page, hospitalSetQueryWrapper);
        return Result.ok(hospitalSetPage);
    }

    //4 添加列表项目
    @ApiOperation(value = "添加医院")
    @PostMapping("saveHospitalSet")
    public Result saveHospitalSet(@RequestBody HospitalSet hospitalSet) {

        //设置状态0使用，1不能使用
        hospitalSet.setStatus(0);
        //签名秘钥
        Random random = new Random();
        hospitalSet.setSignKey(DigestUtils.md5Hex(System.currentTimeMillis()+""+random.nextInt(1000)));
        //调用service保存
        boolean save = hospitalSetService.save(hospitalSet);
        if(save){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    //5 逻辑删除医院设置
    @ApiOperation(value = "逻辑删除医院设置")
    @DeleteMapping("deleteHospitalSet/{id}")
    public Result deleteHospitalSet(@PathVariable Long id) {
        boolean flag = hospitalSetService.removeById(id);
        if (flag) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    //6 批量删除医院设置
    @ApiOperation(value = "批量删除医院")
    @DeleteMapping("batchDeleteHospitalSet")
    public Result batchDeleteHospitalSet(@RequestBody List<Long> idList) {
        hospitalSetService.removeByIds(idList);
        return Result.ok();
    }

    //7 修改医院设置
    @ApiOperation(value = "修改医院")
    @PostMapping("updateHospitalSet")
    public Result updateHospitalSet(@RequestBody HospitalSet hospitalSet) {
        boolean flag = hospitalSetService.updateById(hospitalSet);
        if(flag) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    //8 列表项目锁定和解锁
    @ApiOperation(value = "锁定医院和解锁")
    @PutMapping("lockHospitalSet/{id}/{status}")
    public Result lockHospitalSet(@PathVariable long id,
                                  @PathVariable Integer status) {
        //根据id查询医院设置信息
        HospitalSet lockById = hospitalSetService.getById(id);
        //设置状态
        lockById.setStatus(status);
        //调用修改方法
        hospitalSetService.updateById(lockById);
        return Result.ok();
    }

    //9 发送签名秘钥
    @ApiOperation(value = "发送签名秘钥")
    @PutMapping("sendKey/{id}")
    public Result lockHospitalSet(@PathVariable Long id) {
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        String signKey = hospitalSet.getSignKey();
        String hoscode = hospitalSet.getHoscode();
        return Result.ok();
    }

}
