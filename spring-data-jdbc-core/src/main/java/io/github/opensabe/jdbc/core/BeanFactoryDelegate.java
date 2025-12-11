package io.github.opensabe.jdbc.core;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;

/**
 * @author heng.ma
 */
@SuppressWarnings("NullableProblems")
public class BeanFactoryDelegate implements BeanFactory {

    private final BeanFactory delegate;

    public BeanFactoryDelegate(BeanFactory delegate) {
        this.delegate = delegate;
    }

    @Override
    public Object getBean(String name) throws BeansException {
        return delegate.getBean(name);
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        return delegate.getBean(name, requiredType);
    }

    @Override
    public Object getBean(String name, Object... args) throws BeansException {
        return delegate.getBean(name, args);
    }

    @Override
    public <T> T getBean(Class<T> requiredType) throws BeansException {
        return delegate.getBean(requiredType);
    }

    @Override
    public <T> T getBean(Class<T> requiredType, Object... args) throws BeansException {
        return delegate.getBean(requiredType, args);
    }

    @Override
    public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType) {
        return delegate.getBeanProvider(requiredType);
    }

    @Override
    public <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType) {
        return delegate.getBeanProvider(requiredType);
    }

    @Override
    public <T> ObjectProvider<T> getBeanProvider(ParameterizedTypeReference<T> requiredType) {
        return delegate.getBeanProvider(requiredType);
    }

    @Override
    public boolean containsBean(String name) {
        return delegate.containsBean(name);
    }

    @Override
    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return delegate.isSingleton(name);
    }

    @Override
    public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
        return delegate.isPrototype(name);
    }

    @Override
    public boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
        return delegate.isTypeMatch(name, typeToMatch);
    }

    @Override
    public boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
        return delegate.isTypeMatch(name, typeToMatch);
    }

    @Override
    public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return delegate.getType(name);
    }

    @Override
    public Class<?> getType(String name, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
        return delegate.getType(name, allowFactoryBeanInit);
    }

    @Override
    public String[] getAliases(String name) {
        return delegate.getAliases(name);
    }
}
