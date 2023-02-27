package com.offcn;

import feign.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.bootstrap.encrypt.KeyProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.offcn.user.feign"})
public class UserOauth2Application {
    public static void main(String[] args) {
        SpringApplication.run(UserOauth2Application.class,args);
    }

    //声明密钥读取工具对象
    @Bean(name="keyProp")
    public KeyProperties keyProperties(){
        return new KeyProperties();
    }

    //声明RestTemplate对象
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @Bean
    public Logger.Level level(){
        return Logger.Level.FULL;
    }
}
