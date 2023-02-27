package com.offcn.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

 @RequestMapping("/demo1")
    public String demo1(){
        return "demo1";
    }

    @RequestMapping("/demo2")
    public String demo2(){
        return "demo2";
    }

    @RequestMapping("/demo3")
    public String demo3(){
        return "demo3";
    }
}
