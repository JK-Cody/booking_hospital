package com.hospital.book.service;

import com.baomidou.mybatisplus.extension.service.IService;
import model.hosp.HospitalSet;
import vo.order.SignInfoVo;

public interface HospitalSetService extends IService<HospitalSet> {

    // 查询数据表的sign_key字段
    String getSignKey(String hoscode);

    //获取医院签名信息
    SignInfoVo getSignInformation(String hoscode);
}
