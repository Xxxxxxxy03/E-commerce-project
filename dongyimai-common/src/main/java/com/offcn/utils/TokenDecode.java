package com.offcn.utils;

import com.alibaba.fastjson.JSON;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class TokenDecode {

    //公钥
    private static final String PUBLIC_KEY = "public.key";

    //    定义变量，记录公钥内容
    private static String publickey = null;

    //定义读取公钥方法
    private String getPublicKey() {
        ClassPathResource resource = new ClassPathResource(PUBLIC_KEY);
        try {
            //使用输入字符流
            InputStreamReader inputStreamReader = new InputStreamReader(resource.getInputStream());
            //支持缓冲区字符流
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            if ((publickey = bufferedReader.readLine()) != null) {
                System.out.println("公钥内容：" + publickey);
                return publickey;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //    解析令牌方法
    public Map<String, String> decodeToken(String token) {
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(getPublicKey()));
        String claims = jwt.getClaims();
        //    把载荷数据从json字符串转换为map
        Map map = JSON.parseObject(claims, Map.class);
        return map;
    }

    //    获取当前springSecurity环境获取上下文对象，获取用户认证携带的令牌
    public Map<String, String> getUserINfo() {
        OAuth2AuthenticationDetails oAuth2AuthenticationDetails = (OAuth2AuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String token = oAuth2AuthenticationDetails.getTokenValue();
        //    调用解析校验令牌方法
        Map<String, String> map = decodeToken(token);
        return map;
    }
}
