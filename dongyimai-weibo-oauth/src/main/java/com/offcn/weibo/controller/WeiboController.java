package com.offcn.weibo.controller;

import com.alibaba.fastjson.JSON;
import com.offcn.weibo.bean.WeiboUser;
import com.offcn.weibo.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;



    @Slf4j
    @Controller
    public class WeiboController {

       @Autowired
       private AuthService authService;

        @Value("${auth.clientId}")
        private String client_id;

        @Value("${auth.clientSecret}")
        private String client_secret;


        @Value("${auth.redirectUri}")
        private String redirect_uri;


        @RequestMapping("/")
        public String login() {
            return "login";
        }

        //获取微博认证服务器返回的授权码，向服务器发出请求换取令牌
        @RequestMapping("/weibo/success")
        public String authorize(String code, HttpSession session) {
            String url = "https://api.weibo.com/oauth2/access_token";

            url += "?client_id=" + client_id;

            url += "&client_secret=" + client_secret;

            url += "&grant_type=authorization_code";

            url += "&redirect_uri=" + redirect_uri;

            url += "&code=" + code;

            //发出换取令牌的请求
            String jsonStr = authService.getAccessToken(url);

            //判断响应内容是否为空
            if (jsonStr == null) {
                //获取令牌失败，跳转到失败页面
                return "fail";
            } else {


                //解析转换为实体封装类
                WeiboUser user = JSON.parseObject(jsonStr, WeiboUser.class);

                //获取令牌
                String access_token = user.getAccess_token();
                String isRealName = user.getIsRealName();
                String uid = user.getUid();
                long expires_in = user.getExpires_in();

                System.out.println("access_token:" + access_token);
                System.out.println("isRealName:" + isRealName);
                System.out.println("uid:" + uid);
                System.out.println("expires_in:" + expires_in);

                //获取用户信息
                this.getUserInfo(access_token, uid);

                return "home";
            }

        }

        //获取用户信息
        private String getUserInfo(String access_token, String uid) {
            String url = "https://api.weibo.com/2/users/show.json";

            url += "?access_token=" + access_token;

            url += "&uid=" + uid;

            String userInfoJsonStr = authService.getUserInfo(url);

            System.out.println("获取用户信息:" + userInfoJsonStr);

            return userInfoJsonStr;

        }
    }


