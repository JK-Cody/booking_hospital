package com.hospital.client.login.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 手机登录的请求参数
 */
@Component
public class ConstantPropertiesForMsmUtils implements InitializingBean {

    //无连接阿里云，且与注解@EnableFeignClients冲突
//    @Value("${aliyun.sms.regionId}")
    @Value("default")
    private String regionId;

//    @Value("${aliyun.sms.accessKeyId}")
    @Value("LTAI5t8f5sGZZxziCPHfRaU4")
    private String accessKeyId;

//    @Value("${aliyun.sms.secret}")
    @Value("0C75j2OEyLvmP0LAyBIpAcoBxVnrKA")
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
