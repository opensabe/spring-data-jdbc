package io.github.opensabe.jdbc.core.repository;

public interface InsertRepository <T> {

    int insertSelective (T entity);

    long insertList (Iterable<T> entities);
}
