package com.offcn;

import feign.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.offcn.feign"})
public class SearchWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchWebApplication.class,args);
    }

    //声明Feign日志级别对象
    @Bean
    public Logger.Level level(){
        return Logger.Level.FULL;
    }
}
