package com.offcn.sms.controller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
@RestController
public class SmsController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RequestMapping("/smssend")
    public void sendSms(){
        HashMap<String, String> map = new HashMap<>();
        map.put("mobile","15630157906");
        map.put("code","520521");
        rabbitTemplate.convertAndSend("dongyimai.sms.queue",map);
    }
}
