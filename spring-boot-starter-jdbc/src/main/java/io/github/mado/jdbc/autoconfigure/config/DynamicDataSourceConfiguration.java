package io.github.mado.jdbc.autoconfigure.config;

import io.github.mado.jdbc.datasource.aop.ContentNameAdvice;
import io.github.mado.jdbc.datasource.aop.ContentNameAdvisor;
import io.github.mado.jdbc.datasource.aop.ReadOnlyRepositoryFactoryCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.interceptor.BeanFactoryTransactionAttributeSourceAdvisor;

import java.util.Optional;

/**
 * @author heng.ma
 */
@Configuration(proxyBeanMethods = false)
public class DynamicDataSourceConfiguration {

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
}
