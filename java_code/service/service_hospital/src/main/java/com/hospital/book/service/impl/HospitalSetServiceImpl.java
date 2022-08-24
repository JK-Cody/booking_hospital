package com.hospital.book.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hospital.book.mapper.HospitalSetMapper;
import com.hospital.book.service.HospitalSetService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.common.exception.bookException;
import com.hospital.common.result.ResultCodeEnum;
import model.hosp.HospitalSet;
import org.springframework.stereotype.Service;
import vo.order.SignInfoVo;

@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper,HospitalSet> implements HospitalSetService {


    @Override
    public String getSignKey(String hoscode) {
        //查询医院的sign_key字段
        HospitalSet hospitalSet = this.getByHoscode(hoscode);
        if(null == hospitalSet) {
            throw new bookException(ResultCodeEnum.HOSPITAL_OPEN);
        }
        if(hospitalSet.getStatus() == 1) {
            throw new bookException(ResultCodeEnum.HOSPITAL_LOCK);
        }
        return hospitalSet.getSignKey();
    }

    @Override
    public SignInfoVo getSignInformation(String hoscode) {

        HospitalSet hospitalSet = this.getByHoscode(hoscode);
        if(null == hospitalSet) {
            throw new bookException(ResultCodeEnum.HOSPITAL_OPEN);
        }
        SignInfoVo signInfoVo = new SignInfoVo();
        signInfoVo.setApiUrl(hospitalSet.getApiUrl());
        signInfoVo.setSignKey(hospitalSet.getSignKey());
        return signInfoVo;
    }

//++++++++++
    public HospitalSet getByHoscode(String hoscode) {
        QueryWrapper<HospitalSet> hospitalSetQueryWrapper = new QueryWrapper<>();
        //数据库的数据的is_deleted不能为1
        return baseMapper.selectOne(hospitalSetQueryWrapper.eq("hoscode",hoscode));

    }
}

