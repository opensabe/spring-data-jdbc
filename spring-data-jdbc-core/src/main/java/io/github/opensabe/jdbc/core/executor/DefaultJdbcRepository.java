package io.github.opensabe.jdbc.core.executor;

import io.github.opensabe.jdbc.core.ApplicationContextHolder;
import io.github.opensabe.jdbc.core.lambda.Weekend;
import io.github.opensabe.jdbc.core.repository.BaseRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
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

    private final boolean unionkey;

    public DefaultJdbcRepository(JdbcAggregateOperations entityOperations, PersistentEntity<T, ?> entity,
                                 //代理创建对象时参数定死了，参数个数、类型、顺序都不能变
                                 @SuppressWarnings("unused") JdbcConverter converter) {
        this.operations = entityOperations;
        this.clazz = entity.getType();
        this.exampleMapper = Lazy.of(() -> ApplicationContextHolder.getBean(RelationalExampleMapper.class));
        this.criteriaJdbcOperation = Lazy.of(() -> ApplicationContextHolder.getBean(CustomerJdbcOperation.class));
        this.unionkey = !entity.hasIdProperty() || entity.getRequiredIdProperty().getType().isAssignableFrom(clazz);
    }




    private Query toQuery (Example<T> example) {
        return exampleMapper.get().getMappedExample(example);
    }
    private Query toObjectQuery (Example<?> example) {
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
    public List<T> findLimit(int limit, Sort sort) {
        List<T> list = new ArrayList<>(limit);
        list.addAll(operations.findAll(Query.empty().limit(limit).sort(sort), clazz));
        return list;
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
    public List<T> findAll(Sort sort) {
        return new ArrayList<>(operations.findAll(clazz, sort));
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
    public Page<T> findAll(Pageable pageable) {
        return operations.findAll(Query.empty(), clazz, pageable);
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
    public T updateById(T entity) {
        return operations.update(entity);
    }

    @Override
    public int updateByIdSelective(T entity) {
        if (unionkey) {
            Class<?> superclass = entity.getClass().getSuperclass();
            if (!Object.class.equals(superclass)) {
                try {
                    Object id = superclass.getConstructor().newInstance();
                    BeanUtils.copyProperties(entity, id);
                    var example = Example.of(id, ExampleMatcher.matching().withIgnoreNullValues());
                    return Long.valueOf(criteriaJdbcOperation.get().updateSelective(entity, toObjectQuery(example), clazz)).intValue();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
        }
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
        if (unionkey) {
            var example = Example.of(id, ExampleMatcher.matching().withIgnoreNullValues());
            return criteriaJdbcOperation.get().findOne(toObjectQuery(example), clazz);
        }
        return Optional.ofNullable(operations.findById(id, clazz));
    }

    @Override
    public Optional<T> findOne(Sort sort) {
        return operations.findOne(Query.empty().limit(1).sort(sort), clazz);
    }

    @Override
    public boolean existsById(ID id) {
        if (unionkey) {
            var example = Example.of(id, ExampleMatcher.matching().withIgnoreNullValues());
            return criteriaJdbcOperation.get().exists(toObjectQuery(example), clazz);
        }
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
        if (unionkey) {
            throw new UnsupportedOperationException("find by ids not supported for union key");
        }
        return toList(operations.findAllById(ids, clazz));
    }

    @Override
    public long count() {
        return operations.count(clazz);
    }

    @Override
    public int deleteById(ID id) {
        if (unionkey) {
            try {
                T instance = clazz.getConstructor().newInstance();
                BeanUtils.copyProperties(id, instance);
                var example = Example.of(instance, ExampleMatcher.matching().withIgnoreNullValues());
                return Long.valueOf(deleteAll(example)).intValue();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
        return criteriaJdbcOperation.get().deleteById(id, clazz);
    }


    @Override
    public int deleteAllById(Iterable<ID> ids) {
        if (unionkey) {
            throw new UnsupportedOperationException("delete by ids not supported for union key");
        }
        return criteriaJdbcOperation.get().deleteAllById(ids, clazz);
    }

    @Override
    @SuppressWarnings("unchecked")
    public int deleteAllById(ID... ids) {
        if (unionkey) {
            throw new UnsupportedOperationException("delete by ids not supported for union key");
        }
        return criteriaJdbcOperation.get().deleteAllById(ids, clazz);
    }


    @Override
    public long deleteAll() {
        return criteriaJdbcOperation.get().deleteAll(clazz);
    }

    @Override
    public Optional<T> findById(ID id, String table) {
        return criteriaJdbcOperation.get().findById(id, clazz, table);
    }

    @Override
    public List<T> findAllById(Iterable<ID> ids, String table) {
        if (unionkey) {
            throw new UnsupportedOperationException("find by ids not supported for union key");
        }
        return criteriaJdbcOperation.get().findAllById(ids, clazz, table);
    }

    @Override
    public List<T> findAll(Example<T> example, String table) {
        return criteriaJdbcOperation.get().findAll(toQuery(example), example.getProbeType(), table);
    }

    @Override
    public List<T> findAll(Example<T> example, Sort sort, String table) {
        return criteriaJdbcOperation.get().findAll(toQuery(example).sort(sort), example.getProbeType(), table);
    }

    @Override
    public List<T> findAll(String table) {
        return criteriaJdbcOperation.get().findAll(Query.empty(), clazz, table);
    }

    @Override
    public List<T> findAll(Sort sort, String table) {
        return criteriaJdbcOperation.get().findAll(Query.empty().sort(sort), clazz, table);
    }

    @Override
    public List<T> findAll(Weekend<T> weekend, String table) {
        return criteriaJdbcOperation.get().findAll(toQuery(weekend), weekend.getEntityClass(), table);
    }

    @Override
    public List<T> findAll(Weekend<T> weekend, Sort sort, String table) {
        return criteriaJdbcOperation.get().findAll(toQuery(weekend).sort(sort), weekend.getEntityClass(), table);
    }

    @Override
    public Page<T> findAll(Pageable pageable, String table) {
        return criteriaJdbcOperation.get().findAll(Query.empty(), pageable, clazz, table);
    }

    @Override
    public Page<T> findAll(Weekend<T> weekend, Pageable pageable, String table) {
        return criteriaJdbcOperation.get().findAll(toQuery(weekend), pageable, weekend.getEntityClass(), table);
    }

    @Override
    public Page<T> findAll(Example<T> example, Pageable pageable, String table) {
        return criteriaJdbcOperation.get().findAll(toQuery(example), pageable, example.getProbeType(), table);
    }

    @Override
    public List<T> findLimit(int limit, Sort sort, String table) {
        return criteriaJdbcOperation.get().findAll(Query.empty().limit(limit).sort(sort), clazz, table);
    }

    @Override
    public List<T> findLimit(Weekend<T> weekend, int limit, Sort sort, String table) {
        return criteriaJdbcOperation.get().findAll(toQuery(weekend).limit(limit).sort(sort), weekend.getEntityClass(), table);
    }

    @Override
    public List<T> findLimit(Example<T> example, int limit, Sort sort, String table) {
        return criteriaJdbcOperation.get().findAll(toQuery(example).limit(limit).sort(sort), example.getProbeType(), table);
    }

    @Override
    public boolean existsById(ID id, String table) {
        return criteriaJdbcOperation.get().existsById(id, clazz, table);
    }

    @Override
    public boolean exists(Weekend<T> weekend, String table) {
        return criteriaJdbcOperation.get().exists(toQuery(weekend), weekend.getEntityClass(), table);
    }

    @Override
    public boolean exists(Example<T> example, String table) {
        return criteriaJdbcOperation.get().exists(toQuery(example), example.getProbeType(), table);
    }

    @Override
    public long count(Weekend<T> weekend, String table) {
        return criteriaJdbcOperation.get().count(toQuery(weekend), weekend.getEntityClass(), table);
    }

    @Override
    public long count(Example<T> example, String table) {
        return criteriaJdbcOperation.get().count(toQuery(example), example.getProbeType(), table);
    }
    @Override
    public long count(String table) {
        return criteriaJdbcOperation.get().count(Query.empty(), clazz, table);
    }
}
