package io.github.mado.jdbc.core.executor;

import io.github.mado.jdbc.core.ApplicationContextHolder;
import io.github.mado.jdbc.core.lambda.Weekend;
import io.github.mado.jdbc.core.repository.BaseRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.repository.query.RelationalExampleMapper;
import org.springframework.data.util.Lazy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author heng.mai
 */
public class DefaultJdbcRepository<T, ID>  implements BaseRepository<T, ID> {

    private final Lazy<CustomerJdbcOperation> criteriaJdbcOperation;
    private final Lazy<RelationalExampleMapper> exampleMapper;

    private final JdbcAggregateOperations operations;

    private final Class<T> clazz;

    public DefaultJdbcRepository(JdbcAggregateOperations entityOperations, PersistentEntity<T, ?> entity, JdbcConverter converter) {
        this.operations = entityOperations;
        this.clazz = entity.getType();
        this.exampleMapper = Lazy.of(() -> ApplicationContextHolder.getBean(RelationalExampleMapper.class));
        this.criteriaJdbcOperation = Lazy.of(() -> ApplicationContextHolder.getBean(CustomerJdbcOperation.class));
    }




    private Query toQuery (Example<T> example) {
        return exampleMapper.get().getMappedExample(example);
    }

    private Query toQuery (Weekend<T> weekend) {
        return weekend.toQuery();
    }

    private <S extends T> List<S> toList (Iterable<S> iterable) {
        List<S> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }


    @Override
    public long count(Example<T> example) {
        return operations.count(toQuery(example), example.getProbeType());
    }

    @Override
    public long count(Weekend<T> weekend) {
        return operations.count(toQuery(weekend), weekend.getEntityClass());
    }

    @Override
    public long deleteAll(Example<T> example) {
        return criteriaJdbcOperation.get().deleteAll(toQuery(example), example.getProbeType());
    }

    @Override
    public long deleteAll(Example<T> example, int limit) {
        return criteriaJdbcOperation.get().deleteAll(toQuery(example).limit(limit),example.getProbeType());
    }

    @Override
    public long deleteAll(Weekend<T> weekend) {
        return criteriaJdbcOperation.get().deleteAll(toQuery(weekend), weekend.getEntityClass());
    }

    @Override
    public long deleteAll(Weekend<T> weekend, int limit) {
        return criteriaJdbcOperation.get().deleteAll(toQuery(weekend).limit(limit), weekend.getEntityClass());
    }

    @Override
    public int insertSelective(T entity) {
        return criteriaJdbcOperation.get().insertSelective(entity, clazz);
    }

    @Override
    public long insertList(Iterable<T> entities) {
        return criteriaJdbcOperation.get().insertList(entities, clazz);
    }

    @Override
    public List<T> findLimit(Weekend<T> weekend, int limit, Sort sort) {
        return criteriaJdbcOperation.get().findAll(toQuery(weekend).limit(limit).sort(sort), weekend.getEntityClass());
    }

    @Override
    public List<T> findLimit(Example<T> example, int limit, Sort sort) {
        return criteriaJdbcOperation.get().findAll(toQuery(example).limit(limit).sort(sort), example.getProbeType());
    }

    @Override
    public List<T> findAll(Weekend<T> weekend) {
        return criteriaJdbcOperation.get().findAll(toQuery(weekend), weekend.getEntityClass());
    }

    @Override
    public List<T> findAll(Weekend<T> weekend, Sort sort) {
        return criteriaJdbcOperation.get().findAll(toQuery(weekend).sort(sort), weekend.getEntityClass());
    }

    @Override
    public List<T> findAll(Example<T> example) {
        return criteriaJdbcOperation.get().findAll(toQuery(example), example.getProbeType());
    }

    @Override
    public List<T> findAll(Example<T> example, Sort sort) {
        return criteriaJdbcOperation.get().findAll(toQuery(example).sort(sort), example.getProbeType());
    }

    @Override
    public Optional<T> findOne(Example<T> example) {
        return criteriaJdbcOperation.get().findOne(toQuery(example), example.getProbeType());
    }

    @Override
    public Optional<T> findOne(Example<T> example, Sort sort) {
        return criteriaJdbcOperation.get().findOne(toQuery(example).sort(sort), example.getProbeType());
    }

    @Override
    public Optional<T> findOne(Weekend<T> weekend) {
        return criteriaJdbcOperation.get().findOne(toQuery(weekend), weekend.getEntityClass());
    }

    @Override
    public Optional<T> findOne(Weekend<T> weekend, Sort sort) {
        return criteriaJdbcOperation.get().findOne(toQuery(weekend).sort(sort), weekend.getEntityClass());
    }

    @Override
    public Page<T> findAll(Weekend<T> weekend, Pageable pageable) {
        return criteriaJdbcOperation.get().findAll(toQuery(weekend), pageable, weekend.getEntityClass());
    }

    @Override
    public Page<T> findAll(Example<T> example, Pageable pageable) {
        return criteriaJdbcOperation.get().findAll(toQuery(example), pageable, example.getProbeType());
    }

    @Override
    public int updateByIdSelective(T entity) {
        return criteriaJdbcOperation.get().updateByIdSelective(entity, clazz);
    }

    @Override
    public long updateSelective(T entity, Weekend<T> weekend) {
        return criteriaJdbcOperation.get().updateSelective(entity, toQuery(weekend), weekend.getEntityClass());
    }

    @Override
    public long updateSelective(T entity, Weekend<T> weekend, int limit) {
        return criteriaJdbcOperation.get().updateSelective(entity, toQuery(weekend).limit(limit), weekend.getEntityClass());
    }

    @Override
    public long updateSelective(T entity, Example<T> example) {
        return criteriaJdbcOperation.get().updateSelective(entity, toQuery(example), example.getProbeType());
    }

    @Override
    public long updateSelective(T entity, Example<T> example, int limit) {
        return criteriaJdbcOperation.get().updateSelective(entity, toQuery(example).limit(limit), example.getProbeType());
    }



    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(operations.findById(id, clazz));
    }

    @Override
    public boolean existsById(ID id) {
        return operations.existsById(id, clazz);
    }

    @Override
    public boolean exists(Weekend<T> weekend) {
        return criteriaJdbcOperation.get().exists(toQuery(weekend), weekend.getEntityClass());
    }

    @Override
    public boolean exists(Example<T> example) {
        return criteriaJdbcOperation.get().exists(toQuery(example), example.getProbeType());
    }

    @Override
    public List<T> findAll() {
        return toList(operations.findAll(clazz));
    }

    @Override
    public List<T> findAllById(Iterable<ID> ids) {
        return toList(operations.findAllById(ids, clazz));
    }

    @Override
    public long count() {
        return operations.count(clazz);
    }

    @Override
    public int deleteById(ID id) {
        return criteriaJdbcOperation.get().deleteById(id, clazz);
    }


    @Override
    public int deleteAllById(Iterable<ID> ids) {
        return criteriaJdbcOperation.get().deleteAllById(ids, clazz);
    }

    @Override
    public int deleteAllById(ID... ids) {
        return criteriaJdbcOperation.get().deleteAllById(ids, clazz);
    }


    @Override
    public long deleteAll() {
        return criteriaJdbcOperation.get().deleteAll(clazz);
    }
}
