package com.hospital.feign.client.login;

import model.user.Patient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "service-client-login")
@Repository
public interface LoginFeignClient {

    //调用模块service_client_login,获取就诊人信息
    @GetMapping("/api/client/patient/inner/getPatientById/{id}")
    Patient getPatientById(@PathVariable("id") Long id);
}
