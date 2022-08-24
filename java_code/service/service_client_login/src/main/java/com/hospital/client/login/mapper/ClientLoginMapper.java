package com.hospital.client.login.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import model.user.UserInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ClientLoginMapper extends BaseMapper<UserInfo> {
}
