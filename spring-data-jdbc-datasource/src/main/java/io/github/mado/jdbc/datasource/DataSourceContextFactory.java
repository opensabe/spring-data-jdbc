package io.github.mado.jdbc.datasource;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.cloud.context.named.NamedContextFactory;
import org.springframework.data.util.Lazy;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author heng.ma
 */
public class DataSourceContextFactory extends NamedContextFactory<DataSourceContextFactory.Specification> {

    private final static String propertySourceName = "spring.datasource";
    public final static String propertyName = propertySourceName+".name";

    private final Lazy<DataSource> readOnlyDataSource;

    public DataSourceContextFactory(List<Specification> specifications) {
        super(DataSourceDefaultConfiguration.class, propertySourceName, propertyName);
        if (specifications != null && !specifications.isEmpty()) {
            setConfigurations(specifications);
        }

        this.readOnlyDataSource = Lazy.of(() -> getContextNames().stream()
                .map(this::hikariDataSource)
                .filter(HikariDataSource::isReadOnly)
                .findFirst().orElseThrow());
    }


    public HikariDataSource hikariDataSource (String name) {
        return getInstance(name, HikariDataSource.class);
    }

    public Lazy<DataSource> getReadOnlyDataSource () {
        return readOnlyDataSource;
    }








    public static class Specification implements NamedContextFactory.Specification {


        private final String name;
        private final Class<?>[] configuration;

        public Specification(String name, Class<?> ... configuration) {
            this.name = name;
            this.configuration = configuration;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Class<?>[] getConfiguration() {
            return configuration;
        }
    }
}
