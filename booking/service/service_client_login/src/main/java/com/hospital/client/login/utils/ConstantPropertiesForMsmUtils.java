package com.hospital.client.login.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 手机登录的请求参数
 */
@Component
public class ConstantPropertiesForMsmUtils implements InitializingBean {

//    @Value("${aliyun.sms.regionId}")
    private String regionId;

//    @Value("${aliyun.sms.accessKeyId}")
    private String accessKeyId;

//    @Value("${aliyun.sms.secret}")
    private String secret;

    public static String REGION_Id;
    public static String ACCESS_KEY_ID;
    public static String SECRECT;

//    取得参数赋值
    @Override
    public void afterPropertiesSet() throws Exception {
        REGION_Id=regionId;
        ACCESS_KEY_ID=accessKeyId;
        SECRECT=secret;
    }
}
