package com.offcn.user.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        paginationInnerInterceptor.setDbType(DbType.MYSQL);
        //设置请求的页面大于最后页操作，true调回到首页，false继续请求，默认为false
        paginationInnerInterceptor.setOverflow(true);
        //设置最大单页限制数量，默认500条， -1 不受限制
        paginationInnerInterceptor.setMaxLimit(500l);
        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        return interceptor;
    }
    }


