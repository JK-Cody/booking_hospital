package com.hospital.client.login.service;

import vo.msm.MsmVo;

import java.util.Map;

public interface MsmService {

    //发送手机验证码
    boolean send(String phone, String code);

    //发送手机验证码
    boolean send(String phone, Map<String,Object> mapParam);

    //用于rabbitMQ的手机验证码确认发送
    boolean sendMsmVo(MsmVo msmVo);

}
