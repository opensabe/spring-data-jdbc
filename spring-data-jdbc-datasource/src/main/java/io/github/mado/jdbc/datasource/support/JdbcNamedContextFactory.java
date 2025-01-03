package io.github.mado.jdbc.datasource.support;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.context.named.NamedContextFactory;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.transaction.TransactionManager;

import javax.sql.DataSource;

/**
 * @author heng.ma
 */
public class JdbcNamedContextFactory extends NamedContextFactory<JdbcNamedContextFactory.Specification> {

    public static final String  propertySourceName = "spring.dynamic.datasource";
    public static final String  propertyName = propertySourceName+".name";

    public JdbcNamedContextFactory(Class<?> ... configuration) {
        super(DefaultDataSourceConfiguration.class, propertySourceName, propertyName);
    }


    public DataSource getDataSource (String name) {
        return getInstance(name, DataSource.class);
    }

    public JdbcTemplate jdbcTemplate (String name) {
        return getInstance(name, JdbcTemplate.class);
    }

    public JdbcOperations getJdbcOperations (String name) {
        return getInstance(name, JdbcOperations.class);
    }

    public NamedParameterJdbcOperations namedParameterJdbcOperations (String name) {
        return getInstance(name, NamedParameterJdbcOperations.class);
    }


    public RepositoryFactorySupport getRepositoryFactorySupport (String name) {
        return getInstance(name, RepositoryFactorySupport.class);
    }

    public TransactionManager getTransactionManager (String name) {
        return getInstance(name, TransactionManager.class);
    }
    public ObjectProvider<TransactionManager> getTransactionManagerProvider (String name) {
        return getProvider(name, TransactionManager.class);
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
