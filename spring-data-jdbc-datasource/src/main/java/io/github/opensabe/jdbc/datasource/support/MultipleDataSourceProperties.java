package io.github.opensabe.jdbc.datasource.support;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author heng.ma
 */
@ConfigurationProperties(prefix = MultipleDataSourceProperties.PREFIX)
public class MultipleDataSourceProperties implements EnvironmentAware, InitializingBean {

    public static final String PREFIX = "spring.dynamic";

    /**
     * Pool settings shared by every datasource unless it configures its own.
     */
    public static final String HIKARI_TEMPLATE_PREFIX = "spring.datasource.hikari";

    private Environment environment;

    private Map<String, List<Properties>> datasource;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    /**
     * Merge the shared pool settings right after binding, so every datasource is complete
     * before the named child contexts start building their own {@link javax.sql.DataSource}.
     */
    @Override
    public void afterPropertiesSet() {
        if (datasource == null || environment == null) {
            return;
        }
        HikariDataSource template = Binder.get(environment)
                .bind(HIKARI_TEMPLATE_PREFIX, Bindable.of(HikariDataSource.class))
                .orElse(null);
        if (template == null) {
            return;
        }
        datasource.values().stream()
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .forEach(property -> property.setHikari(HikariConfigMerger.mergeDefaults(property.getHikari(), template)));
    }


    public Properties defaultProperties (String name) {
        List<Properties> properties = datasource.get(name);
        if (properties == null || properties.isEmpty()) {
            properties = datasource.get("default");
        }
        if (properties != null) {
            for (Properties property : properties) {
                if (property.getHikari() == null || (!property.getHikari().isReadOnly())) {
                    return property;
                }
            }
        }
        return null;
    }
    public Properties readOnlyProperties (String name) {
        List<Properties> properties = datasource.get(name);
        if (properties == null || properties.isEmpty()) {
            properties = datasource.get("default");
        }
        if (properties != null) {
            for (Properties property : properties) {
                if (property.getHikari() != null && property.getHikari().isReadOnly()) {
                    return property;
                }
            }
        }
        return null;
    }

    public Map<String, List<Properties>> getDatasource() {
        return datasource;
    }

    public void setDatasource(Map<String, List<Properties>> datasource) {
        this.datasource = datasource;
    }

    public static class Properties extends DataSourceProperties {
        private HikariDataSource hikari;

        public HikariDataSource getHikari() {
            return hikari;
        }


        public void setHikari(HikariDataSource hikari) {
            this.hikari = hikari;
        }
    }
}
