package com.offcn.weibo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class WeiboOauthApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeiboOauthApplication.class,args);
    }
}
