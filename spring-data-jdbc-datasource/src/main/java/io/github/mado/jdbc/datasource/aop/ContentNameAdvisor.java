package io.github.mado.jdbc.datasource.aop;

import io.github.mado.jdbc.core.BaseService;
import org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor;
import org.springframework.core.Ordered;
import org.springframework.transaction.interceptor.BeanFactoryTransactionAttributeSourceAdvisor;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * @author heng.ma
 */
public class ContentNameAdvisor extends StaticMethodMatcherPointcutAdvisor {

    public ContentNameAdvisor(ContentNameAdvice advice, Optional<BeanFactoryTransactionAttributeSourceAdvisor> transactionAttributeSourceAdvisor) {
        super(advice);
        //order必须比Transaction靠前
        Integer i = transactionAttributeSourceAdvisor
                .map(BeanFactoryTransactionAttributeSourceAdvisor::getOrder)
                .map(order -> order -1)
                .orElse(Ordered.LOWEST_PRECEDENCE);
        setOrder(i);
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        return BaseService.class.equals(targetClass);
    }
}
