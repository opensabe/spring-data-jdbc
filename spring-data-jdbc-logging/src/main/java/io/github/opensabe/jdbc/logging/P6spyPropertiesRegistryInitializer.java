package io.github.opensabe.jdbc.logging;

import org.springframework.boot.BootstrapRegistry;
import org.springframework.boot.BootstrapRegistryInitializer;

/**
 * @author heng.ma
 */
public class P6spyPropertiesRegistryInitializer implements BootstrapRegistryInitializer {

    static {

//        System.setProperty("p6spy.config.excludecategories", "info,debug,result,resultset");
    }

    @Override
    public void initialize(BootstrapRegistry registry) {
        System.setProperty("p6spy.config.appender", "com.p6spy.engine.spy.appender.Slf4JLogger");
        System.setProperty("p6spy.config.logMessageFormat", "com.p6spy.engine.spy.appender.CustomLineFormat");
        System.setProperty("p6spy.config.customLogMessageFormat", "%(sql)");
    }
}
