package com.offcn.sms.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;

public class SimpleQueueConfig {
    private final String simpleQueue = "dongyimai.sms.queue";

    @Bean
    public Queue simpleQueue(){
        return new Queue(simpleQueue,true);
    }
}
