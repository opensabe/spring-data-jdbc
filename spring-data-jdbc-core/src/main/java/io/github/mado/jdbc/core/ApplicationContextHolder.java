package io.github.mado.jdbc.core;

import org.springframework.context.ApplicationContext;

/**
 * @author heng.ma
 */
public class ApplicationContextHolder  {

    private static ApplicationContext applicationContext;

    public ApplicationContextHolder(ApplicationContext applicationContext) {
        ApplicationContextHolder.applicationContext = applicationContext;
    }

    public static  <T> T getBean (Class<T> requiredType) {
        return applicationContext.getBean(requiredType);
    }
}
