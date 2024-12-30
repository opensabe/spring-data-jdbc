package io.github.mado.jdbc.core;

import io.github.mado.jdbc.core.lambda.Fn;
import io.github.mado.jdbc.core.lambda.Weekend;
import io.github.mado.jdbc.core.repository.BaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.util.TypeInformation;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author heng.ma
 */
public abstract class BaseService<T, ID> implements IService<T, ID> {

    private final Class<T> entityClass;

    @Autowired
    private BaseRepository<T, ID> repository;

    @SuppressWarnings("unchecked")
    public BaseService() {
        this.entityClass = (Class<T>) TypeInformation
                .of(this.getClass())
                .getSuperTypeInformation(BaseService.class)
                .getTypeArguments().get(0).getType();
    }

    public BaseRepository<T, ID> getRepository() {
        return repository;
    }

    @Override
    public Optional<T> selectById(ID id) {
        return repository.findById(id);
    }

    @Override
    public List<T> selectByIds(List<ID> ids) {
        return repository.findAllById(ids);
    }

    @Override
    public T selectInstanceById(ID id) {
        return selectById(id).orElseThrow(NullPointerException::new);
    }

    @Override
    public Optional<T> selectOne(T entity) {
        return repository.findOne(getExample(entity));
    }

    @Override
    public Optional<T> selectOne(T entity, Sort sort) {
        return repository.findOne(getExample(entity), sort);
    }

    @Override
    public Optional<T> selectOne(T entity, Sort.Direction order, Fn<T, Object>... properties) {
        return repository.findOne(getExample(entity), SortUtils.formArray(order, properties));
    }

    @Override
    public Optional<T> selectOne(T entity, Map<Fn<T, Object>, Sort.Direction> sort) {
        return repository.findOne(getExample(entity), SortUtils.formMap(sort));
    }

    @Override
    public Optional<T> selectOne(Weekend<T> weekend) {
        return repository.findOne(weekend);
    }

    @Override
    public Optional<T> selectOne(Weekend<T> weekend, Sort sort) {
        return repository.findOne(weekend, sort);
    }

    @Override
    public Optional<T> selectOne(Weekend<T> weekend, Sort.Direction order, Fn<T, Object>... properties) {
        return repository.findOne(weekend, SortUtils.formArray(order, properties));
    }

    @Override
    public Optional<T> selectOne(Weekend<T> weekend, Map<Fn<T, Object>, Sort.Direction> sort) {
        return repository.findOne(weekend, SortUtils.formMap(sort));
    }

    @Override
    public List<T> select(T entity) {
        return repository.findAll(getExample(entity));
    }

    @Override
    public List<T> select(T entity, Sort.Direction direction, Fn<T, Object>... order) {
        return repository.findAll(getExample(entity), SortUtils.formArray(direction, order));
    }

    @Override
    public List<T> select(T entity, Map<Fn<T, Object>, Sort.Direction> sort) {
        return repository.findAll(getExample(entity), SortUtils.formMap(sort));
    }

    @Override
    public List<T> select(Weekend<T> weekend) {
        return repository.findAll(weekend);
    }

    @Override
    public List<T> select(Weekend<T> weekend, Sort.Direction direction, Fn<T, Object>... order) {
        return repository.findAll(weekend, SortUtils.formArray(direction, order));
    }

    @Override
    public List<T> select(Weekend<T> weekend, Map<Fn<T, Object>, Sort.Direction> sort) {
        return repository.findAll(weekend, SortUtils.formMap(sort));
    }

    @Override
    public Page<T> select(T entity, Pageable pageable) {
        return repository.findAll(getExample(entity), pageable);
    }

    @Override
    public Page<T> select(T entity, int pageNum, int pageSize) {
        return repository.findAll(getExample(entity), PageRequest.of(pageNum-1, pageSize));
    }

    @Override
    public Page<T> select(T entity, int pageNum, int pageSize, Sort.Direction order, Fn<T, Object>... properties) {
        return repository.findAll(getExample(entity), PageRequest.of(pageNum-1, pageSize).withSort(SortUtils.formArray(order, properties)));
    }

    @Override
    public Page<T> select(T entity, int pageNum, int pageSize, Map<Fn<T, Object>, Sort.Direction> order) {
        return repository.findAll(getExample(entity), PageRequest.of(pageNum-1, pageSize).withSort(SortUtils.formMap(order)));
    }

    @Override
    public Page<T> select(Weekend<T> weekend, int pageNum, int pageSize, Sort.Direction order, Fn<T, Object>... properties) {
        return repository.findAll(weekend, PageRequest.of(pageNum-1, pageSize).withSort(SortUtils.formArray(order, properties)));
    }

    @Override
    public Page<T> select(Weekend<T> weekend, int pageNum, int pageSize, Map<Fn<T, Object>, Sort.Direction> order) {
        return repository.findAll(weekend, PageRequest.of(pageNum-1, pageSize).withSort(SortUtils.formMap(order)));
    }

    @Override
    public List<T> selectByLimit(Weekend<T> weekend, int limit) {
        return repository.findLimit(weekend, limit, Sort.unsorted());
    }

    @Override
    public List<T> selectByLimit(Weekend<T> weekend, int limit, Sort.Direction order, Fn<T, Object>... properties) {
        return repository.findLimit(weekend, limit, SortUtils.formArray(order, properties));
    }

    @Override
    public List<T> selectByLimit(Weekend<T> weekend, int limit, Map<Fn<T, Object>, Sort.Direction> order) {
        return repository.findLimit(weekend, limit, SortUtils.formMap(order));
    }

    @Override
    public List<T> selectByLimit(T entity, int limit) {
        return repository.findLimit(getExample(entity), limit, Sort.unsorted());
    }

    @Override
    public List<T> selectByLimit(T entity, int limit, Sort.Direction order, Fn<T, Object>... properties) {
        return repository.findLimit(getExample(entity), limit, SortUtils.formArray(order, properties));
    }

    @Override
    public List<T> selectByLimit(T entity, int limit, Map<Fn<T, Object>, Sort.Direction> order) {
        return repository.findLimit(getExample(entity), limit, SortUtils.formMap(order));
    }

    @Override
    public boolean existsById(ID id) {
        return repository.existsById(id);
    }

    @Override
    public long count(T entity) {
        return repository.count(getExample(entity));
    }

    @Override
    public long count(Weekend<T> weekend) {
        return repository.count(weekend);
    }

    @Override
    public boolean exists(T entity) {
        return repository.exists(getExample(entity));
    }

    @Override
    public boolean exists(Weekend<T> weekend) {
        return repository.exists(weekend);
    }

    public Example<T> getExample (T entity) {
        return Example.of(entity, ExampleMatcher.matching().withIgnoreNullValues());
    }
}
