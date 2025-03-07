package io.github.opensabe.jdbc.datasource.aop;

import io.github.opensabe.jdbc.datasource.ReadOnly;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repository的切面，如果方法上包含@ReadOnly注解，或者sql中包含,#mode=readonly就切换只读库
 * <b>如果当前处于事务中，不切换，因为已经用其他线程开启过事务了</b>
 * @author heng.ma
 */
public class ReadOnlyRepositoryAdvice implements MethodInterceptor {

    private final static ThreadLocal<Boolean> READ_ONLY_HOLDER = new ThreadLocal<>();

    private Map<MethodInvocation, Boolean> cache = new ConcurrentHashMap<>();

    public static boolean containsReadOnly (String sql) {
        return sql.toLowerCase(Locale.ROOT).contains("/*#mode=readonly*/");
    }

    public static boolean isReadOnly () {
        return Optional.ofNullable(READ_ONLY_HOLDER.get()).orElse(false);
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        //该Advice在最前面，因此在repository开启事务之前判断是否在事务中，如果在事务中，说明事务是在Service开启的
        //service开启的事务不切换数据源
        boolean isReadOnly = cache.computeIfAbsent(invocation, k -> {
            boolean active = TransactionSynchronizationManager.isSynchronizationActive();
            if (active) {
                return false;
            }
            Method method = invocation.getMethod();
            ReadOnly readOnly = AnnotatedElementUtils.findMergedAnnotation(method, ReadOnly.class);
            boolean read = Objects.nonNull(readOnly);
            if (!read) {
                Query query = AnnotatedElementUtils.findMergedAnnotation(method, Query.class);
                read = Objects.nonNull(query) && containsReadOnly(query.value());
            }
            return read;
        });
        try {
            READ_ONLY_HOLDER.set(isReadOnly);
            return invocation.proceed();
        }finally {
            READ_ONLY_HOLDER.remove();
        }
    }
}
