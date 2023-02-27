package com.offcn.seckill.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class AsyncConfig {
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        //设置线程核心线程数量
        threadPoolTaskExecutor.setCorePoolSize(10);
        //设置最大线程数
        threadPoolTaskExecutor.setMaxPoolSize(100);
        //设置队伍大小  无界队伍
        threadPoolTaskExecutor.setQueueCapacity(Integer.MAX_VALUE);

        //设置空闲线程最大空闲时间  单位是秒
        threadPoolTaskExecutor.setKeepAliveSeconds(60);

        //设置线程名前缀+分组名称
        threadPoolTaskExecutor.setThreadNamePrefix("JAVA_Thread-");
        threadPoolTaskExecutor.setThreadGroupName("myGroup");

        //所有任务结束后关闭线程池
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);

        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());

        threadPoolTaskExecutor.initialize();

        return threadPoolTaskExecutor;
    }
}
