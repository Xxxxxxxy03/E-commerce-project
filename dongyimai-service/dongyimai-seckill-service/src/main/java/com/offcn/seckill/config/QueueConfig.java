package com.offcn.seckill.config;

import org.springframework.amqp.core.*;
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

    /**
     * 到期数据队列
     * @return
     */
    @Bean
    public Queue seckillOrderTimerQueue() {
        return new Queue(env.getProperty("mq.pay.queue.seckillordertimer"), true);
    }

    /**
     * 超时数据队列
     * @return
     */
    @Bean
    public Queue delaySeckillOrderTimerQueue() {
        return QueueBuilder.durable(env.getProperty("mq.pay.queue.seckillordertimerdelay"))
                .withArgument("x-dead-letter-exchange", env.getProperty("mq.pay.exchange.seckillordertimer"))        // 消息超时进入死信队列，绑定死信队列交换机
                .withArgument("x-dead-letter-routing-key", env.getProperty("mq.pay.routing.seckillordertimerkey"))   // 绑定指定的routing-key
                .build();
    }

    //创建延时交换机
    @Bean
    public DirectExchange directExchangeOrderTimer(){
        return new DirectExchange(env.getProperty("mq.pay.exchange.seckillordertimer"));
    }


    /***
     * 交换机与队列绑定
     * @param
     * @param
     * @return
     */
    @Bean
    public Binding basicBindingOrderTime() {
        return BindingBuilder.bind(seckillOrderTimerQueue())
                .to(directExchangeOrderTimer())
                .with(env.getProperty("mq.pay.routing.seckillordertimerkey"));
    }
}
