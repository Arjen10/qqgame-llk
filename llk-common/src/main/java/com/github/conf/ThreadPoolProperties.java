package com.github.conf;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @author Arjen10
 * @date 2022/9/16 14:43
 */
@Slf4j
@Data
@Configuration
@ConfigurationProperties("thread.pool")
public class ThreadPoolProperties {

    /**
     * 最大线程数
     */
    private int poolSize;

    /**
     * 允许线程空闲时间（单位：默认为秒）
     */
    private int awaitTime;

    /**
     * 线程池的前缀名称
     */
    private String threadNamePrefix;

}
