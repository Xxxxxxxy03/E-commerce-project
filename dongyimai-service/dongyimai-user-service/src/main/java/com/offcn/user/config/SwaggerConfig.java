package com.offcn.user.config;

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

    //设置文档基本属性
    private ApiInfo createApiInfo(){
       return  new ApiInfoBuilder().title("用户操作文档")
                .description("调用错误，一律别找我！")
                .termsOfServiceUrl("http://ujiuye.com")
                .version("1.0")
                .license("开源文档")
                .licenseUrl("http://www.ujiuye.com")
                .contact(new Contact("李老师","http://lilaoshi.com","xy@126.com"))
                .build();
    }
    //创建Swagger文档配置对象
    @Bean
    public Docket docket(){
       return new Docket(DocumentationType.SWAGGER_2).apiInfo(createApiInfo())//设置文档属性
                .groupName("组1")
                .select()//选择哪些代码包生成文档
                .build();
    }
}
