package io.github.mado.jdbc.core;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.data.jdbc.repository.config.JdbcRepositoryConfigExtension;
import org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;
import org.springframework.data.repository.config.RepositoryConfigurationSource;

import java.lang.annotation.Annotation;

/**
 * @author heng.ma
 */
public class JdbcRepositoriesRegistrar extends RepositoryBeanDefinitionRegistrarSupport {
    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return EnableJdbcRepositories.class;
    }

    @Override
    protected RepositoryConfigurationExtension getExtension() {
        return new JdbcRepositoryConfigExtension() {
            @Override
            public void registerBeansForRoot(BeanDefinitionRegistry registry, RepositoryConfigurationSource configurationSource) {
                String name = configurationSource.getRequiredAttribute("name", String.class);
                registry.registerBeanDefinition(name+"RepositoryConfiguration", BeanDefinitionBuilder
                        .rootBeanDefinition(RepositoryConfigurationSource.class, () -> configurationSource).getBeanDefinition());
            }
        };
    }
}
