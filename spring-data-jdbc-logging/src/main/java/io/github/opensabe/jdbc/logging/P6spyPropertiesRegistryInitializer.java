package io.github.opensabe.jdbc.logging;

import org.springframework.boot.BootstrapRegistry;
import org.springframework.boot.BootstrapRegistryInitializer;

/**
 * 因为p6spy加载时机比较早，因此使用这种方法来添加配置
 * @author heng.ma
 */
public class P6spyPropertiesRegistryInitializer implements BootstrapRegistryInitializer {

    @Override
    public void initialize(BootstrapRegistry registry) {
        //使用log框架打印日志
        System.setProperty("p6spy.config.appender", "com.p6spy.engine.spy.appender.Slf4JLogger");
//        System.setProperty("p6spy.config.logMessageFormat", "com.p6spy.engine.spy.appender.CustomLineFormat");
//        System.setProperty("p6spy.config.customLogMessageFormat", "%(sql)");
        //打印日志格式，这里使用我们自定义格式，限制日志长度
        System.setProperty("p6spy.config.logMessageFormat", "io.github.opensabe.jdbc.logging.LimitableLineFormat");
    }
}
