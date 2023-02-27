package com.offcn.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@Controller
@RequestMapping("/oauth")
public class LoginRedirect {

//    跳转到登录模板视图
    @RequestMapping("/login")
    public String login(@RequestParam(value = "FROM",required = false) String from, Model model){

        //把接收过来的原问地址，做解码处理
        String urlSrc = "";
        try {
            if(StringUtils.isEmpty(from)){
             urlSrc = URLDecoder.decode(from, "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //原始访问地址封装到model
        model.addAttribute("from",urlSrc);

        return "login";
    }
}
