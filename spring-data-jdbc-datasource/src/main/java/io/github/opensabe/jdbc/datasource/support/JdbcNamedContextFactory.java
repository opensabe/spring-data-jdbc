package io.github.opensabe.jdbc.datasource.support;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.context.named.NamedContextFactory;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.data.jdbc.core.convert.DataAccessStrategy;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.transaction.TransactionManager;

import javax.sql.DataSource;
import java.util.List;
import java.util.Objects;

/**
 * @author heng.ma
 */
public class JdbcNamedContextFactory extends NamedContextFactory<JdbcNamedContextFactory.Specification> {

    public static final String  propertySourceName = "spring.dynamic.datasource";
    public static final String  propertyName = propertySourceName+".name";

    public JdbcNamedContextFactory(List<Specification> specifications) {
        super(DefaultDataSourceConfiguration.class, propertySourceName, propertyName);
        if (Objects.nonNull(specifications) && !specifications.isEmpty()) {
            setConfigurations(specifications);
        }
    }


    public DataSource getDataSource (String name) {
        return getInstance(name, DataSource.class);
    }


    public DataAccessStrategy getDataAccessStrategy (String name) {
        return getInstance(name, DataAccessStrategy.class);
    }

    public NamedParameterJdbcOperations getNamedParameterJdbcOperations (String name) {
        return getInstance(name, NamedParameterJdbcOperations.class);
    }

    public TransactionManager getTransactionManager (String name) {
        return getInstance(name, TransactionManager.class);
    }
    public ObjectProvider<TransactionManager> getTransactionManagerProvider (String name) {
        return getProvider(name, TransactionManager.class);
    }

    public JdbcAggregateOperations getJdbcAggregateOperations (String name) {
        return getInstance(name, JdbcAggregateOperations.class);
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
            return "";
        }

        @Override
        public Class<?>[] getConfiguration() {
            return new Class[0];
        }
    }

}
