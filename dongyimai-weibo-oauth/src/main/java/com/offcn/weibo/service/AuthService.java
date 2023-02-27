package com.offcn.weibo.service;

public interface AuthService {

    //使用授权码换取令牌
    String getAccessToken(String url);

    //获取用户信息
    String getUserInfo(String url);
}
