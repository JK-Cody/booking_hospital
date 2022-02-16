package com.hospital.client.login.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.client.login.service.ClientLoginService;
import com.hospital.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import model.user.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vo.user.UserInfoQueryVo;

import java.util.Map;

@Api(tags = "所有用户数据的审核接口")
@RestController
@RequestMapping("/admin/client")
public class AdminClientUserController {

    @Autowired
    private ClientLoginService clientLoginService;

    @ApiOperation(value = "用户列表（条件查询带分页）")
    @GetMapping("{page}/{limit}")
    public Result userList(@PathVariable Long page,
                       @PathVariable Long limit,
                       UserInfoQueryVo userInfoQueryVo) {
        Page<UserInfo> pageParam = new Page<>(page,limit);
        IPage<UserInfo> pageModel = clientLoginService.selectPage(pageParam,userInfoQueryVo);
        return Result.ok(pageModel);
    }

    @ApiOperation(value = "锁定用户")
    @GetMapping("lockUser/{userId}/{status}")
    public Result userLockUser(
            @PathVariable("userId") Long userId,
            @PathVariable("status") Integer status){
        clientLoginService.lockUser(userId, status);
        return Result.ok();
    }

    @ApiOperation(value = "用户详情")
    @GetMapping("showUser/{userId}")
    public Result showUserDetail(@PathVariable Long userId) {
        Map<String,Object> map = clientLoginService.getUserAndPatientList(userId);
        return Result.ok(map);
    }

    @ApiOperation(value = "确认用户认证审批状态")
    @GetMapping("authUser/{userId}/{authStatus}")
    public Result setUserAuthenticationStatus(@PathVariable Long userId,
                           @PathVariable Integer authStatus) {
        clientLoginService.setUserAuthentication(userId,authStatus);
        return Result.ok();
    }

}
