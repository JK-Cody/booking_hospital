package com.hospital.dictionary.controller;

import com.hospital.common.result.Result;
import com.hospital.dictionary.service.dictionaryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import model.cmn.Dict;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(tags= "所有医院成员的背景资料总目录的查询接口")
@RestController
@RequestMapping("/admin/cmn/dict")
//使用网关配置后不启用
//@CrossOrigin
public class AdminDictionaryController {

    @Autowired
    private dictionaryService dictionaryService;

    @PostMapping("importData")
    public Result importData(MultipartFile file){   //必须为file才能匹配到element-ui的组件格式
        dictionaryService.importExcelData(file);
        return Result.ok();
    }

    @GetMapping("exportData")
    public void exportData(HttpServletResponse response){
        dictionaryService.exportExcelData(response);
    }

    //http://localhost:8202/admin/dict/dictionaryService/findChildData/{id}
    @ApiOperation(value = "根据数据id查询子数据列表")
    @GetMapping("findChildData/{id}")
    public Result findChildData(@PathVariable Long id) {
        List<Dict> chlidData = dictionaryService.findChlidData(id);
        return Result.ok(chlidData);
    }

    @ApiOperation(value = "根据数据dictCode查询子数据列表")
    @GetMapping("findChildDataByDictCode/{dictCode}")
    public Result findChildDataByDictCode(@PathVariable String dictCode) {
        List<Dict> chlidData = dictionaryService.findChlidDataByDictCode(dictCode);
        return Result.ok(chlidData);
    }

    @ApiOperation(value = "根据dictCode和value查询项目")
    @GetMapping("getName/{dictCode}/{value}")
    public String getName(@PathVariable String dictCode,
                          @PathVariable String value) {
        return dictionaryService.getNameByDictCodeAndValue(dictCode, value);
    }

    @ApiOperation(value = "根据value查询项目")
    @GetMapping("getName/{value}")
    public String getName(@PathVariable String value) {
        return dictionaryService.getNameByDictCodeAndValue("",value);
    }

}
