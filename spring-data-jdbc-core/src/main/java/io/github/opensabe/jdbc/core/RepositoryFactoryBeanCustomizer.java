package io.github.opensabe.jdbc.core;

public interface RepositoryFactoryBeanCustomizer {

    @SuppressWarnings("rawtypes")
    void customize (ExtendRepositoryFactoryBean factoryBean);
}
