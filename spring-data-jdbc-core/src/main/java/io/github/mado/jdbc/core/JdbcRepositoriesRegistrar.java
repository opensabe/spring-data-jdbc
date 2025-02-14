package io.github.mado.jdbc.core;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.data.jdbc.repository.config.JdbcRepositoryConfigExtension;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

import java.lang.annotation.Annotation;

/**
 * @author heng.ma
 */
@SuppressWarnings("NullableProblems")
public class JdbcRepositoriesRegistrar extends RepositoryBeanDefinitionRegistrarSupport {
    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return EnableJdbcRepositories.class;
    }

    @Override
    protected RepositoryConfigurationExtension getExtension() {
        return new JdbcRepositoryConfigExtension() {
            @Override
            public void postProcess(BeanDefinitionBuilder builder, AnnotationRepositoryConfigurationSource config) {
                builder.addPropertyValue("configSource", config);
            }
        };
    }
}
