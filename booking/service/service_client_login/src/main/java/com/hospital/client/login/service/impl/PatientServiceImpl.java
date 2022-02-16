package com.hospital.client.login.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.feign.client.dictionary.DictionaryFeignClient;
import com.hospital.client.login.mapper.PatientMapper;
import com.hospital.client.login.service.PatientService;
import enums.DictEnum;
import model.user.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class PatientServiceImpl  extends ServiceImpl<PatientMapper, Patient> implements PatientService {

    @Autowired
    public DictionaryFeignClient dictionaryFeignClient;

    //获取就诊人列表
    @Override
    public List<Patient> getPatientListById(Long userId) {

        QueryWrapper<Patient> wrapper = new QueryWrapper<>();
        //根据userid查询所有就诊人信息列表
        wrapper.eq("user_id",userId);
        List<Patient> patientList = baseMapper.selectList(wrapper);

        patientList.forEach(item -> {
            //调用Feign模块，查询就诊人的背景数据字典内容
            this.packPatient(item);
        });
        return patientList;
    }

    //获取单个就诊人信息
    @Override
    public Patient getPatientById(Long id) {

        //调用Feign模块，查询就诊人的背景数据字典内容
        return this.packPatient(baseMapper.selectById(id));
    }



//+++++++++++工具方法

    /**
     * 调用Feign模块，查询就诊人的背景数据字典内容
     * @param patient
     * @return
     */
    private Patient packPatient(Patient patient) {

        //获取证件类型的值,CertificatesType需要为数字10或20
        String certificatesTypeString =
                dictionaryFeignClient.getName(DictEnum.CERTIFICATES_TYPE.getDictCode(), patient.getCertificatesType());//联系人证件
        //获取联系人证件类型的值
        String contactsCertificatesTypeString =
                dictionaryFeignClient.getName(DictEnum.CERTIFICATES_TYPE.getDictCode(),patient.getContactsCertificatesType());
        //获取省的值
        String provinceString = dictionaryFeignClient.getName(patient.getProvinceCode());
        //获取市的值
        String cityString = dictionaryFeignClient.getName(patient.getCityCode());
        //获取区的值
        String districtString = dictionaryFeignClient.getName(patient.getDistrictCode());

        //存放到map
        patient.getParam().put("certificatesTypeString", certificatesTypeString);
        patient.getParam().put("contactsCertificatesTypeString", contactsCertificatesTypeString);
        patient.getParam().put("provinceString", provinceString);
        patient.getParam().put("cityString", cityString);
        patient.getParam().put("districtString", districtString);
        String fullAddress=provinceString + cityString + districtString + patient.getAddress();
        patient.getParam().put("fullAddress", fullAddress);

        return patient;
    }

}
