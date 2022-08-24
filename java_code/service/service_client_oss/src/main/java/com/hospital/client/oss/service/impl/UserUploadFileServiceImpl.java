package com.hospital.client.oss.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.hospital.client.oss.utils.ConstantPropertiesForAliCloudOss;
import com.hospital.client.oss.service.UserUploadFileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class UserUploadFileServiceImpl implements UserUploadFileService {

    @Override
    public String upload(MultipartFile file) {

        String endpoint = ConstantPropertiesForAliCloudOss.EDNPOINT;
        String accessKeyId = ConstantPropertiesForAliCloudOss.ACCESS_KEY_ID;
        String accessKeySecret = ConstantPropertiesForAliCloudOss.SECRECT;
        String bucketName = ConstantPropertiesForAliCloudOss.BUCKET;
        try {
            // 创建OSSClient实例。
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            // 上传文件流
            InputStream inputStream = file.getInputStream();
            // 获取文件名称
            String fileName = file.getOriginalFilename();

//            //生成随机唯一值，使用uuid，添加到文件名称里面
//            String uuid = UUID.randomUUID().toString().replaceAll("-","");
//            fileName = uuid+fileName;
//            //按照当前日期，创建文件夹，上传到创建文件夹里面
//            //  2021/02/02/01.jpg
//            String timeUrl = new DateTime().toString("yyyy/MM/dd");
//            fileName = timeUrl+"/"+fileName;

            //调用方法实现上传
            ossClient.putObject(bucketName, fileName, inputStream);
            // 关闭OSSClient。
            ossClient.shutdown();
            //上传之后文件路径,一般为固定格式 https://hospital-login.oss-cn-hongkong.aliyuncs.com/???
            String url = "https://"+bucketName+"."+endpoint+"/"+fileName;

            //返回文件路径
            return url;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
