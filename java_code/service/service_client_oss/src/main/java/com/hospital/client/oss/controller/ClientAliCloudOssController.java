package com.hospital.client.oss.controller;

import com.hospital.client.oss.service.UserUploadFileService;
import com.hospital.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = "用户上传文件的客户端接口")
@RestController
@RequestMapping("/api/oss/file")
public class ClientAliCloudOssController {

    @Autowired
    private UserUploadFileService userUploadFileService;

    @ApiOperation(value = "上传文件")
    @PostMapping("fileUpload")
    public Result fileUpload(MultipartFile file) {
        //获取上传文件
        String url = userUploadFileService.upload(file);
        return Result.ok(url);
    }

}
