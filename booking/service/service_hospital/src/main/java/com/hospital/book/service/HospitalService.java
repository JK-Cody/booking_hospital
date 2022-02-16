package com.hospital.book.service;

import model.hosp.Hospital;
import org.springframework.data.domain.Page;
import vo.hosp.HospitalQueryVo;
import vo.hosp.HospitalSetQueryVo;

import java.util.List;
import java.util.Map;

public interface HospitalService {

    //保存医院对象
    void save(Map<String, Object> parameterMap);

    //通过Hoscode查询医院
    Hospital getByHoscode(String hoscode);

    //查询医院词典列表
    Page<Hospital> selectHospitalPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo);

    //更新医院状态
    void updateHospital(String id, Integer status);

    //获取医院名
    List<Hospital> getHospitalByHosname(String hosname);

    //获取医院所有信息
    Map<String, Object> getHospitalDetail(String id);

    //获取医院所有信息
    Map<String, Object> getHospitalDetailByHoscode(String hoscode);

    //获取医院名称
    String getHospitalName(String hoscode);

}
