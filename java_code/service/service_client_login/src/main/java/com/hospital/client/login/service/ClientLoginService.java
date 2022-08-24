package com.hospital.client.login.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import model.user.UserInfo;
import vo.user.LoginVo;
import vo.user.UserAuthVo;
import vo.user.UserInfoQueryVo;

import java.util.Map;

public interface ClientLoginService extends IService<UserInfo> {

    //用户登录
    Map<String, Object> login(LoginVo loginVo);

    //获取用户信息以及就诊人信息
    Map<String, Object> getUserAndPatientList(Long userId);

    //根据微信Openid查询用户信息
    UserInfo getUserByOpenid(String openId);

    //用户转为认证中用户
    void getUserIntoAuthentication(Long userId, UserAuthVo userAuthVo);

    //响应和保存用户认证状态
    void setUserAuthentication(Long userId, Integer authStatus);

    //获取用户列表（条件查询带分页）
    IPage<UserInfo> selectPage(Page<UserInfo> pageParam, UserInfoQueryVo userInfoQueryVo);

    //用户锁定
    void lockUser(Long userId, Integer status);

}
