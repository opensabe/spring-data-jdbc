package io.github.mado.jdbc.datasource.support;

import com.zaxxer.hikari.HikariConfig;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

/**
 * @author heng.ma
 */
@ConfigurationProperties(prefix = MultipleDataSourceProperties.PREFIX)
public class MultipleDataSourceProperties {

    public static final String PREFIX = "spring.dynamic";

    private Map<String, List<Properties>> datasource;


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
        private HikariConfig hikari;

        public HikariConfig getHikari() {
            return hikari;
        }

        public void setHikari(HikariConfig hikari) {
            this.hikari = hikari;
        }
    }
}
