package io.github.mado.jdbc.autoconfigure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.mado.jdbc.converter.SpecifyConvertingPropertyAccessor;
import io.github.mado.jdbc.converter.SpecifyPropertyConverterFactory;
import io.github.mado.jdbc.converter.extension.BigIntToIntegerConverter;
import io.github.mado.jdbc.converter.extension.BigIntToLongConverter;
import io.github.mado.jdbc.converter.extension.IntegerToBooleanConverter;
import io.github.mado.jdbc.converter.extension.JsonPropertyValueConverter;
import io.github.mado.jdbc.core.executor.CustomerJdbcOperation;
import io.github.mado.jdbc.core.executor.CustomerJdbcOperationImpl;
import io.github.mado.jdbc.core.executor.ExtendSQLGeneratorSource;
import io.github.mado.jdbc.core.executor.PropertyAccessorCustomizer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.data.convert.PropertyValueConversions;
import org.springframework.data.convert.PropertyValueConverterFactory;
import org.springframework.data.convert.PropertyValueConverterRegistrar;
import org.springframework.data.convert.SimplePropertyValueConversions;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.jdbc.core.convert.BatchJdbcOperations;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author heng.ma
 */
@Configuration(proxyBeanMethods = false)
public class ExtensionsConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ExtendSQLGeneratorSource globalSQLGeneratorSource (RelationalMappingContext context, Dialect dialect, JdbcConverter converter, List<PropertyAccessorCustomizer> propertyAccessorCustomizers) {
        return new ExtendSQLGeneratorSource(context, converter, dialect, propertyAccessorCustomizers);
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
    public Consumer<GenericConversionService> innerConverterConsumer () {
        return conversionService -> {
          conversionService.addConverter(new BigIntToIntegerConverter());
          conversionService.addConverter(new BigIntToLongConverter());
          conversionService.addConverter(new IntegerToBooleanConverter());
        };
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
    public JsonPropertyValueConverter jsonPropertyValueConverter (ObjectMapper objectMapper) {
        return new JsonPropertyValueConverter(objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public PropertyValueConverterFactory propertyValueConverterFactory (ApplicationContext applicationContext) {
        return new SpecifyPropertyConverterFactory(applicationContext);
    }

    @Bean
    @ConditionalOnMissingBean
    public PropertyValueConversions propertyValueConversions (PropertyValueConverterFactory propertyValueConverterFactory, Optional<List<Consumer<PropertyValueConverterRegistrar>>> config) {
        SimplePropertyValueConversions conversions = new SimplePropertyValueConversions();
        PropertyValueConverterRegistrar registrar = new PropertyValueConverterRegistrar();
        config.stream().flatMap(List::stream).reduce(Consumer::andThen).ifPresent(c -> c.accept(registrar));
        conversions.setConverterFactory(propertyValueConverterFactory);
        conversions.setValueConverterRegistry(registrar.buildRegistry());
        return conversions;
    }

    @Bean
    @Order
    public PropertyAccessorCustomizer propertyAccessorCustomizer (ConversionService conversionService, ApplicationContext applicationContext) {
        return accessor -> new SpecifyConvertingPropertyAccessor<>(accessor, conversionService, applicationContext);
    }
}
