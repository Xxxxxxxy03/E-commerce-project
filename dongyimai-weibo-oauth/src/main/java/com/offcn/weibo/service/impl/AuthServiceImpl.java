package com.offcn.weibo.service.impl;

import com.offcn.weibo.service.AuthService;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
@Service
public class AuthServiceImpl implements AuthService {
    @Override
    public String getAccessToken(String url) {
        //创建用户端对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        //创建post对象
        HttpPost post = new HttpPost(url);

        //设置请求头
        post.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 SAFARI/537.36");

        try {
            //调用客户端发出请求
            CloseableHttpResponse response = httpClient.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();
            //判断响应状态码
            if(statusCode == 200){
            //    获取=响应体
                HttpEntity entity = response.getEntity();

            //    判断响应体是否为空
                if( entity != null){
                    //转换响应体为字符串
                    String s = EntityUtils.toString(entity,"UTF-8");
                    return s;
                }
            }else{
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getUserInfo(String url) {
       //创建客户端对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //创建post对象
        HttpPost post = new HttpPost(url);
    //    设置请求头对象
        post.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");

        try {
    //    客户端发出请求
            CloseableHttpResponse response = httpClient.execute(post);

            int statusCode = response.getStatusLine().getStatusCode();
            //判断响应状态码
            if(statusCode == 200){
            //    获取响应体
                HttpEntity entity = response.getEntity();

            //    判断响应体是否为空
                if(entity == null){
                //    转换响应体为字符串
                    String s = EntityUtils.toString(entity, "UTF-8");
                    return s;
                }
            }else{
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  null;
    }
}
