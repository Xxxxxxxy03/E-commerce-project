package com.offcn.seckill.listener;

import com.alibaba.fastjson.JSON;
import com.offcn.entity.Result;
import com.offcn.seckill.entity.SeckillStatus;
import com.offcn.seckill.feign.PayFeign;
import com.offcn.seckill.pojo.SeckillOrder;
import com.offcn.seckill.service.SeckillOrderService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RabbitListener(queues = "${mq.pay.queue.seckillordertimer}")
public class SeckillOrderDelayMessageListener {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SeckillOrderService seckillOrderService;

    @Autowired
    private PayFeign payFeign;

    //接收消息方法
    @RabbitHandler
    public void comsumerMessage(@Payload String msg) {
        //消息是一个json，转换成SeckillStatus
        SeckillStatus seckillStatus = JSON.parseObject(msg, SeckillStatus.class);
        //获取用户名
        String username = seckillStatus.getUsername();
        //根据用户名去redis获取对应秒杀订单
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.boundHashOps("SeckillOrder").get(username);

        //判断是否为空
        if (seckillOrder != null) {

            //关闭支付
            Result<Map<String, String>> result = payFeign.closePay(seckillStatus.getOrderId());
            if (result != null && result.isFlag()) {
                Map<String, String> data = result.getData();
                //获取响应状态码
                if (data != null && data.get("code").equals("10000")) {
                    //关闭订单，回滚库存
                    seckillOrderService.closeOrder(username);
                }
            }

        }
    }
}
