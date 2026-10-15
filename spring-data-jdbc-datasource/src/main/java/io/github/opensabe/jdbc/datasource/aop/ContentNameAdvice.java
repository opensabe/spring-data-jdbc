package io.github.opensabe.jdbc.datasource.aop;

import io.github.opensabe.jdbc.core.BaseService;
import io.github.opensabe.jdbc.core.RepositoryBootConfig;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 这里用简单的ThreadLocal,如果service中用了多线程，那么新开启的线程重新计算自己的name
 * @author heng.ma
 */
public class ContentNameAdvice implements MethodInterceptor {

    private static final ThreadLocal<String> CONTENT_NAME_HOLDER = new ThreadLocal<>();

    public static String getRepositoryName () {
        return CONTENT_NAME_HOLDER.get();
    }

    private final Map<Object, String> cache = new ConcurrentHashMap<>();

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (StringUtils.hasText(CONTENT_NAME_HOLDER.get())) {
            return invocation.proceed();
        }
        // name 来自当前 service 实例的 repository，按目标对象缓存。MethodInvocation 每次调用都是新对象，不能当 key
        Object target = invocation.getThis();
        String name = target == null ? "default" : cache.computeIfAbsent(target, k -> {
            if (k instanceof BaseService service) {
                return ((RepositoryBootConfig)service.getRepository()).getConfig().getAttribute("name", String.class).orElse("default");
            }
            return "default";
        });
        try {
            CONTENT_NAME_HOLDER.set(name);
            return invocation.proceed();
        }finally {
            CONTENT_NAME_HOLDER.remove();
        }
    }
}
