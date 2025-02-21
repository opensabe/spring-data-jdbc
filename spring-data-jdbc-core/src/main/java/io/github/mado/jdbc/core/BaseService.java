package io.github.mado.jdbc.core;

import io.github.mado.jdbc.core.lambda.Weekend;
import io.github.mado.jdbc.core.repository.BaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.util.Lazy;
import org.springframework.data.util.TypeInformation;

import java.util.List;
import java.util.Optional;

/**
 * @author heng.ma
 */
public abstract class BaseService<T, ID> implements IService<T, ID> {

    private final Class<T> entityClass;

    private String tableName;

    private final Lazy<ArchiveService<T, ID>> archive;

    private BaseRepository<T, ID> repository;


    @SuppressWarnings("unchecked")
    public BaseService() {
        this.entityClass = (Class<T>) TypeInformation
                .of(this.getClass())
                .getSuperTypeInformation(BaseService.class)
                .getTypeArguments().get(0).getType();

        this.archive = Lazy.of(() -> new ArchiveService<>(getRepository(), tableName+"_his"));
    }

    @Autowired
    @SuppressWarnings("unused")
    public void setRelationalMappingContext(RelationalMappingContext relationalMappingContext) {
        this.tableName = relationalMappingContext.getRequiredPersistentEntity(entityClass).getQualifiedTableName().getReference();
    }

    public ArchiveService<T, ID> archive () {
        return archive.get();
    }

    @Autowired
    public void setRepository(BaseRepository<T, ID> repository) {
        this.repository = repository;
    }

    public BaseRepository<T, ID> getRepository() {
        return repository;
    }

    public Example<T> getExample (T entity) {
        return Example.of(entity, ExampleMatcher.matching().withIgnoreNullValues());
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
    public Optional<T> selectOne(Sort sort) {
        return repository.findOne(sort);
    }

    @Override
    public Optional<T> selectOne(T entity, Sort sort) {
        return repository.findOne(getExample(entity), sort);
    }

    @Override
    public Optional<T> selectOne(Weekend<T> weekend, Sort sort) {
        return repository.findOne(weekend, sort);
    }

    @Override
    public List<T> select() {
        return repository.findAll();
    }

    @Override
    public List<T> select(Sort sort) {
        return repository.findAll(sort);
    }

    @Override
    public List<T> select(T entity, Sort sort) {
        return repository.findAll(getExample(entity), sort);
    }

    @Override
    public List<T> select(Weekend<T> weekend, Sort sort) {
        return repository.findAll(weekend, sort);
    }

    @Override
    public Page<T> select(T entity, Pageable pageable) {
        return repository.findAll(getExample(entity), pageable);
    }

    @Override
    public Page<T> select(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Page<T> select(Weekend<T> weekend, Pageable pageable) {
        return repository.findAll(weekend, pageable);
    }

    @Override
    public List<T> selectByLimit(Weekend<T> weekend, int limit, Sort sort) {
        return repository.findLimit(weekend, limit, sort);
    }

    @Override
    public List<T> selectByLimit(T entity, int limit, Sort sort) {
        return repository.findLimit(getExample(entity), limit, sort);
    }

    @Override
    public List<T> selectByLimit(int limit, Sort sort) {
        return repository.findLimit(limit, sort);
    }

    @Override
    public boolean existsById(ID id) {
        return repository.existsById(id);
    }

    @Override
    public long count() {
        return repository.count();
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

    @Override
    public int insertSelective(T entity) {
        return repository.insertSelective(entity);
    }

    @Override
    public long insertList(List<T> entities) {
        return repository.insertList(entities);
    }

    @Override
    public T updateById(T entity) {
        return repository.updateById(entity);
    }

    @Override
    public int updateByIdSelective(T entity) {
        return repository.updateByIdSelective(entity);
    }

    @Override
    public long updateSelective(T entity, T query) {
        return repository.updateSelective(entity, getExample(query));
    }

    @Override
    public long updateSelective(T entity, Weekend<T> weekend) {
        return repository.updateSelective(entity, weekend);
    }

    @Override
    public int deleteById(ID id) {
        return repository.deleteById(id);
    }

    @Override
    public int deleteAllById(Iterable<ID> ids) {
        return repository.deleteAllById(ids);
    }

    @Override
    @SafeVarargs
    public final int deleteAllById(ID... ids) {
        return repository.deleteAllById(ids);
    }

    @Override
    public long deleteAll(T entity) {
        return repository.deleteAll(getExample(entity));
    }

    @Override
    public long deleteAll(Weekend<T> weekend) {
        return repository.deleteAll(weekend);
    }
}
