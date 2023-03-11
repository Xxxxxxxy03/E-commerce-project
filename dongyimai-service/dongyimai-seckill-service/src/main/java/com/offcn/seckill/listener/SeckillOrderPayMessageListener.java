package com.offcn.seckill.listener;

import com.alibaba.fastjson.JSON;
import com.offcn.seckill.service.SeckillOrderService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RabbitListener(queues = "${mq.pay.queue.seckillorder}")
public class SeckillOrderPayMessageListener {

    //注入秒杀订单fwu
    @Autowired
    private SeckillOrderService seckillOrderService;

    //监听接收消息方法
    @RabbitHandler
    public void consumeMessage(@Payload String message){

        //消息是json字符串，转换成map
        Map<String, String> map = JSON.parseObject(message, Map.class);
        System.out.println("接收到消息:"+map);
        //收到支付成功消息，调用更新秒杀订单方法
        seckillOrderService.updatePayStatus(map.get("out_trade_no"),map.get("trade_no"),map.get("body"));

    }
}
