package io.github.opensabe.jdbc.core;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jdbc.core.convert.DataAccessStrategy;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.jdbc.repository.QueryMappingConfiguration;
import org.springframework.data.jdbc.repository.support.JdbcRepositoryFactory;
import org.springframework.data.jdbc.repository.support.JdbcRepositoryFactoryBean;
import org.springframework.data.jdbc.repository.support.PagedJdbcQueryLookupStrategy;
import org.springframework.data.mapping.callback.EntityCallbacks;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.config.RepositoryConfigurationSource;
import org.springframework.data.repository.core.support.RepositoryFactoryCustomizer;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * operations, dataAccessStrategy
 * @author heng.ma
 */
@SuppressWarnings({"unused", "NullableProblems"})
public class ExtendRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable> extends JdbcRepositoryFactoryBean<T, S, ID> {

    private List<RepositoryFactoryCustomizer> repositoryFactoryCustomizers;

    private List<BeanFactoryCustomizer> beanFactoryCustomizers;

    private RepositoryConfigurationSource configSource;

    private RepositoryFactorySupportSupplier repositoryFactorySupportSupplier;

    private RepositoryFactoryBeanCustomizer factoryBeanCustomizer;


    public ExtendRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    public RepositoryConfigurationSource getConfigSource() {
        return configSource;
    }

    public void setConfigSource(RepositoryConfigurationSource configSource) {
        this.configSource = configSource;
    }

    @Autowired(required = false)
    public void setRepositoryFactoryCustomizers(List<RepositoryFactoryCustomizer> repositoryFactoryCustomizers) {
        this.repositoryFactoryCustomizers = repositoryFactoryCustomizers;
    }

    @Autowired(required = false)
    public void setBeanFactoryCustomizers(List<BeanFactoryCustomizer> beanFactoryCustomizers) {
        this.beanFactoryCustomizers = beanFactoryCustomizers;
    }

    @Autowired(required = false)
    public void setRepositoryFactorySupportSupplier(RepositoryFactorySupportSupplier repositoryFactorySupportSupplier) {
        this.repositoryFactorySupportSupplier = repositoryFactorySupportSupplier;
    }


    @Autowired(required = false)
    public void setFactoryBeanCustomizer(RepositoryFactoryBeanCustomizer factoryBeanCustomizer) {
        this.factoryBeanCustomizer = factoryBeanCustomizer;
    }

    /**
     * 自定义BeanFactory,在获取transaction时使用动态数据源
     * @param beanFactory owning BeanFactory (never {@code null}).
     * The bean can immediately call methods on the factory.
     */
    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        if (beanFactoryCustomizers != null) {
            for (BeanFactoryCustomizer customizer : beanFactoryCustomizers) {
                beanFactory = customizer.transform(beanFactory);
            }
        }
        super.setBeanFactory(beanFactory);
    }

    @Override
    protected RepositoryFactorySupport doCreateRepositoryFactory() {
        if (repositoryFactorySupportSupplier != null) {
            return repositoryFactorySupportSupplier.get(this);
        }
        DataAccessStrategy dataAccessStrategy = getFiled(DataAccessStrategy.class, "dataAccessStrategy");
        RelationalMappingContext mappingContext = getFiled(RelationalMappingContext.class, "mappingContext");
        JdbcConverter converter = getFiled(JdbcConverter.class, "converter");
        Dialect dialect = getFiled(Dialect.class, "dialect");
        ApplicationEventPublisher publisher = getFiled(ApplicationEventPublisher.class, "publisher");
        NamedParameterJdbcOperations operations = getFiled(NamedParameterJdbcOperations.class, "operations");
        QueryMappingConfiguration queryMappingConfiguration = getFiled(QueryMappingConfiguration.class, "queryMappingConfiguration");
        EntityCallbacks entityCallbacks = getFiled(EntityCallbacks.class, "entityCallbacks");
        BeanFactory beanFactory = getFiled(BeanFactory.class, "beanFactory");

        JdbcRepositoryFactory jdbcRepositoryFactory = new JdbcRepositoryFactory(dataAccessStrategy, mappingContext,
                converter, dialect, publisher, operations) {
            @Override
            protected Optional<QueryLookupStrategy> getQueryLookupStrategy(QueryLookupStrategy.Key key, QueryMethodEvaluationContextProvider evaluationContextProvider) {
                return super.getQueryLookupStrategy(key, evaluationContextProvider).map(s ->
                         (method, metadata, factory, namedQueries) -> {
                            try {
                                return s.resolveQuery(method, metadata, factory, namedQueries);
                            }catch (UnsupportedOperationException e) {
                                 return new PagedJdbcQueryLookupStrategy(publisher, entityCallbacks, mappingContext, converter, dialect, queryMappingConfiguration, operations, beanFactory,evaluationContextProvider)
                                         .resolveQuery(method, metadata, factory, namedQueries);
                            }
                        }
                );
            }
        };
        jdbcRepositoryFactory.setQueryMappingConfiguration(queryMappingConfiguration);
        jdbcRepositoryFactory.setEntityCallbacks(entityCallbacks);
        jdbcRepositoryFactory.setBeanFactory(beanFactory);

        return jdbcRepositoryFactory;
    }



    @SuppressWarnings("unchecked")
    private <E> E getFiled (Class<E> type, String name) {
        Field field = ReflectionUtils.findField(this.getClass(), name, type);
        try {
            Objects.requireNonNull(field).setAccessible(true);
            return (E)field.get(this);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterPropertiesSet() {
        addRepositoryFactoryCustomizer(new RepositoryFactoryCategoryCustomizer(getConfigSource()));
        if (factoryBeanCustomizer != null) {
            factoryBeanCustomizer.customize(this);
        }
        if (repositoryFactoryCustomizers != null) {
            repositoryFactoryCustomizers.forEach(this::addRepositoryFactoryCustomizer);
        }
        super.afterPropertiesSet();
    }
}
