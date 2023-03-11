package com.offcn.seckill.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class QueueConfig {

    //注入一个读取配置文件工具对象
    @Autowired
    private Environment env;

    //声明一个秒杀队列
    @Bean(name = "seckillQueue")
    public Queue seckillQueue(){
        return new Queue(env.getProperty("mq.pay.queue.seckillorder"),true);

    }

    //声明交换机 路由模式交换机
    @Bean(name = "seckillExchanage")
    public DirectExchange seckillExchange(){
        return new DirectExchange(env.getProperty("mq.pay.exchange.seckillorder"));
    }

    //绑定队列到交换机，指定路由key
    @Bean(name = "SeckillBinding")
    public Binding seckillBinding(){
        return BindingBuilder.bind(seckillQueue()).to(seckillExchange()).with(env.getProperty("mq.pay.routing.seckillkey"));
    }
}
