package com.hospital.client.login.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import model.user.UserInfo;
import model.user.UserLoginRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserLoginRecordMapper extends BaseMapper<UserLoginRecord> {
}
