package com.hospital.client.login.controller.client;

import com.hospital.client.login.service.ClientLoginService;
import com.hospital.common.Config.utils.AuthContextHolder;
import com.hospital.common.result.Result;
import com.hospital.common.utils.GetIPAddress;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import model.user.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vo.user.LoginVo;
import vo.user.UserAuthVo;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Api(tags = "用户登录窗口")
@RestController
@RequestMapping("/api/client")
public class ClientLoginController {

    @Autowired
    private ClientLoginService clientLoginService;

    @ApiOperation(value = "用户手机登录")
    @PostMapping("login")
    public Result login(@RequestBody LoginVo loginVo, HttpServletRequest request) {

        loginVo.setIp(GetIPAddress.getIpAddr(request));
        Map<String, Object> info = clientLoginService.login(loginVo);
        return Result.ok(info);
    }


    @ApiOperation(value = "获取用户提交的身份认证信息")
    @PostMapping("auth/userAuth")
    public Result userAuthentication(
            @RequestBody UserAuthVo userAuthVo,   //userAuthVo为用户提交的身份认证信息集
            HttpServletRequest request) {

        //从Token总获取用户ID
        Long userId = AuthContextHolder.getUserId(request);
        //获取用户提交的身份认证信息
        clientLoginService.getUserIntoAuthentication(userId,userAuthVo);
        return Result.ok();
    }


    @ApiOperation(value = "获取用户信息")
    @GetMapping("auth/getUserInfo")
    public Result getUserInfo(HttpServletRequest request) {

        //从Token中获取用户ID
        Long userId = AuthContextHolder.getUserId(request);
        //获取用户信息
        UserInfo userInfo = clientLoginService.getById(userId);
        return Result.ok(userInfo);
    }

}
