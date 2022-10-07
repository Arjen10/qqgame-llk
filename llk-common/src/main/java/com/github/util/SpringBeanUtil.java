package com.github.util;

import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author Arjen10
 * @date 2022/9/16 17:03
 */
public class SpringBeanUtil {

    private static ConfigurableApplicationContext APPLICATION_CONTEXT;

    public static <T> T getBean(Class<T> tClass) {
        return APPLICATION_CONTEXT.getBean(tClass);
    }

    public static <T> T getBean(String beanName, Class<T> tClass) {
        return APPLICATION_CONTEXT.getBean(beanName, tClass);
    }


    public static void setApplicationContext(ConfigurableApplicationContext applicationContext) {
        APPLICATION_CONTEXT = applicationContext;
    }

}
