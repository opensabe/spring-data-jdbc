package io.github.opensabe.jdbc.autoconfigure.config;

import io.github.opensabe.jdbc.core.ApplicationContextHolder;
import io.github.opensabe.jdbc.core.converter.IntegerToBooleanConverter;
import io.github.opensabe.jdbc.core.executor.CustomerJdbcOperation;
import io.github.opensabe.jdbc.core.executor.CustomerJdbcOperationImpl;
import io.github.opensabe.jdbc.core.executor.ExtendSQLGeneratorSource;
import io.github.opensabe.jdbc.core.executor.PropertyAccessorCustomizer;
import io.github.opensabe.jdbc.core.jackson.PageSerializeModule;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.data.convert.*;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.jdbc.core.convert.BatchJdbcOperations;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;
import org.springframework.data.mapping.model.ConvertingPropertyAccessor;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.relational.repository.query.RelationalExampleMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author heng.ma
 */
@Configuration(proxyBeanMethods = false)
public class GenerateConfiguration {


    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public PropertyAccessorCustomizer convertingPropertyAccessorCustomizer (ConversionService conversionService) {
        return accessor -> new ConvertingPropertyAccessor<>(accessor, conversionService);
    }


    @Bean
    public ApplicationContextHolder applicationHolder (ApplicationContext applicationContext) {
        return new ApplicationContextHolder(applicationContext);
    }

    @Bean
    @ConditionalOnMissingBean
    public RelationalExampleMapper relationalExampleMapper (RelationalMappingContext context) {
        return new RelationalExampleMapper(context);
    }

    @Bean
    @ConditionalOnMissingBean
    public ExtendSQLGeneratorSource globalSQLGeneratorSource (RelationalMappingContext context, Dialect dialect, JdbcConverter converter) {
        return new ExtendSQLGeneratorSource(context, converter, dialect);
    }

    @Bean
    @ConditionalOnMissingBean
    public CustomerJdbcOperation criteriaJdbcOperation (JdbcAggregateTemplate jdbcAggregateTemplate, ExtendSQLGeneratorSource extendSQLGeneratorSource, NamedParameterJdbcTemplate namedParameterJdbcTemplate, Dialect dialect) {
        return new CustomerJdbcOperationImpl(jdbcAggregateTemplate, extendSQLGeneratorSource, namedParameterJdbcTemplate, dialect);
    }

    @Bean
    @ConditionalOnMissingBean
    public BatchJdbcOperations batchJdbcOperations (JdbcTemplate jdbcTemplate) {
        return new BatchJdbcOperations(jdbcTemplate);
    }

    @Bean
    public IntegerToBooleanConverter integerToBooleanConverter () {
        return new IntegerToBooleanConverter();
    }

    @Bean
    public Consumer<GenericConversionService> extensionsConverterConsumer (ObjectProvider<Converter<?,?>> converters) {
        return conversionService -> converters.forEach(conversionService::addConverter);
    }

    @Bean
    @ConditionalOnMissingBean
    public ConversionService conversionService (Optional<List<Consumer<GenericConversionService>>> consumers) {
        DefaultConversionService service = new DefaultConversionService();
        consumers.ifPresent(cs -> cs.forEach(c -> c.accept(service)));
        return service;
    }

    @Bean
    @ConditionalOnMissingBean
    public PropertyValueConversions propertyValueConversions (Optional<PropertyValueConverterFactory> propertyValueConverterFactory, Optional<List<Consumer<PropertyValueConverterRegistrar>>> config) {
        SimplePropertyValueConversions conversions = new SimplePropertyValueConversions();
        PropertyValueConverterRegistrar registrar = new PropertyValueConverterRegistrar();
        config.stream().flatMap(List::stream).reduce(Consumer::andThen).ifPresent(c -> c.accept(registrar));
        conversions.setConverterFactory(propertyValueConverterFactory.orElseGet(PropertyValueConverterFactory::simple));
        conversions.setValueConverterRegistry(registrar.buildRegistry());
        return conversions;
    }

    @Bean
    @ConditionalOnMissingBean
    public PropertyValueConversionService propertyValueConversionService(@Lazy JdbcCustomConversions conversions) {
        return new PropertyValueConversionService(conversions);
    }

    @Bean
    @ConditionalOnMissingBean
    public PageSerializeModule pageSerializeModule () {
        return new PageSerializeModule();
    }
}
