package io.github.mado.jdbc.core;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jdbc.repository.support.JdbcRepositoryFactoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.config.RepositoryConfigurationSource;
import org.springframework.data.repository.core.support.RepositoryFactoryCustomizer;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import java.io.Serializable;
import java.util.List;

/**
 * operations, dataAccessStrategy
 * @author heng.ma
 */
public class ExtendRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable> extends JdbcRepositoryFactoryBean<T, S, ID> {

    private List<RepositoryFactoryCustomizer> repositoryFactoryCustomizers;

    private List<BeanFactoryCustomizer> beanFactoryCustomizers;

    private RepositoryConfigurationSource configSource;

    private RepositoryFactorySupportSupplier repositoryFactorySupportSupplier;

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
        return super.doCreateRepositoryFactory();
    }

    @Override
    public void afterPropertiesSet() {
        addRepositoryFactoryCustomizer(new RepositoryFactoryCategoryCustomizer(getConfigSource()));
        if (repositoryFactoryCustomizers != null) {
            repositoryFactoryCustomizers.forEach(this::addRepositoryFactoryCustomizer);
        }
        super.afterPropertiesSet();
    }
}
