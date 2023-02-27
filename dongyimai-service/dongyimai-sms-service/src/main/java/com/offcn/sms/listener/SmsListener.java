package com.offcn.sms.listener;

import com.offcn.sms.utils.SmsUtil;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SmsListener {

    @Autowired
    private SmsUtil smsUtil;

    @RabbitListener(queues = "dongyimai.sms.queue")
    public void getMessage(Map<String,String> map){
        if(map == null){
            return;
        }

        String mobile = map.get("mobile");
        String code = map.get("code");
        String s = smsUtil.smsSend(mobile, code);

        //判断响应字符串里是否包含5个0
        if(s != null && s.indexOf("00000") != -1){
            System.out.println("发送短信成功！");
        }else{
            System.out.println("发送短信失败！");
        }
    }
}
