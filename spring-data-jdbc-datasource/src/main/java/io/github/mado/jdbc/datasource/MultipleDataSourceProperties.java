package io.github.mado.jdbc.datasource;

import com.zaxxer.hikari.HikariConfig;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

import java.util.Map;

/**
 * @author heng.ma
 */

public class MultipleDataSourceProperties {

    private Map<String, Properties> datasource;

    public Map<String, Properties> getDatasource() {
        return datasource;
    }

    public void setDatasource(Map<String, Properties> datasource) {
        this.datasource = datasource;
    }

    public Properties getDatasource (String name) {
        return datasource.get(name);
    }


    public static class Properties extends DataSourceProperties {

        private HikariConfig hikari;

        public Properties() {
            setGenerateUniqueName(false);
        }


        public HikariConfig getHikari() {
            return hikari;
        }

        public void setHikari(HikariConfig hikari) {
            this.hikari = hikari;
        }
    }
}
