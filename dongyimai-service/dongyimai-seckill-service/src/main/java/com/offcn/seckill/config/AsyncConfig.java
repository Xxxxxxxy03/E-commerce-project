package com.offcn.seckill.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class AsyncConfig {
    @Bean(name="myThread001")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        //设置参数一:线程核心线程数量
        threadPoolTaskExecutor.setCorePoolSize(10);
        //设置参数二:最大线程数
        threadPoolTaskExecutor.setMaxPoolSize(100);

        //设置参数3:空闲线程最大空闲时间  单位是秒
        threadPoolTaskExecutor.setKeepAliveSeconds(60);
        //设置参数4:缓冲区队列长度  无界队列
        threadPoolTaskExecutor.setQueueCapacity(Integer.MAX_VALUE);



        //设置线程池的线程名字
        threadPoolTaskExecutor.setThreadNamePrefix("ujiuye-Thread-");
        threadPoolTaskExecutor.setThreadGroupName("group1");//线程池所在组名称

        //所有任务结束后关闭线程池
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);

        //策略1：丢弃任务，抛异常
        // threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());

        //策略2：丢弃任务，不抛异常
        // threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());

        //策略3：丢弃最前面任务
        // threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());

        //策略4：充实添加当前任务，自动重复调用，直到成功
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        //线程池初始化
        threadPoolTaskExecutor.initialize();

        return threadPoolTaskExecutor;
    }
}
