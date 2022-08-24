package com.hospital.client.login.service.impl;

import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.hospital.client.login.service.MsmService;
import com.hospital.client.login.utils.ConstantPropertiesForMsmUtils;
import org.springframework.stereotype.Service;
import vo.msm.MsmVo;

import java.util.HashMap;
import java.util.Map;

@Service
public class MsmServiceImpl implements MsmService {

    @Override
    public boolean send(String phone, String code) {
        //判断手机号是否为空
        if(StringUtils.isEmpty(phone)) {
            return false;
        }
        //获取阿里云配置参数
        DefaultProfile profile = DefaultProfile.
                getProfile(ConstantPropertiesForMsmUtils.REGION_Id,
                        ConstantPropertiesForMsmUtils.ACCESS_KEY_ID,
                        ConstantPropertiesForMsmUtils.SECRECT);
        //封装为通信客户端
        IAcsClient client = new DefaultAcsClient(profile);
        //获取请求
        CommonRequest request = this.phoneRequest(phone);
        //保存code到请求
        Map<String,Object> codeMap = new HashMap<>();
        codeMap.put("code",code);
        //转为json格式   如{"code":"123456"}
        request.putQueryParameter("TemplateParam", JSONObject.toJSONString(codeMap));

        //调用方法进行短信发送,在阿里云个人端将收到验证码
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
            return response.getHttpResponse().isSuccess();   //    成功就返回true
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean send(String phone, Map<String,Object>mapParam) {

        //判断手机号是否为空
        if(StringUtils.isEmpty(phone)) {
            return false;
        }
        //获取阿里云配置参数
        DefaultProfile profile = DefaultProfile.
                getProfile(ConstantPropertiesForMsmUtils.REGION_Id,
                        ConstantPropertiesForMsmUtils.ACCESS_KEY_ID,
                        ConstantPropertiesForMsmUtils.SECRECT);
        //封装为通信客户端
        IAcsClient client = new DefaultAcsClient(profile);
        //获取请求
        CommonRequest request = this.phoneRequest(phone);
        //保存mapParam到请求
        //转为json格式   如{"code":"123456"}
        request.putQueryParameter("TemplateParam", JSONObject.toJSONString(mapParam));

        //调用方法进行短信发送,在阿里云个人端将收到验证码
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
            return response.getHttpResponse().isSuccess();   //    成功就返回true
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean sendMsmVo(MsmVo msmVo) {

        //获取短信对象msmVo的信息
        String phone = msmVo.getPhone();
        if(!StringUtils.isEmpty(phone)) {
            boolean msmVoParam = this.send(phone,msmVo.getParam());
            return msmVoParam;
        }
        return false;
    }


    //请求的模板信息
    private CommonRequest phoneRequest(String phone){

        CommonRequest request = new CommonRequest();
        //request.setProtocol(ProtocolType.HTTPS);  //HTTPS类型
        request.setMethod(MethodType.POST);    //POST类型
        //附加版本信息
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");   //日期
        request.setAction("SendSms");

        //保存需发送的阿里短信模板信息
        request.putQueryParameter("PhoneNumbers", phone);   //手机号
        request.putQueryParameter("SignName", "在线网站");         //短信服务签名名称
        request.putQueryParameter("TemplateCode", "SMS_180051135");   //短信服务模板code
        return request;
    }
}

