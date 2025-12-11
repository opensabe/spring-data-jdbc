package io.github.opensabe.jdbc.core;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.jdbc.core.convert.DataAccessStrategy;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.jdbc.core.convert.QueryMappingConfiguration;
import org.springframework.data.jdbc.repository.query.RowMapperFactory;
import org.springframework.data.jdbc.repository.support.*;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.config.RepositoryConfigurationSource;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.support.RepositoryComposition;
import org.springframework.data.repository.core.support.RepositoryFactoryCustomizer;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.query.CachingValueExpressionDelegate;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.ValueExpressionDelegate;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.List;
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



    private @Nullable ApplicationEventPublisher publisher;
    private @Nullable BeanFactory beanFactory;
    private @Nullable JdbcAggregateOperations aggregateOperations;
    private @Nullable JdbcConverter converter;
    private @Nullable DataAccessStrategy dataAccessStrategy;
    private @Nullable QueryMappingConfiguration queryMappingConfiguration;



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
        this.beanFactory = beanFactory;
    }

    @Override
    protected RepositoryFactorySupport doCreateRepositoryFactory() {
        if (repositoryFactorySupportSupplier != null) {
            return repositoryFactorySupportSupplier.get(this);
        }
        JdbcRepositoryFactory repositoryFactory;

        if (this.aggregateOperations != null) {
            repositoryFactory = new JdbcRepositoryFactoryWrapper(this.aggregateOperations, beanFactory, queryMappingConfiguration);
        } else {

            Assert.state(this.dataAccessStrategy != null, "DataAccessStrategy is required and must not be null");
            Assert.state(this.converter != null, "RelationalConverter is required and must not be null");

            JdbcAggregateOperations operations = new JdbcAggregateTemplate(converter, dataAccessStrategy);

            repositoryFactory = new JdbcRepositoryFactoryWrapper(operations, beanFactory, queryMappingConfiguration);
        }

//        repositoryFactory.setQueryMappingConfiguration(queryMappingConfiguration);
//        repositoryFactory.setBeanFactory(beanFactory);
        return repositoryFactory;
    }


    @Override
    public void setJdbcAggregateOperations(JdbcAggregateOperations jdbcAggregateOperations) {
        super.setJdbcAggregateOperations(jdbcAggregateOperations);
        this.aggregateOperations = jdbcAggregateOperations;
    }



    @Override
    public void setConverter(JdbcConverter converter) {
        super.setConverter(converter);
        this.converter = converter;
    }

    @Override
    public void setDataAccessStrategy(DataAccessStrategy dataAccessStrategy) {
        super.setDataAccessStrategy(dataAccessStrategy);
        this.dataAccessStrategy = dataAccessStrategy;
    }

    @Override
    public void setQueryMappingConfiguration(QueryMappingConfiguration queryMappingConfiguration) {
        super.setQueryMappingConfiguration(queryMappingConfiguration);
        this.queryMappingConfiguration = queryMappingConfiguration;
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

        if (this.queryMappingConfiguration == null) {
            if (this.beanFactory == null) {
                setQueryMappingConfiguration(QueryMappingConfiguration.EMPTY);
            } else {
                setQueryMappingConfiguration(beanFactory.getBeanProvider(QueryMappingConfiguration.class)
                        .getIfAvailable(() -> QueryMappingConfiguration.EMPTY));
            }
        }

        super.afterPropertiesSet();
    }

    public static class JdbcRepositoryFactoryWrapper extends JdbcRepositoryFactory {

        private final JdbcAggregateOperations operations;
        private final BeanFactory beanFactory;
        private final QueryMappingConfiguration queryMappingConfiguration;
        public JdbcRepositoryFactoryWrapper(JdbcAggregateOperations operations, BeanFactory beanFactory, QueryMappingConfiguration configuration) {
            super(operations);
            setBeanFactory(beanFactory);
            setQueryMappingConfiguration(configuration);
            this.operations = operations;
            this.beanFactory = beanFactory;
            this.queryMappingConfiguration = configuration;
        }

        @Override
        protected Object getTargetRepository(RepositoryInformation repositoryInformation) {
            Object repository = super.getTargetRepository(repositoryInformation);
            beanFactory.getBeanProvider(BeanPostProcessor.class).forEach(beanPostProcessor -> beanPostProcessor.postProcessAfterInitialization(repository, repositoryInformation.getRepositoryInterface().getName()));
            if (repository instanceof BeanFactoryAware beanFactoryAware) {
                beanFactoryAware.setBeanFactory(beanFactory);
            }
            return repository;
        }

        @Override
        public <T> T getRepository(Class<T> repositoryInterface, RepositoryComposition.RepositoryFragments fragments) {
            T repository = super.getRepository(repositoryInterface, fragments);
            beanFactory.getBeanProvider(BeanPostProcessor.class).forEach(beanPostProcessor -> beanPostProcessor.postProcessAfterInitialization(repository, repositoryInterface.getName()));
            return repository;
        }

        @Override
        protected Optional<QueryLookupStrategy> getQueryLookupStrategy(QueryLookupStrategy.@Nullable Key key, ValueExpressionDelegate valueExpressionDelegate) {
            RowMapperFactory rowMapperFactory = beanFactory != null
                    ? new BeanFactoryAwareRowMapperFactory(beanFactory, operations, queryMappingConfiguration)
                    : new DefaultRowMapperFactory(operations, queryMappingConfiguration);
            return super.getQueryLookupStrategy(key, valueExpressionDelegate).map(s ->
                    (method, metadata, factory, namedQueries) -> {
                        try {
                            return s.resolveQuery(method, metadata, factory, namedQueries);
                        }catch (UnsupportedOperationException e) {
                            return new PagedJdbcQueryLookupStrategy(operations, rowMapperFactory, new CachingValueExpressionDelegate(valueExpressionDelegate))
                                    .resolveQuery(method, metadata, factory, namedQueries);
                        }
                    });
        }

    }
}
