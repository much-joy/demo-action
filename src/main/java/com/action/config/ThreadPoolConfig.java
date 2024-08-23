package com.action.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolConfig {

    @Bean(name = "customThreadPoolExecutor")
    public Executor customThreadPoolExecutor() {
        int corePoolSize = 4; // 核心线程数
        int maxPoolSize = 8; // 最大线程数
        long keepAliveTime = 60L; // 空闲线程的存活时间
        int queueCapacity = 100; // 任务队列的大小

        // 创建自定义的 ThreadPoolExecutor
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueCapacity),
                new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略
        );

        return executor;
    }
}
