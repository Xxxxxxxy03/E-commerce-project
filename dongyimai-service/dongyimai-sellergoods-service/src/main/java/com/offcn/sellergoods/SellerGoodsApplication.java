package com.offcn.sellergoods;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@SpringBootApplication
@EnableDiscoveryClient
@MapperScan(basePackages = {"com.offcn.sellergoods.dao"})
@EnableSwagger2//允许使用swagger文档
public class SellerGoodsApplication {
    public static void main(String[] args) {
        SpringApplication.run(SellerGoodsApplication.class, args);
    }


}
