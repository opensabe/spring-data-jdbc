package io.github.mado.jdbc.datasource.support;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.lang.Nullable;
import org.springframework.transaction.TransactionManager;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.lang.reflect.Modifier;
import java.util.Objects;

/**
 * @author heng.ma
 */
@Configuration(proxyBeanMethods = false)
public class DefaultDataSourceConfiguration {

    private final String name;

    public DefaultDataSourceConfiguration (Environment environment) {
        this.name = environment.getProperty(JdbcNamedContextFactory.propertyName);
    }

    @Bean
    public DataSource defaultDataSource (MultipleDataSourceProperties properties, ApplicationContext applicationContext) {
        DataSource written = writeDataSource(properties, applicationContext);
        DataSource read = readOnlyDataSource(properties);
        return new WriteReadDataSource(written, read);
    }

    private DataSource writeDataSource (MultipleDataSourceProperties properties, ApplicationContext applicationContext) {
        MultipleDataSourceProperties.Properties defaultProperties = properties.defaultProperties(name);
        if (defaultProperties == null) {
            return applicationContext.getBean(DataSource.class);
        }

        return createDataSource(defaultProperties);
    }

    @Nullable
    private DataSource readOnlyDataSource (MultipleDataSourceProperties properties) {
        MultipleDataSourceProperties.Properties readOnly = properties.readOnlyProperties(name);
        if (readOnly != null) {
            return createDataSource(properties.readOnlyProperties(name));
        }
        return null;
    }

    private DataSource createDataSource (MultipleDataSourceProperties.Properties properties) {
        if (!StringUtils.hasText(properties.getName())) {
            properties.setName(name);
        }

        HikariDataSource dataSource = properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();

        HikariConfig hikariConfig = properties.getHikari();

        if (hikariConfig != null) {
            for (var field : HikariConfig.class.getDeclaredFields()) {
                if (!Modifier.isFinal(field.getModifiers())) {
                    field.setAccessible(true);
                    try {
                        Object o = field.get(hikariConfig);
                        if (Objects.nonNull(o)) {
                            field.set(dataSource, o);
                        }
                    }
                    catch (Exception e) {
                        throw new RuntimeException("Failed to copy HikariConfig state: " + e.getMessage(), e);
                    }
                }
            }
        }
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate (@Qualifier("defaultDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public NamedParameterJdbcOperations namedParameterJdbcOperations (JdbcTemplate jdbcTemplate) {
        return new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    @Bean
    @ConditionalOnMissingBean(TransactionManager.class)
    DataSourceTransactionManager transactionManager(Environment environment, @Qualifier("defaultDataSource")DataSource dataSource,
                                                    ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
        DataSourceTransactionManager transactionManager = createTransactionManager(environment, dataSource);
        transactionManagerCustomizers.ifAvailable((customizers) -> customizers.customize(transactionManager));
        return transactionManager;
    }

    private DataSourceTransactionManager createTransactionManager(Environment environment, DataSource dataSource) {
        return environment.getProperty("spring.dao.exceptiontranslation.enabled", Boolean.class, Boolean.TRUE)
                ? new JdbcTransactionManager(dataSource) : new DataSourceTransactionManager(dataSource);
    }
}
