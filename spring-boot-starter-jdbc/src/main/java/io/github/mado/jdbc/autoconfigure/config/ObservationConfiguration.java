package io.github.mado.jdbc.autoconfigure.config;

import io.github.mado.jdbc.observation.advice.RepositoryObservationAdvice;
import io.github.mado.jdbc.observation.advice.TransactionObservationAdvice;
import io.github.mado.jdbc.observation.advice.TransactionObservationAdvisor;
import io.github.mado.jdbc.observation.jfr.ConnectionJFRGenerator;
import io.github.mado.jdbc.observation.jfr.JFRObservationHandler;
import io.github.mado.jdbc.observation.jfr.ObservationToJFRGenerator;
import io.github.mado.jdbc.observation.jfr.SQLExecuteJFRGenerator;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.core.support.RepositoryFactoryCustomizer;
import org.springframework.transaction.interceptor.BeanFactoryTransactionAttributeSourceAdvisor;

import java.util.List;

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

    @Bean
    @ConditionalOnMissingBean
    public ConnectionJFRGenerator connectionJFRGenerator () {
        return new ConnectionJFRGenerator();
    }

    @Bean
    @ConditionalOnMissingBean
    public SQLExecuteJFRGenerator sqlExecuteJFRGenerator () {
        return new SQLExecuteJFRGenerator();
    }

    @Bean
    @ConditionalOnMissingBean
    public JFRObservationHandler jdbcJFRObservationHandler (List<ObservationToJFRGenerator> generators) {
        return new JFRObservationHandler(generators);
    }
}
