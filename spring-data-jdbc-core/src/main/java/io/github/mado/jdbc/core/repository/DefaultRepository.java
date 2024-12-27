package io.github.mado.jdbc.core.repository;

import io.github.mado.jdbc.core.QueryJdbcOperation;
import io.github.mado.jdbc.core.lambda.Weekend;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.jdbc.repository.support.SimpleJdbcRepository;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.repository.query.RelationalExampleMapper;

import java.util.Optional;

/**
 * @author heng.ma
 */
public class DefaultRepository<T, ID> extends SimpleJdbcRepository<T, ID> implements BaseRepository<T, ID> {

    private final QueryJdbcOperation<T> queryJdbcOperation;

    private final RelationalExampleMapper exampleMapper;

    public DefaultRepository(JdbcAggregateOperations entityOperations, PersistentEntity<T, ?> entity, JdbcConverter converter) {
        super(entityOperations, entity, converter);
        this.exampleMapper = new RelationalExampleMapper(converter.getMappingContext());
        if (entityOperations instanceof JdbcAggregateTemplate jdbcAggregateTemplate) {
            this.queryJdbcOperation = new QueryJdbcOperation<>(jdbcAggregateTemplate, entity.getType());
        }else {
            throw new IllegalArgumentException("entityOperations is not JdbcAggregateTemplate instance");
        }
    }


    @Override
    public int insertSelective(T entity) {
        return queryJdbcOperation.insertSelective(entity);
    }

    @Override
    public long insertList(Iterable<T> entities) {
        return queryJdbcOperation.insertList(entities);
    }

    @Override
    public long updateByIdSelective(T entity) {
        return queryJdbcOperation.updateByIdSelective(entity);
    }

    @Override
    public long updateSelective(T updater, Example<T> query) {
        return queryJdbcOperation.updateSelective(updater, toQuery(query));
    }

    @Override
    public long deleteAll(Example<T> query) {
        return queryJdbcOperation.deleteAll(toQuery(query));
    }

    @Override
    public Optional<T> findOne(Example<T> query, Sort sort) {
        return queryJdbcOperation.findOne(toQuery(query), sort);
    }

    @Override
    public Iterable<T> findLimit(Example<T> query, int limit, Sort sort) {
        return queryJdbcOperation.findLimit(toQuery(query), limit, sort);
    }

    @Override
    public long updateSelective(T updater, Weekend<T> query) {
        return queryJdbcOperation.updateSelective(updater, toQuery(query));
    }

    @Override
    public long deleteAll(Weekend<T> query) {
        return queryJdbcOperation.deleteAll(toQuery(query));
    }

    @Override
    public Optional<T> findOne(Weekend<T> query, Sort sort) {
        return queryJdbcOperation.findOne(toQuery(query), sort);
    }

    @Override
    public Iterable<T> findAll(Weekend<T> query, Sort sort) {
        return queryJdbcOperation.findAll(toQuery(query), sort);
    }

    @Override
    public Page<T> findAll(Weekend<T> query, Pageable pageable) {
        return queryJdbcOperation.findAll(toQuery(query), pageable);
    }

    @Override
    public Iterable<T> findLimit(Weekend<T> query, int limit, Sort sort) {
        return queryJdbcOperation.findLimit(toQuery(query), limit, sort);
    }

    @Override
    public long count(Weekend<T> query) {
        return queryJdbcOperation.count(toQuery(query));
    }

    private Query toQuery (Example<T> example) {
        return exampleMapper.getMappedExample(example);
    }

    private Query toQuery (Weekend<T> weekend) {
        return weekend.toQuery();
    }

}
