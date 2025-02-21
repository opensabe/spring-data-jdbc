package io.github.mado.jdbc.autoconfigure.config;

import io.github.mado.jdbc.observation.advice.RepositoryObservationAdvice;
import io.github.mado.jdbc.observation.advice.TransactionObservationAdvice;
import io.github.mado.jdbc.observation.advice.TransactionObservationAdvisor;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.core.support.RepositoryFactoryCustomizer;
import org.springframework.transaction.interceptor.BeanFactoryTransactionAttributeSourceAdvisor;

/**
 * @author heng.ma
 */
@ConditionalOnClass(RepositoryObservationAdvice.class)
@Configuration(proxyBeanMethods = false)
public class ObservationConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RepositoryObservationAdvice sqlExecuteAdvice (ObjectProvider<ObservationRegistry> registry) {

        return new RepositoryObservationAdvice(registry);
    }
    @Bean
    public RepositoryFactoryCustomizer repositoryObservationFactoryCustomizer (RepositoryObservationAdvice advice) {
        return repositoryFactory -> repositoryFactory.addRepositoryProxyPostProcessor((factory, repositoryInformation) -> factory.addAdvice(advice));
    }

    @Bean
    @ConditionalOnMissingBean
    public TransactionObservationAdvice transactionObservationAdvice (ObjectProvider<ObservationRegistry> registry) {
        return new TransactionObservationAdvice(registry);
    }

    @Bean
    public TransactionObservationAdvisor transactionObservationAdvisor (TransactionObservationAdvice advice, BeanFactoryTransactionAttributeSourceAdvisor attributeSourceAdvisor) {
        return new TransactionObservationAdvisor(advice, attributeSourceAdvisor);
    }
}
