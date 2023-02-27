package com.offcn.sellergoods.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true,securedEnabled = true)
@EnableResourceServer//把当前应用作为资源服务器启用
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    //定义公钥名称
    private static final String PUBLIC_KEY = "public.key";

    //定义一个方法，读取公钥内容
    private String getPublicKey(){
        //使用类资源加载器，读取公钥
        ClassPathResource resource = new ClassPathResource(PUBLIC_KEY);
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(resource.getInputStream());
            //把输入字符流包装成带缓冲区字符流
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String s = "";//定义公钥读取到内容
            //调用带缓冲区字符流读取公钥内容
            s = bufferedReader.readLine();
            //判断是否为空
            if(s != null){
                System.out.println("读取到公钥内容："+ s);
                return s;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    //声明jwt令牌解析转换器对象
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter(){
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        //获取公钥，关联到jwt令牌解析转换器对象
        jwtAccessTokenConverter.setVerifier(new RsaVerifier(getPublicKey()));
        return  jwtAccessTokenConverter;
    }

    //声明令牌存储对象
    @Bean
    public TokenStore tokenStore(){
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    //设置哪些资源需要保护，哪些允许放行
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/user/")//允许放行地址
                .permitAll()
                .anyRequest()
                .authenticated();//除了上面两条，其他地址全部资源保护
    }
}
