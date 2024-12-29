package io.github.mado.jdbc.core.repository;

import io.github.mado.jdbc.core.executor.DefaultQueryJdbcOperation;
import io.github.mado.jdbc.core.executor.QueryJdbcOperation;
import io.github.mado.jdbc.core.lambda.Weekend;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.jdbc.repository.support.SimpleJdbcRepository;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.repository.query.RelationalExampleMapper;
import org.springframework.data.util.Lazy;

import java.util.Optional;

/**
 * @author heng.ma
 */
public class DefaultRepository<T, ID> extends SimpleJdbcRepository<T, ID> implements BaseRepository<T, ID> {

    private final Lazy<QueryJdbcOperation> queryJdbcOperation;

    private final RelationalExampleMapper exampleMapper;

    private final Class<T> entityClass;

    public DefaultRepository(JdbcAggregateOperations entityOperations, PersistentEntity<T, ?> entity, JdbcConverter converter) {
        super(entityOperations, entity, converter);
        this.exampleMapper = new RelationalExampleMapper(converter.getMappingContext());
        this.entityClass = entity.getType();
        this.queryJdbcOperation = Lazy.of(() -> DefaultQueryJdbcOperation.INSTANCE);
    }


    @Override
    public int insertSelective(T entity) {
        return queryJdbcOperation.get().insertSelective(entity, entityClass);
    }

    @Override
    public long insertList(Iterable<T> entities) {
        return queryJdbcOperation.get().insertList(entities, entityClass);
    }

    @Override
    public long updateByIdSelective(T entity) {
        return queryJdbcOperation.get().updateByIdSelective(entity, entityClass);
    }

    @Override
    public long updateSelective(T updater, Example<T> query) {
        return queryJdbcOperation.get().updateSelective(updater, toQuery(query), query.getProbeType());
    }

    @Override
    public long deleteAll(Example<T> query) {
        return queryJdbcOperation.get().deleteAll(toQuery(query), query.getProbeType());
    }

    @Override
    public Optional<T> findOne(Example<T> query, Sort sort) {
        return queryJdbcOperation.get().findOne(toQuery(query), sort, query.getProbeType());
    }

    @Override
    public Iterable<T> findLimit(Example<T> query, int limit, Sort sort) {
        return queryJdbcOperation.get().findLimit(toQuery(query), limit, sort, query.getProbeType());
    }

    @Override
    public long updateSelective(T updater, Weekend<T> query) {
        return queryJdbcOperation.get().updateSelective(updater, toQuery(query), query.getEntityClass());
    }

    @Override
    public long deleteAll(Weekend<T> query) {
        return queryJdbcOperation.get().deleteAll(toQuery(query), query.getEntityClass());
    }

    @Override
    public Optional<T> findOne(Weekend<T> query, Sort sort) {
        return queryJdbcOperation.get().findOne(toQuery(query), sort, query.getEntityClass());
    }

    @Override
    public Iterable<T> findAll(Weekend<T> query, Sort sort) {
        return queryJdbcOperation.get().findAll(toQuery(query), sort, query.getEntityClass());
    }

    @Override
    public Page<T> findAll(Weekend<T> query, Pageable pageable) {
        return queryJdbcOperation.get().findAll(toQuery(query), pageable, query.getEntityClass());
    }

    @Override
    public Iterable<T> findLimit(Weekend<T> query, int limit, Sort sort) {
        return queryJdbcOperation.get().findLimit(toQuery(query), limit, sort, query.getEntityClass());
    }

    @Override
    public long count(Weekend<T> query) {
        return queryJdbcOperation.get().count(toQuery(query), query.getEntityClass());
    }

    private Query toQuery (Example<T> example) {
        return exampleMapper.getMappedExample(example);
    }

    private Query toQuery (Weekend<T> weekend) {
        return weekend.toQuery();
    }

}
