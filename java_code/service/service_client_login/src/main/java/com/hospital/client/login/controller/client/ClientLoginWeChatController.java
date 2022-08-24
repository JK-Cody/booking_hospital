package com.hospital.client.login.controller.client;

import com.alibaba.fastjson.JSONObject;
import com.hospital.client.login.service.ClientLoginService;
import com.hospital.client.login.utils.ConstantPropertiesForWeChatUtils;
import com.hospital.client.login.utils.HttpClientUtils;
import com.hospital.common.exception.bookException;
import com.hospital.common.result.Result;
import com.alibaba.excel.util.StringUtils;
import com.hospital.common.result.ResultCodeEnum;
import com.hospital.common.utils.JwtToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import model.user.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "用户微信登录窗口")
@Slf4j
@Controller
@RequestMapping("/api/client/wechat")
public class ClientLoginWeChatController {

    @Autowired
    private ClientLoginService clientLoginService;

    @Autowired
    private RedisTemplate redisTemplate;


    @ApiOperation(value = "微信扫码登录")
    @GetMapping("getLoginParam")
    @ResponseBody
    public Result genQrConnect(HttpSession session) {

        try {
//            获取回调地址
            String redirectUri = URLEncoder.encode(ConstantPropertiesForWeChatUtils.WX_OPEN_REDIRECT_URL, "UTF-8");
            Map<String, Object> map = new HashMap<>();
//            获取app_id
            map.put("appid", ConstantPropertiesForWeChatUtils.WX_OPEN_APP_ID);
            map.put("redirectUri", redirectUri);
            map.put("scope", "snsapi_login");
            map.put("state", System.currentTimeMillis()+"");//System.currentTimeMillis()+""
            return Result.ok(map);
        }catch (UnsupportedEncodingException e){
                e.printStackTrace();
            return null;
        }
    }


    @ApiOperation(value = "微信扫码登录后，补充回调手机登录")
    @RequestMapping("callback")
    public String callback(String code, String state) {

        //输出授权临时票据
        System.out.println("微信授权服务器回调。。。。。。");
        System.out.println("state = " + state);
        System.out.println("code = " + code);

        if (StringUtils.isEmpty(state) || StringUtils.isEmpty(code)) {
            log.error("非法回调请求");
            throw new bookException(ResultCodeEnum.ILLEGAL_CALLBACK_REQUEST_ERROR);
        }

        //获取access_token的请求
        StringBuffer baseAccessTokenUrl = new StringBuffer()
                .append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&code=%s")
                .append("&grant_type=authorization_code");
        //添加code和微信配置信息
        String accessTokenUrl = String.format(baseAccessTokenUrl.toString(),
                ConstantPropertiesForWeChatUtils.WX_OPEN_APP_ID,
                ConstantPropertiesForWeChatUtils.WX_OPEN_APP_SECRET,
                code);
        //发送access_token的请求
        String accessTokenString = null;
        try {
            accessTokenString = HttpClientUtils.get(accessTokenUrl);
        } catch (Exception e) {
            throw new bookException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
        }
        System.out.println("access_token结果 = " + accessTokenString);

        //获取access_token的登录人信息，需要access_token和openid
        JSONObject accessTokenStringJson = JSONObject.parseObject(accessTokenString);
        if(accessTokenStringJson.getString("errcode") != null){
            log.error("获取access_token失败：" + accessTokenStringJson.getString("errcode") + accessTokenStringJson.getString("errmsg"));
            throw new bookException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
        }
        String accessToken= accessTokenStringJson.getString("access_token");
        String openId = accessTokenStringJson.getString("openid");
        //进行数据库查询
         UserInfo userInfo = clientLoginService.getUserByOpenid(openId);
        // 如果没有查到用户信息,那么调用微信个人信息获取的接口
        //如果查询到个人信息，那么直接进行登录
         if(null == userInfo){
             //获取access_token的登录人的请求
             String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                     "?access_token=%s" +
                     "&openid=%s";
             String userInfoUrl = String.format(baseUserInfoUrl, accessToken, openId);
             //发送access_token的请求
             String UserInfo = null;
             try {
                 UserInfo = HttpClientUtils.get(userInfoUrl);
             } catch (Exception e) {
                 throw new bookException(ResultCodeEnum.FETCH_USERINFO_ERROR);
             }
             System.out.println("access_token用户信息的结果 = " + UserInfo);
             JSONObject UserInfoJson = JSONObject.parseObject(UserInfo);
             if(UserInfoJson.getString("errcode") != null){
                 log.error("获取用户信息失败：" + UserInfoJson.getString("errcode") + UserInfoJson.getString("errmsg"));
                 throw new bookException(ResultCodeEnum.FETCH_USERINFO_ERROR);
             }
             //获取用户名和头像
             String nickname = UserInfoJson.getString("nickname");
             String headimgurl = UserInfoJson.getString("headimgurl");
             //保存用户信息
             userInfo = new UserInfo();  //此前判断为空
             userInfo.setOpenid(openId);
             userInfo.setNickName(nickname);
             userInfo.setStatus(1);
             clientLoginService.save(userInfo);
         }
        log.info(accessToken);
        log.info(openId);

        //根据已有信息显示用户名
        Map<String, Object> map = new HashMap<>();
        String name = userInfo.getName();
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getPhone();
        }
        map.put("name", name);
        if(StringUtils.isEmpty(userInfo.getPhone())) {
            map.put("openid", userInfo.getOpenid());
        } else {
            map.put("openid", "");
        }
//生成Token签名保存
        String token = JwtToken.createToken(userInfo.getId(), name);
        map.put("token", token);

       //登录后跳转到weixin/callback页面
        return "redirect:" + ConstantPropertiesForWeChatUtils.CLIENTWEB_BASE_URL
                + "/weixin/callback?token="+map.get("token")+"&openid="+map.get("openid")+"&name="+URLEncoder.encode((String)map.get("name"));
    }

}

