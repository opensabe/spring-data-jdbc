package io.github.mado.jdbc.core;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.data.repository.config.RepositoryConfigurationSource;
import org.springframework.data.repository.core.support.RepositoryFactoryCustomizer;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

/**
 * @author heng.ma
 */
public class RepositoryFactoryCategoryCustomizer implements RepositoryFactoryCustomizer {

    
    private final RepositoryConfigurationSource configurationSource;

    public RepositoryFactoryCategoryCustomizer(RepositoryConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }

    @Override
    public void customize(RepositoryFactorySupport repositoryFactory) {
        repositoryFactory.addRepositoryProxyPostProcessor((factory, repositoryInformation) -> factory.addInterface(RepositoryBootConfig.class));
        repositoryFactory.addRepositoryProxyPostProcessor((factory, repositoryInformation) -> factory.addAdvice(new MethodInterceptor() {
            @Override
            public Object invoke(MethodInvocation invocation) throws Throwable {
                Class<?> declaringClass = invocation.getMethod().getDeclaringClass();
                if (RepositoryBootConfig.class.equals(declaringClass)) {
                    return configurationSource;
                }
                return invocation.proceed();
            }
        }));
    }
}
