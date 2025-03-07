package io.github.opensabe.jdbc.core.executor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.relational.core.query.Query;

import java.util.List;
import java.util.Optional;

/**
 * @author heng.ma
 */
public interface CriteriaJdbcOperation {

    <T> int insertSelective (T entity, Class<T> entityClass);

    <T> long insertList (Iterable<T> entities, Class<T> entityClass);

    <T> int updateByIdSelective (T entity, Class<T> entityClass);

    <T> long updateSelective (T updater, Query query, Class<T> entityClass);

    <T> long deleteAll (Query query, Class<T> entityClass);

    /**
     * select limit 1
     */
    <T>  Optional<T> findOne (Query query, Class<T> entityClass);

    <T>  List<T> findAll (Query query, Class<T> entityClass);

    <T> Page<T> findAll (Query query, Pageable pageable, Class<T> entityClass);

    <T> long count (Query query, Class<T> entityClass);

    <T>  boolean exists (Query query, Class<T> entityClass);
}
