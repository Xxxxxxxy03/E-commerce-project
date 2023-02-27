package com.offcn.service.impl;

import com.offcn.bean.AuthToken;
import com.offcn.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    //注入支持负载均衡的服务实例注册发现工具对象
    @Autowired
    private LoadBalancerClient loadBalancerClient;

    //发出http请求调用
    @Autowired
    private RestTemplate restTemplate;

    //授权认证方法

    /**
     * @param username     springSecurity 账号
     * @param password     springSecurity 密码
     * @param clientId     客户端账号
     * @param clientSecret 客户端密码
     * @return
     */
    @Override
    public AuthToken login(String username, String password, String clientId, String clientSecret) {
        AuthToken authToken = applyToken(username, password, clientId, clientSecret);
        if (authToken == null) {
            System.out.println("获取令牌失败");
        }
        return authToken;
    }

    //定义一个方法：查找认证授权服务器，发出获取令牌请求
    private AuthToken applyToken(String username, String password, String clientId, String clientSecret) {

        //使用负载均衡服务实例查询工具获取到服务实例对象
        // ServiceInstance serviceInstance = loadBalancerClient.choose("user-auth");

        //获取服务实例得的url地址
        // String path = serviceInstance.getUri() + "/oauth/token";

        String path="http://localhost:9100"+"/oauth/token";
        //创建一个集合封装请求头数据
        MultiValueMap<String, String> heads = new LinkedMultiValueMap<>();
        //在请求头集合封装basic auth验证信息
        heads.add("Authorization", httpBasic(clientId, clientSecret));

        //创建一个集合封装请求体信息
        LinkedMultiValueMap<String, String> bodys = new LinkedMultiValueMap<>();
        //封装参数一：授权类型 password
        bodys.add("grant_type", "password");
        //封装参数2：spring Security账号
        bodys.add("username", username);
        //封装参数3：spring Security密码
        bodys.add("password", password);

        //调用restTemplate
        ResponseEntity<Map> response = restTemplate.exchange(path, HttpMethod.POST, new HttpEntity<>(bodys, heads), Map.class);

        //判断响应状态
        boolean ok = response.getStatusCode().is2xxSuccessful();
        if (ok) {
            //获取响应数据
            Map map = response.getBody();
            //判断map是否为空,map里面是否包含access_token refresh_token jti
            if (map == null || map.get("access_token") == null || map.get("refresh_token") == null || map.get("jti") == null) {
                throw new RuntimeException("获取令牌失败");
            }
            //把获取到相关信息封装返回对象
            AuthToken authToken = new AuthToken();
            authToken.setAccessToken(map.get("access_token").toString());
            authToken.setRefreshToken(map.get("refresh_token").toString());
            authToken.setJti(map.get("jti").toString());
            return authToken;
        } else {
            System.out.println("获取令牌请求失败");
        }
        return null;
    }

    //定义准备basic auth验证value方法
    private String httpBasic(String clientId, String clientSecret) {
        String temp = clientId + ":" + clientSecret;
        byte[] encode = Base64.getEncoder().encode(temp.getBytes());
        //要把base64后账号密码串在最前面拼接一个Basic+半角空格 + base64(clientId+clientSecret)
        return "Basic " + new String(encode);
    }
}
