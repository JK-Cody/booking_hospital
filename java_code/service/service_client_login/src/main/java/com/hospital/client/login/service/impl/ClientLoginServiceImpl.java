package com.hospital.client.login.service.impl;

import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.client.login.mapper.ClientLoginMapper;
import com.hospital.client.login.service.PatientService;
import com.hospital.client.login.mapper.UserLoginRecordMapper;
import com.hospital.client.login.service.ClientLoginService;
import com.hospital.common.exception.bookException;
import com.hospital.common.result.ResultCodeEnum;
import com.hospital.common.utils.JwtToken;
import enums.AuthStatusEnum;
import model.user.Patient;
import model.user.UserInfo;
import model.user.UserLoginRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import vo.user.LoginVo;
import vo.user.UserAuthVo;
import vo.user.UserInfoQueryVo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClientLoginServiceImpl extends
        ServiceImpl<ClientLoginMapper, UserInfo> implements ClientLoginService{

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private UserLoginRecordMapper userLoginRecordMapper;

    @Autowired
    private PatientService patientService;


    @Override
    public Map<String, Object> login(LoginVo loginVo) {

//获取填写的信息
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();
        //校验信息完整度
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)) {
            throw new bookException(ResultCodeEnum.PARAM_ERROR);
        }
//从redis中获取验证码来校验
        //模拟登录键值对 13123456789 123456
        String mobleCode = redisTemplate.opsForValue().get(phone);
        if(!code.equals(mobleCode)) {
            throw new bookException(ResultCodeEnum.CODE_ERROR);
        }
        //如果是微信登录则有Openid，此时已有用户数据
        UserInfo userInfo=null;
        if(!StringUtils.isEmpty(loginVo.getOpenid())) {
            userInfo = this.getUserByOpenid(loginVo.getOpenid());
            if(null != userInfo) {
                userInfo.setPhone(loginVo.getPhone());  //绑定手机号
                this.updateById(userInfo);
            } else {
                throw new bookException(ResultCodeEnum.DATA_ERROR);
            }
        }
        //如果没有用户数据，则开放注册
            if(null == userInfo) {
                QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("phone", phone);
                userInfo = baseMapper.selectOne(queryWrapper);
                if (null == userInfo) {   //首次
                    // 添加信息进数据库
                    userInfo = new UserInfo();
                    userInfo.setName("");
                    userInfo.setPhone(phone);
                    userInfo.setStatus(1);
                    baseMapper.insert(userInfo);
                }
            }
        //校验是否被禁用
            if(userInfo.getStatus() == 0) {
            throw new bookException(ResultCodeEnum.LOGIN_DISABLED_ERROR);
        }
//保存登录记录
        UserLoginRecord userLoginRecord = new UserLoginRecord();
        userLoginRecord.setUserId(userInfo.getId());
        userLoginRecord.setIp(loginVo.getIp());
        userLoginRecordMapper.insert(userLoginRecord);
//返回一个用户名称到页面
        Map<String, Object> map = new HashMap<>();

        String name = userInfo.getName();
            if(StringUtils.isEmpty(name)) {
            //无名字但存在nickname
            name = userInfo.getNickName();
        }
             //无nickname,使用手机号代替
            if(StringUtils.isEmpty(name)) {
            name = userInfo.getPhone();
        }
        map.put("name", name);
//生成Token签名保存
        String token = JwtToken.createToken(userInfo.getId(), name);
        map.put("token", token);
            return map;
        }


    @Override
    public Map<String, Object> getUserAndPatientList(Long userId) {

        Map<String,Object> map = new HashMap<>();
        //附加状态信息到用户对象,有益于前端页面获取
        UserInfo userInfo = this.packageUserInfo(baseMapper.selectById(userId));
        //保存用户对象
        map.put("userInfo",userInfo);
        //保存用户管理的就诊人信息
        List<Patient> patientList = patientService.getPatientListById(userId);
        map.put("patientList",patientList);
        return map;
    }


    @Override
    public UserInfo getUserByOpenid(String openId) {

        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        //查询微信登录openId
        queryWrapper.eq("openId", openId);
        UserInfo userInfo = baseMapper.selectOne(queryWrapper);
        return userInfo;
    }


    @Override
    public IPage<UserInfo> selectPage(Page<UserInfo> pageParam, UserInfoQueryVo userInfoQueryVo) {

//从用户信息对象获取条件值,并进行非空判断
        String name = userInfoQueryVo.getKeyword(); //用户名称
        Integer status = userInfoQueryVo.getStatus();//用户状态
        Integer authStatus = userInfoQueryVo.getAuthStatus(); //认证状态
        String createTimeBegin = userInfoQueryVo.getCreateTimeBegin(); //开始时间
        String createTimeEnd = userInfoQueryVo.getCreateTimeEnd(); //结束时间
        //对条件值进行非空判断
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(name)) {
            wrapper.like("name",name);
        }
        if(!StringUtils.isEmpty(status)) {
            wrapper.eq("status",status);
        }
        if(!StringUtils.isEmpty(authStatus)) {
            wrapper.eq("auth_status",authStatus);
        }
        if(!StringUtils.isEmpty(createTimeBegin)) {
            wrapper.ge("create_time",createTimeBegin);
        }
        if(!StringUtils.isEmpty(createTimeEnd)) {
            wrapper.le("create_time",createTimeEnd);
        }
//将所有用户按照上述条件获取方式处理，并分页
        IPage<UserInfo> pages = baseMapper.selectPage(pageParam, wrapper);
//附加状态信息到用户对象,有益于前端页面获取
        pages.getRecords().stream().forEach(item -> {
            this.packageUserInfo(item);
        });
        return pages;
    }


    @Override
    public void getUserIntoAuthentication(Long userId, UserAuthVo userAuthVo) {

        //根据用户id查询用户信息
        UserInfo userInfo = baseMapper.selectById(userId);
        //用户属性转为认证人用户属性
        userInfo.setName(userAuthVo.getName());
        userInfo.setCertificatesType(userAuthVo.getCertificatesType());
        userInfo.setCertificatesNo(userAuthVo.getCertificatesNo());
        userInfo.setCertificatesUrl(userAuthVo.getCertificatesUrl());
        userInfo.setAuthStatus(AuthStatusEnum.AUTH_RUN.getStatus());  //设置状态为1，即认证中
        //进行信息更新
        baseMapper.updateById(userInfo);
    }


    @Override
    public void setUserAuthentication(Long userId, Integer authStatus) {

        //前端通过点击改变用户认证状态时响应
        if(authStatus ==2 || authStatus ==-1) {   //通过为 2，不通过为 -1
            UserInfo userInfo = baseMapper.selectById(userId);
            //保存认证状态
            userInfo.setAuthStatus(authStatus);
            //进行信息更新
            baseMapper.updateById(userInfo);
        }
    }


    @Override
    public void lockUser(Long userId, Integer status) {

        if(status == 0 || status == 1) {
            UserInfo userInfo = this.getById(userId);
            userInfo.setStatus(status);
            this.updateById(userInfo);
        }
    }


    /**
     * 附加状态信息到用户对象,有益于前端页面获取
     * @param userInfo
     * @return
     */
    private UserInfo packageUserInfo(UserInfo userInfo) {

        //保存认证状态
        userInfo.getParam().put("authStatusString",AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus()));
        //保存用户状态
        String statusString = userInfo.getStatus() ==0 ?"锁定" : "正常";
        userInfo.getParam().put("statusString",statusString);
        return userInfo;
    }

}

