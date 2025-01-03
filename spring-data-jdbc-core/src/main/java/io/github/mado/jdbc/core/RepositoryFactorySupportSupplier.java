package io.github.mado.jdbc.core;

import org.springframework.data.repository.core.support.RepositoryFactorySupport;

public interface RepositoryFactorySupportSupplier {

    @SuppressWarnings("rawtypes")
    RepositoryFactorySupport get (ExtendRepositoryFactoryBean factoryBean);
}
