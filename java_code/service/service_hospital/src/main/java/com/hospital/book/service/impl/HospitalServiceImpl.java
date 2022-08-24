package com.hospital.book.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hospital.feign.client.dictionary.DictionaryFeignClient;
import com.hospital.book.repository.HospitalRepository;
import com.hospital.book.service.HospitalService;
import model.hosp.Hospital;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import vo.hosp.HospitalQueryVo;

import java.util.*;

@Service
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DictionaryFeignClient dictionaryFeignClient;

    private Hospital setHospitalHosType(Hospital hospital) {

        //查找dict_code为Hostype的字段
        String hosTypeName = dictionaryFeignClient.getName("Hostype", hospital.getHostype());
        //查询各级字典
        String provinceCode = dictionaryFeignClient.getName(hospital.getProvinceCode());
        String cityCode = dictionaryFeignClient.getName(hospital.getCityCode());
        String districtCode = dictionaryFeignClient.getName(hospital.getDistrictCode());
        //使用Param保存各级字典
        hospital.getParam().put("fullAddress", provinceCode + cityCode + districtCode);
        hospital.getParam().put("hosTypeName", hosTypeName);
        return hospital;
    }

    @Override
    public void save(Map<String, Object> parameterMap) {

        //将参数转为Hospital对象
        String mapString = JSONObject.toJSONString(parameterMap);
        Hospital hospital = JSONObject.parseObject(mapString, Hospital.class);
        //提取对象
        String hoscode = hospital.getHoscode();
        //获取Mongo数据库的医院对象
        Hospital HospitalByHoscode = hospitalRepository.getHospitalByHoscode(hoscode);
        //判断对象是否存在
        if (null != HospitalByHoscode) {   //对象更新
            hospital.setStatus(HospitalByHoscode.getStatus());
            hospital.setCreateTime(HospitalByHoscode.getCreateTime());
            hospital.setUpdateTime(new Date()); //赋予新日期
            hospital.setIsDeleted(0);
            //保存到Mongo数据库
            hospitalRepository.save(hospital);

        } else {
            hospital.setStatus(0); //创建对象
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }
    }

    @Override
    public Hospital getByHoscode(String hoscode) {

        //获取Mongo数据库的医院对象
        return hospitalRepository.getHospitalByHoscode(hoscode);
    }

    @Override
    public String getHospitalName(String hoscode) {

        //获取Mongo数据库的医院对象
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        if (null != hospital) {
            return hospital.getHosname();
        }
        return "";
    }

    @Override
    public List<Hospital> getHospitalByHosname(String hosname) {

        //获取Mongo数据库的医院列表
        return hospitalRepository.findHospitalByHosnameLike(hosname);
    }

    @Override
    public Page<Hospital> selectHospitalPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {

        //设定分页规则
        Pageable pageable = PageRequest.of(page - 1, limit);
        //创建匹配器
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        //将hospitalQueryVo转为hospital
        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo, hospital);
        //创建匹配器对象
        Example<Hospital> example = Example.of(hospital, matcher);
        //获取Mongo数据库的医院列表
        Page<Hospital> HospitalPage = hospitalRepository.findAll(example, pageable);
        //循环得到mongdb的数据,获取下级字典
        HospitalPage.getContent().stream().forEach(item -> {
            this.setHospitalHosType(item);
        });
        return HospitalPage;
    }

    @Override
    public void updateHospital(String id, Integer status) {

        if (status == 0 || status == 1) {
            //获取Mongo数据库的医院对象
            Hospital hospital = hospitalRepository.findById(id).orElse(null);
            hospital.setStatus(status);
            hospital.setUpdateTime(new Date());
            hospitalRepository.save(hospital);
        }
    }

    @Override
    public Map<String, Object> getHospitalDetail(String id) {

        Map<String, Object> mapDetail = new HashMap<>();
        //获取Mongo数据库的医院对象
        Hospital hospital = this.setHospitalHosType(hospitalRepository.findById(id).get());
        mapDetail.put("hospital", hospital);
        //存放一个预约规则
        mapDetail.put("bookingRule", hospital.getBookingRule());
        //设置规则为空
        hospital.setBookingRule(null);
        return mapDetail;
    }

    @Override
    public Map<String, Object> getHospitalDetailByHoscode(String hoscode) {

        Map<String, Object> result = new HashMap<>();
        //医院详情
        Hospital hospital = this.setHospitalHosType(this.getByHoscode(hoscode));
        result.put("hospital", hospital);
        //预约规则
        result.put("bookingRule", hospital.getBookingRule());
        //不需要重复返回
        hospital.setBookingRule(null);
        return result;
    }


}
