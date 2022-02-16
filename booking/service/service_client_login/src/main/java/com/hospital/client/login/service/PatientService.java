package com.hospital.client.login.service;

import com.baomidou.mybatisplus.extension.service.IService;
import model.user.Patient;

import java.util.List;

public interface PatientService extends IService<Patient> {

    //获取就诊人列表
    List<Patient> getPatientListById(Long userId);

    //根据id获取就诊人信息
    Patient getPatientById(Long id);

}
