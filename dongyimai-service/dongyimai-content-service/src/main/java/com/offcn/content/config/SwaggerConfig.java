package com.offcn.content.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    //创建文档属性
    private ApiInfo getApiInfo(){
        return new ApiInfoBuilder().title("广告微服务接口文档")
                .description("官方文档")
                .version("1.0")
                .contact(new Contact("孙老师","http://www.ujiuye.com","xy@126.com"))
                .build();
    }

    //声明swagger文档配置
    @Bean
    public Docket docket(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(getApiInfo())
                .groupName("组1")
                .select()
                .build();
    }
}
