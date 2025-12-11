package io.github.opensabe.jdbc.autoconfigure.config;

import io.github.opensabe.jdbc.converter.InternalConversions;
import io.github.opensabe.jdbc.converter.InternalJdbcConverter;
import io.github.opensabe.jdbc.converter.SpecifyPropertyConverterFactory;
import io.github.opensabe.jdbc.converter.extension.JsonPropertyValueConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.PropertyValueConversionService;
import org.springframework.data.convert.PropertyValueConversions;
import org.springframework.data.convert.PropertyValueConverterFactory;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.jdbc.core.convert.*;
import org.springframework.data.jdbc.core.dialect.JdbcDialect;
import org.springframework.data.jdbc.core.mapping.JdbcMappingContext;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

/**
 * @author heng.ma
 */
@ConditionalOnClass(JsonPropertyValueConverter.class)
@Import(ConverterConfiguration.SmartJdbcConfiguration.class)
@Configuration(proxyBeanMethods = false)
public class ConverterConfiguration {
    @Bean
    @ConditionalOnMissingBean
    @SuppressWarnings("unused")
    public JsonPropertyValueConverter jsonPropertyValueConverter(JsonMapper objectMapper) {
        return new JsonPropertyValueConverter(objectMapper);
    }


    @Bean
    @ConditionalOnMissingBean
    @SuppressWarnings("unused")
    public PropertyValueConverterFactory propertyValueConverterFactory(ApplicationContext applicationContext) {
        return new SpecifyPropertyConverterFactory(applicationContext);
    }


    @SuppressWarnings("NullableProblems")
    public static class SmartJdbcConfiguration extends AbstractJdbcConfiguration {

        private ApplicationContext applicationContext;

        private final PropertyValueConversions propertyValueConversions;

        @SuppressWarnings("rawtypes")
        private final List<Converter> converters;

        private final PropertyValueConversionService propertyValueConversionService;

        public SmartJdbcConfiguration(PropertyValueConversions propertyValueConversions, @SuppressWarnings("rawtypes")List<Converter> converters, @Lazy PropertyValueConversionService propertyValueConversionService) {
            this.propertyValueConversions = propertyValueConversions;
//            this.propertyAccessorCustomizer = propertyAccessorCustomizer.stream().reduce(PropertyAccessorCustomizer::then).orElse(p -> p);
            this.converters = converters;
            this.propertyValueConversionService = propertyValueConversionService;
        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) {
            super.setApplicationContext(applicationContext);
            this.applicationContext = applicationContext;
        }


        @Bean
        @ConditionalOnMissingBean
        @Override
        public JdbcAggregateTemplate jdbcAggregateTemplate(ApplicationContext applicationContext, JdbcMappingContext mappingContext, JdbcConverter converter, DataAccessStrategy dataAccessStrategy) {
            return super.jdbcAggregateTemplate(applicationContext, mappingContext, converter, dataAccessStrategy);
        }

        @Bean
        @ConditionalOnMissingBean
        @Override
        public JdbcDialect jdbcDialect(NamedParameterJdbcOperations operations) {
            return super.jdbcDialect(operations);
        }

        @Bean
        @ConditionalOnMissingBean
        @Override
        public DataAccessStrategy dataAccessStrategyBean(NamedParameterJdbcOperations operations,
                                                         JdbcConverter jdbcConverter, JdbcMappingContext context, JdbcDialect dialect) {
            return super.dataAccessStrategyBean(operations, jdbcConverter, context, dialect);
        }



        @Bean
        @Override
        public JdbcCustomConversions jdbcCustomConversions() {
            return new InternalConversions(applicationContext.getBean(Dialect.class), userConverters(), propertyValueConversions);
        }



        @Override
        @Bean
        public JdbcConverter jdbcConverter(JdbcMappingContext mappingContext,
                                           NamedParameterJdbcOperations operations,
                                           @Lazy RelationResolver relationResolver,
                                           JdbcCustomConversions conversions, JdbcDialect dialect) {
            DefaultJdbcTypeFactory jdbcTypeFactory = new DefaultJdbcTypeFactory(operations.getJdbcOperations(), dialect.getArraySupport());
            return new InternalJdbcConverter(mappingContext, relationResolver, conversions, jdbcTypeFactory, converters, propertyValueConversionService);
        }
    }
}
