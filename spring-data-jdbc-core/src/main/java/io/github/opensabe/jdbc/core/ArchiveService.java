package io.github.opensabe.jdbc.core;

import io.github.opensabe.jdbc.core.lambda.Weekend;
import io.github.opensabe.jdbc.core.repository.BaseRepository;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

/**
 * @author heng.ma
 */
public class ArchiveService<T, ID> implements QueryService<T, ID> {

    private final BaseRepository<T, ID> repository;
    private final String table;

    public ArchiveService(BaseRepository<T, ID> repository, String table) {
        this.repository = repository;
        this.table = table;
    }

    private Example<T> getExample (T entity) {
        return Example.of(entity, ExampleMatcher.matching().withIgnoreNullValues());
    }

    @Override
    public Optional<T> selectById(ID id) {
        return repository.findById(id, table);
    }

    @Override
    public List<T> selectByIds(List<ID> ids) {
        return repository.findAllById(ids, table);
    }

    @Override
    public Optional<T> selectOne(Sort sort) {
        return repository.findOne(sort, table);
    }

    @Override
    public Optional<T> selectOne(T entity, Sort sort) {
        return repository.findOne(getExample(entity), table);
    }

    @Override
    public Optional<T> selectOne(Weekend<T> weekend, Sort sort) {
        return repository.findOne(weekend, sort, table);
    }

    @Override
    public List<T> select() {
        return repository.findAll(table);
    }

    @Override
    public List<T> select(Sort sort) {
        return repository.findAll(sort, table);
    }

    @Override
    public List<T> select(T entity, Sort sort) {
        return repository.findAll(getExample(entity), sort, table);
    }

    @Override
    public List<T> select(Weekend<T> weekend, Sort sort) {
        return repository.findAll(weekend, sort, table);
    }

    @Override
    public Page<T> select(T entity, Pageable pageable) {
        return repository.findAll(getExample(entity), pageable, table);
    }

    @Override
    public Page<T> select(Pageable pageable) {
        return repository.findAll(pageable, table);
    }

    @Override
    public Page<T> select(Weekend<T> weekend, Pageable pageable) {
        return repository.findAll(weekend, pageable, table);
    }

    @Override
    public List<T> selectByLimit(Weekend<T> weekend, int limit, Sort sort) {
        return repository.findLimit(weekend, limit, sort, table);
    }

    @Override
    public List<T> selectByLimit(T entity, int limit, Sort sort) {
        return repository.findLimit(getExample(entity), limit, sort, table);
    }

    @Override
    public List<T> selectByLimit(int limit, Sort sort) {
        return repository.findLimit(limit, sort, table);
    }

    @Override
    public boolean existsById(ID id) {
        return repository.existsById(id, table);
    }

    @Override
    public long count(T entity) {
        return repository.count(getExample(entity), table);
    }

    @Override
    public long count(Weekend<T> weekend) {
        return repository.count(weekend, table);
    }

    @Override
    public long count() {
        return repository.count(table);
    }

    @Override
    public boolean exists(T entity) {
        return repository.exists(getExample(entity), table);
    }

    @Override
    public boolean exists(Weekend<T> weekend) {
        return repository.exists(weekend, table);
    }
}
