package com.hospital.dictionary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages="com.hospital")
@EnableDiscoveryClient
public class ServiceDictionaryApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceDictionaryApplication.class, args);
    }
}

