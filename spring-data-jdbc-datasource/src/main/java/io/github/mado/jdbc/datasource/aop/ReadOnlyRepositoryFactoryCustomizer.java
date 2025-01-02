package io.github.mado.jdbc.datasource.aop;

import org.springframework.data.repository.core.support.RepositoryFactoryCustomizer;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

/**
 * @author heng.ma
 */
public class ReadOnlyRepositoryFactoryCustomizer implements RepositoryFactoryCustomizer {
    @Override
    public void customize(RepositoryFactorySupport repositoryFactory) {
        repositoryFactory.addRepositoryProxyPostProcessor((factory, repositoryInformation) -> factory.addAdvice(0, new ReadOnlyRepositoryAdvice()));
    }
}
