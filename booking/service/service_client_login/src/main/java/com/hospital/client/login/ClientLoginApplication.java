package com.hospital.client.login;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.hospital"})
@EnableDiscoveryClient
//冲突
@EnableFeignClients(basePackages = "com.hospital")
public class ClientLoginApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClientLoginApplication.class, args);
    }
}
