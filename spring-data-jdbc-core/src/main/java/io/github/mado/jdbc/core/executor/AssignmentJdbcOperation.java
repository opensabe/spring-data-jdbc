package io.github.mado.jdbc.core.executor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.relational.core.query.Query;

import java.util.List;
import java.util.Optional;

public interface AssignmentJdbcOperation {

    <T> Optional<T> findById (Object id, Class<T> entityClass, String table);
    <T> List<T> findAllById (Iterable<?> id, Class<T> entityClass, String table);

    <T> List<T> findAll (Query query, Class<T> entityClass, String table);

    <T> Page<T> findAll (Query query, Pageable pageable, Class<T> entityClass, String table);

    <T> long count (Query query, Class<T> entityClass, String table);

    <T>  boolean exists (Query query, Class<T> entityClass, String table);
    <T>  boolean existsById (Object id, Class<T> entityClass, String table);
}
