package com.offcn;

import com.offcn.bean.AuthToken;
import com.offcn.service.AuthService;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
public class AuthServiceImplTest implements AuthService {

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

    public AuthToken login(String username, String password, String clientId, String clientSecret)  {
        AuthToken authToken = applyToken(username, password, clientId, clientSecret);
        if (authToken == null) {
            System.out.println("获取令牌失败");
        }

        //定义字符串
        String s = "";
        //定义map存储数据
        HashMap<String, String> map = new HashMap<>();
        map.put("access_token",authToken.getAccessToken());
        map.put("refresh_token",authToken.getRefreshToken());
        map.put("jti",authToken.getJti());


        List<Map.Entry<String, String>> itmes = new ArrayList<Map.Entry<String, String>>(map.entrySet());

        //对所有传入的参数按照字段名从小到大排序
        //Collections.sort(items); 默认正序
        //可通过实现Comparator接口的compare方法来完成自定义排序
        Collections.sort(itmes, new Comparator<Map.Entry<String, String>>() {
            @Override
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                return (o1.getKey().toString().compareTo(o2.getKey()));
            }
        });

        //键值对的形式
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> item : itmes) {
            if (StringUtils.isNotBlank(item.getKey())) {
                String key = item.getKey();
                String val = item.getValue();
                try {
                    val = URLEncoder.encode(val, "UTF-8");
                    //拼接
                    sb.append(key).append("=").append(val).append("&");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        //赋值给s
        s = sb.toString();
        if (!s.isEmpty()) {
            //接去掉最后一个&
            s = s.substring(0, s.lastIndexOf("&"));
        }

        //计算md5值
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        //	调用MD5算法，即返回16个byte类型的值
        byte[] byteArray = md5.digest(s.getBytes());
        //转换为小写
        String s1 = byteArray.toString().toLowerCase();

        return authToken;
    }

    //定义一个方法：查找认证授权服务器，发出获取令牌请求
    private AuthToken applyToken(String username, String password, String clientId, String clientSecret) {

        //使用负载均衡服务实例查询工具获取到服务实例对象
        ServiceInstance serviceInstance = loadBalancerClient.choose("user-auth");

        //获取服务实例得的url地址
        String path = serviceInstance.getUri() + "/oauth/token";

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

