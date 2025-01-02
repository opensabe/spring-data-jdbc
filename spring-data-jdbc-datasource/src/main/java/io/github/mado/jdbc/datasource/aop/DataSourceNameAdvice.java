package io.github.mado.jdbc.datasource.aop;

import io.github.mado.jdbc.core.BaseService;
import io.github.mado.jdbc.core.RepositoryBootConfig;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 这里用简单的ThreadLocal,如果service中用了多线程，那么新开启的线程重新计算自己的name
 * @author heng.ma
 */
public class DataSourceNameAdvice implements MethodInterceptor {

    private static final ThreadLocal<String> DATA_SOURCE_PLACE_HOLDER = new ThreadLocal<>();

    public static String getRepositoryName () {
        return DATA_SOURCE_PLACE_HOLDER.get();
    }

    private Map<MethodInvocation, String> cache = new ConcurrentHashMap<>();

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (StringUtils.hasText(DATA_SOURCE_PLACE_HOLDER.get())) {
            return invocation.proceed();
        }
        String name = cache.computeIfAbsent(invocation, k -> {
            if (invocation.getThis() instanceof BaseService service) {
                return ((RepositoryBootConfig)service.getRepository()).getConfig().getAttribute("name", String.class).orElse("default");
            }
            return "default";
        });
        try {
            DATA_SOURCE_PLACE_HOLDER.set(name);
            return invocation.proceed();
        }finally {
            DATA_SOURCE_PLACE_HOLDER.remove();
        }
    }
}
