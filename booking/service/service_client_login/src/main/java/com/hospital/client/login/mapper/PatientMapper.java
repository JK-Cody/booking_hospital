package com.hospital.client.login.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import model.user.Patient;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PatientMapper extends BaseMapper<Patient> {
}

