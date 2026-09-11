package io.github.opensabe.jdbc.datasource.aop;

import io.github.opensabe.jdbc.datasource.ReadOnly;
import org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Method;

/**
 * Service的切面，如果service方法上包含@ReadOnly注解,切换只读库
 * @author heng.ma
 */
public class ReadOnlyServiceAdviser extends StaticMethodMatcherPointcutAdvisor {


    public ReadOnlyServiceAdviser(ReadOnlyServiceAdvice advice) {
        setAdvice(advice);
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        return AnnotatedElementUtils.hasAnnotation(method, ReadOnly.class);
    }
}
