package io.github.mado.jdbc.core;

public interface RepositoryFactoryBeanCustomizer {

    @SuppressWarnings("rawtypes")
    void customize (ExtendRepositoryFactoryBean factoryBean);
}
