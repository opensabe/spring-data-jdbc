package io.github.opensabe.jdbc.core;


import io.github.opensabe.jdbc.core.executor.DefaultJdbcRepository;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.repository.support.JdbcRepositoryFactoryBean;
import org.springframework.data.repository.config.DefaultRepositoryBaseClass;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import java.lang.annotation.*;

/**
 * @author heng.ma
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(JdbcRepositoriesRegistrar.class)
public @interface EnableJdbcRepositories {

    /**
     * Returns the {@link FactoryBean} class to be used for each repository instance. Defaults to
     * {@link JdbcRepositoryFactoryBean}.
     */
//    @AliasFor(annotation = org.springframework.data.jdbc.repository.config.EnableJdbcRepositories.class)
    Class<?> repositoryFactoryBeanClass() default ExtendRepositoryFactoryBean.class;


    /**
     * Configure the repository base class to be used to create repository proxies for this particular configuration.
     *
     * @since 2.1
     */
//    @AliasFor(annotation = org.springframework.data.jdbc.repository.config.EnableJdbcRepositories.class)
    Class<?> repositoryBaseClass() default DefaultJdbcRepository.class;


    String name () default "default";















    /**
     * Alias for the {@link #basePackages()} attribute. Allows for more concise annotation declarations e.g.:
     * {@code @EnableJdbcRepositories("org.my.pkg")} instead of
     * {@code @EnableJdbcRepositories(basePackages="org.my.pkg")}.
     */
    String[] value() default {};

    /**
     * Base packages to scan for annotated components.
     * <p>
     * {@link #value} is an alias for (and mutually exclusive with) this attribute.
     * <p>
     * Supports {@code ${…}} placeholders which are resolved against the {@link org.springframework.core.env.Environment
     * Environment} as well as Ant-style package patterns &mdash; for example, {@code "org.example.**"}.
     * <p>
     * Multiple packages or patterns may be specified, either separately or within a single {@code String} &mdash; for
     * example, {@code {"org.example.config", "org.example.service.**"}} or
     * {@code "org.example.config, org.example.service.**"}.
     * <p>
     * Use {@link #basePackageClasses} for a type-safe alternative to String-based package names.
     *
     * @see org.springframework.context.ConfigurableApplicationContext#CONFIG_LOCATION_DELIMITERS
     */
    String[] basePackages() default {};

    /**
     * Type-safe alternative to {@link #basePackages()} for specifying the packages to scan for annotated components. The
     * package of each class specified will be scanned. Consider creating a special no-op marker class or interface in
     * each package that serves no purpose other than being referenced by this attribute.
     */
    Class<?>[] basePackageClasses() default {};

    /**
     * Specifies which types are eligible for component scanning. Further narrows the set of candidate components from
     * everything in {@link #basePackages()} to everything in the base packages that matches the given filter or filters.
     */
    ComponentScan.Filter[] includeFilters() default {};

    /**
     * Specifies which types are not eligible for component scanning.
     */
    ComponentScan.Filter[] excludeFilters() default {};

    /**
     * Returns the postfix to be used when looking up custom repository implementations. Defaults to {@literal Impl}. So
     * for a repository named {@code PersonRepository} the corresponding implementation class will be looked up scanning
     * for {@code PersonRepositoryImpl}.
     */
    String repositoryImplementationPostfix() default "Impl";

    /**
     * Configures the location of where to find the Spring Data named queries properties file. Will default to
     * {@code META-INF/jdbc-named-queries.properties}.
     */
    String namedQueriesLocation() default "";

    /**
     * Returns the key of the {@link QueryLookupStrategy} to be used for lookup queries for query methods. Defaults to
     * {@link QueryLookupStrategy.Key#CREATE_IF_NOT_FOUND}.
     *
     * @since 2.1
     */
    QueryLookupStrategy.Key queryLookupStrategy() default QueryLookupStrategy.Key.CREATE_IF_NOT_FOUND;

    /**
     * Configure a specific {@link BeanNameGenerator} to be used when creating the repository beans.
     * @return the {@link BeanNameGenerator} to be used or the base {@link BeanNameGenerator} interface to indicate context default.
     * @since 3.4
     */
    Class<? extends BeanNameGenerator> nameGenerator() default BeanNameGenerator.class;

    /**
     * Configures whether nested repository-interfaces (e.g. defined as inner classes) should be discovered by the
     * repositories infrastructure.
     */
    boolean considerNestedRepositories() default false;

    // JDBC-specific configuration

    /**
     * Configure the name of the {@link org.springframework.data.jdbc.core.JdbcAggregateOperations} bean to be used to
     * create repositories discovered through this annotation.
     *
     * @since 4.0
     */
    String jdbcAggregateOperationsRef() default "";

    /**
     * Configures the name of the {@link org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations} bean to be
     * used to create repositories discovered through this annotation. Defaults to {@code namedParameterJdbcTemplate}.
     *
     * @deprecated since 4.0 use {@link #jdbcAggregateOperationsRef()} instead to ensure consistent configuration
     *             repositories.
     */
    @Deprecated(since = "4.0")
    String jdbcOperationsRef() default "";

    /**
     * Configures the name of the {@link org.springframework.data.jdbc.core.convert.DataAccessStrategy} bean to be used to
     * create repositories discovered through this annotation. Defaults to {@code defaultDataAccessStrategy}.
     *
     * @deprecated since 4.0 use {@link #jdbcAggregateOperationsRef()} instead to ensure consistent configuration
     *             repositories.
     */
    @Deprecated(since = "4.0")
    String dataAccessStrategyRef() default "";

    /**
     * Configures the name of the {@link PlatformTransactionManager} bean to be used to create repositories discovered
     * through this annotation. Defaults to {@code transactionManager}.
     *
     * @since 2.1
     */
    String transactionManagerRef() default "transactionManager";

    /**
     * Configures whether to enable default transactions for Spring Data JDBC repositories. Defaults to {@literal true}.
     * If disabled, repositories must be used behind a facade that's configuring transactions (e.g. using Spring's
     * annotation driven transaction facilities) or repository methods have to be used to demarcate transactions.
     *
     * @return whether to enable default transactions, defaults to {@literal true}.
     * @since 4.0
     */
    boolean enableDefaultTransactions() default true;

}
