package com.hospital.client.oss.service;

import org.springframework.web.multipart.MultipartFile;

public interface UserUploadFileService {

    //文件上传到阿里云oss
    String upload(MultipartFile file);
}
