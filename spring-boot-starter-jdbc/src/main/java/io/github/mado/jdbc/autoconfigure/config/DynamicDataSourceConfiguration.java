package io.github.mado.jdbc.autoconfigure.config;

import io.github.mado.jdbc.core.RepositoryFactorySupportSupplier;
import io.github.mado.jdbc.datasource.aop.ContentNameAdvice;
import io.github.mado.jdbc.datasource.aop.ContentNameAdvisor;
import io.github.mado.jdbc.datasource.aop.ReadOnlyRepositoryFactoryCustomizer;
import io.github.mado.jdbc.datasource.support.JdbcNamedContextFactory;
import io.github.mado.jdbc.datasource.support.MultipleDataSourceProperties;
import io.github.mado.jdbc.datasource.support.TransactionManagerBeanFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.interceptor.BeanFactoryTransactionAttributeSourceAdvisor;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.sql.DataSource;
import java.util.Optional;

/**
 * @author heng.ma
 */
@EnableConfigurationProperties(MultipleDataSourceProperties.class)
@ConditionalOnBean(MultipleDataSourceProperties.class)
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
    @ConditionalOnMissingBean
    public JdbcNamedContextFactory jdbcNamedContextFactory () {
        return new JdbcNamedContextFactory();
    }

    @Bean
    @ConditionalOnMissingBean
    public DataSource dataSource (JdbcNamedContextFactory jdbcNamedContextFactory) {
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
    public RepositoryFactorySupportSupplier repositoryFactorySupportSupplier (JdbcNamedContextFactory factory) {
        return bean -> factory.getRepositoryFactorySupport(bean.getConfigSource().getAttribute("name").orElse("default"));
    }
}
