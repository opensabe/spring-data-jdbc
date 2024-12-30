package io.github.mado.jdbc.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.lang.reflect.Modifier;
import java.util.Objects;

/**
 * @author heng.ma
 */
public class DataSourceDefaultConfiguration implements EnvironmentAware, BeanPostProcessor {

    private Environment environment;

    private final MultipleDataSourceProperties multipleDataSourceProperties;

    public DataSourceDefaultConfiguration(MultipleDataSourceProperties multipleDataSourceProperties) {
        this.multipleDataSourceProperties = multipleDataSourceProperties;
    }

    @Bean
    public HikariDataSource hikariDataSource () {
        String dataSourceName = environment.getProperty(DataSourceContextFactory.propertyName);
        MultipleDataSourceProperties.Properties properties = multipleDataSourceProperties.getDatasource(dataSourceName);
        if (properties == null) {
            properties = multipleDataSourceProperties.getDatasource("default");
        }
        if (!StringUtils.hasText(properties.getName())) {
            properties.setName(dataSourceName);
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



    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
