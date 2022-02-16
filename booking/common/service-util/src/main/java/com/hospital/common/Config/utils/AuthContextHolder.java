package com.hospital.common.Config.utils;

import com.hospital.common.utils.JwtToken;
import javax.servlet.http.HttpServletRequest;

/**
 * Token信息的获取
 */
public class AuthContextHolder {
    //获取当前用户id
    public static Long getUserId(HttpServletRequest request) {
        //从header获取token
        String token = request.getHeader("token");
        //jwt从token获取userid
        Long userId = JwtToken.getUserId(token);
        return userId;
    }

    //获取当前用户名称
    public static String getUserName(HttpServletRequest request) {
        //从header获取token
        String token = request.getHeader("token");
        //jwt从token获取userid
        String userName = JwtToken.getUserName(token);
        return userName;
    }
}

