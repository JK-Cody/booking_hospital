package com.hospital.client.oss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)   //取消数据源自动配置
@ComponentScan(basePackages = {"com.hospital"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.hospital")
public class ClientOssApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClientOssApplication.class, args);
    }
}
