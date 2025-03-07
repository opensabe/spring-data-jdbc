package io.github.opensabe.jdbc.autoconfigure.config;

import io.github.opensabe.jdbc.core.RepositoryFactoryBeanCustomizer;
import io.github.opensabe.jdbc.datasource.aop.ContentNameAdvice;
import io.github.opensabe.jdbc.datasource.aop.ContentNameAdvisor;
import io.github.opensabe.jdbc.datasource.aop.ReadOnlyRepositoryFactoryCustomizer;
import io.github.opensabe.jdbc.datasource.support.JdbcNamedContextFactory;
import io.github.opensabe.jdbc.datasource.support.MultipleDataSourceProperties;
import io.github.opensabe.jdbc.datasource.support.TransactionManagerBeanFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.*;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.transaction.interceptor.BeanFactoryTransactionAttributeSourceAdvisor;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.sql.DataSource;
import java.util.Optional;

/**
 * @author heng.ma
 */
@ConditionalOnClass(MultipleDataSourceProperties.class)
@Conditional(DynamicDataSourceConfiguration.PropertyCondition.class)
@EnableConfigurationProperties(MultipleDataSourceProperties.class)
@Configuration(proxyBeanMethods = false)
public class DynamicDataSourceConfiguration {

    private final BeanFactory beanFactory;

    public DynamicDataSourceConfiguration(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }


    @Bean
    public BeanPostProcessor transactionBeanFactoryBeanPostProcessor () {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof TransactionAspectSupport advisor) {
                    advisor.setBeanFactory(new TransactionManagerBeanFactory(beanFactory));
                    return advisor;
                }
                return bean;
            }
        };
    }


    @Bean
    @ConditionalOnClass(JdbcNamedContextFactory.class)
    public JdbcNamedContextFactory.Specification dataSourceSpecification () {
        return new JdbcNamedContextFactory.Specification("default",
                ConverterConfiguration.SmartJdbcConfiguration.class,
                GenerateConfiguration.class);
    }


    @Bean
    @ConditionalOnMissingBean
    public JdbcNamedContextFactory jdbcNamedContextFactory (ObjectProvider<JdbcNamedContextFactory.Specification> specifications) {
        return new JdbcNamedContextFactory(specifications.stream().toList());
    }

    @Bean
    @ConditionalOnMissingBean
    public DataSource dataSource (MultipleDataSourceProperties properties, JdbcNamedContextFactory jdbcNamedContextFactory) {
        properties.getDatasource().keySet().forEach(jdbcNamedContextFactory::getDataSource);
        return jdbcNamedContextFactory.getDataSource("default");
    }

    @Bean
    @ConditionalOnMissingBean
    public ContentNameAdvisor contentNameAdvisor (Optional<BeanFactoryTransactionAttributeSourceAdvisor> beanFactoryTransactionAttributeSourceAdvisor) {

        return new ContentNameAdvisor(new ContentNameAdvice(), beanFactoryTransactionAttributeSourceAdvisor);
    }


    @Bean
    @ConditionalOnMissingBean
    public ReadOnlyRepositoryFactoryCustomizer readOnlyRepositoryFactoryCustomizer () {
        return new ReadOnlyRepositoryFactoryCustomizer();
    }

    @Bean
    @ConditionalOnMissingBean
    public RepositoryFactoryBeanCustomizer repositoryFactoryBeanCustomizer (JdbcNamedContextFactory factory) {
        return factoryBean -> {
            String name = factoryBean.getConfigSource().getAttribute("name").orElse("default");
            factoryBean.setJdbcOperations(factory.getNamedParameterJdbcOperations(name));
            factoryBean.setDataAccessStrategy(factory.getDataAccessStrategy(name));
            factoryBean.setTransactionManager(name);
        };
    }

    public static class PropertyCondition implements Condition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            Binder binder = Binder.get(context.getEnvironment());
            return binder.bind(MultipleDataSourceProperties.PREFIX, MultipleDataSourceProperties.class).isBound();
        }
    }
}



