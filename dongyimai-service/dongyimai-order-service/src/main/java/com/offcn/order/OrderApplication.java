package com.offcn.order;

import com.offcn.order.filter.FeignInterceptor;
import com.offcn.utils.IdWorker;
import com.offcn.utils.TokenDecode;
import feign.Logger;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients(basePackages = {"com.offcn.sellergoods.feign","com.offcn.user.feign"})
@MapperScan(basePackages = {"com.offcn.order.dao"})
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }

    //声明TokenDecode工具
    @Bean
    public TokenDecode tokenDecode() {
        return new TokenDecode();
    }

    //声明feign日志级别
    @Bean
    public Logger.Level level(){
        return Logger.Level.FULL;
    }

//    声明feign拦截器
    @Bean
    public FeignInterceptor feignInterceptor(){
        return new FeignInterceptor();
    }

    @Bean
    public IdWorker idWorker(){
        return new IdWorker(1,1);//机器编号，机房编号
    }
}
