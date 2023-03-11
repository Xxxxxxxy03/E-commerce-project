package com.offcn.pay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class PaySeckillApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaySeckillApplication.class,args);
    }
}
