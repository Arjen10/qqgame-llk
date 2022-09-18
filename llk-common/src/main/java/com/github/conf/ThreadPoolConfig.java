package com.github.conf;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Arjen10
 * @date 2022/9/16 14:51
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ThreadPoolConfig {

    private final ThreadPoolProperties poolProperties;

    @Bean
    public ThreadPoolTaskScheduler scheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(poolProperties.getPoolSize());
        scheduler.setThreadNamePrefix(poolProperties.getThreadNamePrefix());
        // 设置等待任务在关机时完成
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        // 设置等待终止时间
        scheduler.setAwaitTerminationSeconds(poolProperties.getAwaitTime());
        /*
         * 拒绝处理策略
         * CallerRunsPolicy()：交由调用方线程运行，比如 main 线程。
         * AbortPolicy()：直接抛出异常。
         * DiscardPolicy()：直接丢弃。
         * DiscardOldestPolicy()：丢弃队列中最老的任务。
         */
        scheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        //线程初始化
        scheduler.initialize();
        return scheduler;
    }

}
