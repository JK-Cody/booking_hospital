package com.hospital.book.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import model.user.Patient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import vo.hosp.ScheduleOrderVo;
import vo.order.OrderCountQueryVo;
import vo.order.SignInfoVo;

import java.util.Map;

//分页插件
@Configuration
public class PageConfig {

    @Bean
    public PaginationInterceptor paginationInterceptor(){
        return new PaginationInterceptor();
    }
}
