package com.offcn.controller;

import com.offcn.bean.AuthToken;
import com.offcn.entity.Result;
import com.offcn.entity.StatusCode;
import com.offcn.service.AuthService;
import com.offcn.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/user")
public class AuthController {

    @Qualifier("authServiceImpl")
    @Autowired
    private AuthService authService;

    @Value("${auth.clientId}")
    private String clientId;

    //秘钥
    @Value("${auth.clientSecret}")
    private String clientSecret;

    //Cookie存储的域名
    @Value("${auth.cookieDomain}")
    private String cookieDomain;

    //Cookie生命周期
    @Value("${auth.cookieMaxAge}")
    private int cookieMaxAge;

    //登录请求方法
    @PostMapping("/login")
    public Result login(String username, String password, HttpServletResponse response) {

        //判断用户名或密码是否为空
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return new Result(false, StatusCode.LOGINERROR, "账号或密码为空");
        }

        //调用服务发出获取令牌请求
        AuthToken authToken = authService.login(username, password, clientId, clientSecret);

        //把令牌存储到cookie
        CookieUtil.addCookie(response, cookieDomain, "/", "Authorization", authToken.getAccessToken(), cookieMaxAge, true);

        return new Result(true, StatusCode.OK, "登录成功！");
    }
}
