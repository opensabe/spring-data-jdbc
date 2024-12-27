package io.github.mado.jdbc.core;

import io.github.mado.jdbc.core.repository.PersistentRepository;
import io.github.mado.jdbc.core.repository.QueryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.relational.core.query.Query;

import java.util.Optional;

/**
 * @author heng.ma
 */
public class QueryJdbcOperation<T> implements QueryRepository<T, Query>, PersistentRepository<T, Query> {

    private final JdbcAggregateTemplate jdbcAggregateTemplate;
    private final Class<T> entityClass;
    public QueryJdbcOperation(JdbcAggregateTemplate jdbcAggregateTemplate, Class<T> entityClass) {
        this.jdbcAggregateTemplate = jdbcAggregateTemplate;
        this.entityClass = entityClass;
    }


    @Override
    public Optional<T> findOne(Query query, Sort sort) {
        return jdbcAggregateTemplate.findOne(query.sort(sort), entityClass);
    }

    @Override
    public Iterable<T> findAll(Query query, Sort sort) {
        return jdbcAggregateTemplate.findAll(query.sort(sort), entityClass);
    }

    @Override
    public Page<T> findAll(Query query, Pageable pageable) {
        return jdbcAggregateTemplate.findAll(query, entityClass, pageable);
    }

    @Override
    public Iterable<T> findLimit(Query query, int limit, Sort sort) {
        return jdbcAggregateTemplate.findAll(query.limit(limit).sort(sort), entityClass);
    }

    @Override
    public long count(Query query) {
        return jdbcAggregateTemplate.count(query, entityClass);
    }

    @Override
    public int insertSelective(T entity) {
        return 0;
    }

    @Override
    public long insertList(Iterable<T> entities) {
        return 0;
    }

    @Override
    public long updateByIdSelective(T entity) {
        return 0;
    }

    @Override
    public long updateSelective(T updater, Query query) {
        return 0;
    }

    @Override
    public long deleteAll(Query query) {
        return 0;
    }
}
