package com.hospital.client.login.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 微信登录的请求参数
 */
@Component
public class ConstantPropertiesForWeChatUtils implements InitializingBean {

//    @Value("${wx.open.app_id}")
    private String appId;

//    @Value("${wx.open.app_secret}")
    private String appSecret;

//    @Value("${wx.open.redirect_url}")
    @Value("http://localhost:8203/api/client/wechat/callback")
    private String redirectUrl;

//    @Value("${clientWeb.baseUrl}")
    @Value("http://localhost:3000")
    private String clientWebBaseUrl;

    public static String WX_OPEN_APP_ID;
    public static String WX_OPEN_APP_SECRET;
    public static String WX_OPEN_REDIRECT_URL;

    public static String CLIENTWEB_BASE_URL;

    @Override
    public void afterPropertiesSet() throws Exception {
        WX_OPEN_APP_ID = appId;
        WX_OPEN_APP_SECRET = appSecret;
        WX_OPEN_REDIRECT_URL = redirectUrl;
        CLIENTWEB_BASE_URL = clientWebBaseUrl;
    }

}
