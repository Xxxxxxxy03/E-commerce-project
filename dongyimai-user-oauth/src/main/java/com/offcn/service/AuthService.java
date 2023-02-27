package com.offcn.service;

import com.offcn.bean.AuthToken;

public interface AuthService {

    //授权认证方法
    /**
     *
     * @param username springSecurity 账号
     * @param password springSecurity 密码
     * @param clientId  客户端账号
     * @param clientSecret  客户端密码
     * @return
     */
    AuthToken login(String username,String password,String clientId,String clientSecret);



}
