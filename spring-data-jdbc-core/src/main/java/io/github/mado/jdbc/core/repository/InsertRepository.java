package io.github.mado.jdbc.core.repository;

public interface InsertRepository <T> {

    int insertSelective (T entity);

    long insertList (Iterable<T> entities);
}
