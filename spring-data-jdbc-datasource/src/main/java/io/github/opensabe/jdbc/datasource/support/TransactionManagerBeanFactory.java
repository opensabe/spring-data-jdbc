package io.github.opensabe.jdbc.datasource.support;

import io.github.opensabe.jdbc.core.BeanFactoryDelegate;
import io.github.opensabe.jdbc.datasource.aop.ContentNameAdvice;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.util.Lazy;
import org.springframework.transaction.TransactionManager;
import org.springframework.util.StringUtils;

/**
 * @author heng.ma
 */
@SuppressWarnings("unchecked")
public class TransactionManagerBeanFactory extends BeanFactoryDelegate {

    private final Lazy<JdbcNamedContextFactory> factory;

    public TransactionManagerBeanFactory(BeanFactory delegate) {
        super(delegate);
        this.factory = Lazy.of(() -> delegate.getBean(JdbcNamedContextFactory.class));
    }


    @Override
    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        if (TransactionManager.class.isAssignableFrom(requiredType)) {
            try {
                return super.getBean(name, requiredType);
            }catch (BeansException e) {
                return (T)factory.get().getTransactionManager(name);
            }
        }
        return super.getBean(name, requiredType);
    }

    @Override
    public <T> T getBean(Class<T> requiredType) throws BeansException {
        String contentName = ContentNameAdvice.getRepositoryName();
        if (StringUtils.hasText(contentName) && TransactionManager.class.isAssignableFrom(requiredType)) {
            return (T)factory.get().getTransactionManager(contentName);
        }
        return super.getBean(requiredType);
    }



    @Override
    public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType) {
        String contentName = ContentNameAdvice.getRepositoryName();
        if (StringUtils.hasText(contentName) && TransactionManager.class.isAssignableFrom(requiredType)) {
            return (ObjectProvider<T>)factory.get().getTransactionManagerProvider(contentName);
        }
        return super.getBeanProvider(requiredType);
    }
}
