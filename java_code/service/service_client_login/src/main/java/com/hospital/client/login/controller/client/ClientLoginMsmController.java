package com.hospital.client.login.controller.client;

import com.alibaba.excel.util.StringUtils;
import com.hospital.client.login.service.MsmService;
import com.hospital.client.login.utils.RandomUtil;
import com.hospital.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@Api(tags = "用户手机登录验证窗口")
@RestController
@RequestMapping("/api/client/msm")
public class ClientLoginMsmController {

    @Autowired
    private MsmService msmService;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    //发送手机验证码
    @ApiOperation(value = "手机验证码处理")
    @GetMapping("send/{phone}")
    public Result sendCode(@PathVariable String phone) {
        //从redis获取验证码，如果获取获取到，返回ok
        String code = redisTemplate.opsForValue().get(phone);      // key 手机号  value 验证码
        if(!StringUtils.isEmpty(code)) {
            System.out.println("获取到验证码"+code);
            return Result.ok();
        }

        //如果从redis获取不到，生成验证码，
        code = RandomUtil.getSixBitRandom();
        //调用service方法，通过整合短信服务进行发送
//      boolean isSend = msmService.send(phone,code);
        boolean isSend = true;    //测试

        //生成验证码放到redis里面，设置有效时间
        if(isSend) {
            redisTemplate.opsForValue().set(phone,code,2, TimeUnit.MINUTES);
            return Result.ok();
        } else {
            return Result.fail().message("发送短信失败");
        }
    }

}
