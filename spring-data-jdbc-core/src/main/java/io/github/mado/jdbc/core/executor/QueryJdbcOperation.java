package io.github.mado.jdbc.core.executor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.relational.core.query.Query;

import java.util.Optional;

/**
 * @author heng.ma
 */
public interface QueryJdbcOperation {

    <T> int insertSelective (T entity, Class<T> entityClass);

    <T> long insertList (Iterable<T> entities, Class<T> entityClass);

    <T> long updateByIdSelective (T entity, Class<T> entityClass);

    <T> long updateSelective (T updater, Query query, Class<T> entityClass);

    <T> long deleteAll (Query query, Class<T> entityClass);

    /**
     * select limit 1
     */
    <T>  Optional<T> findOne (Query query, Class<T> entityClass);

    default <T> Optional<T> findOne (Query query, Sort sort, Class<T> entityClass) {
        return findOne(query.sort(sort), entityClass);
    }

    <T>  Iterable<T> findAll (Query query, Class<T> entityClass);

    default <T> Iterable<T> findAll (Query query, Sort sort, Class<T> entityClass) {
        return findAll(query.sort(sort), entityClass);
    }

    <T> Page<T> findAll (Query query, Pageable pageable, Class<T> entityClass);

    default<T>  Iterable<T> findLimit (Query query, int limit, Class<T> entityClass) {
        return findAll(query.limit(limit), entityClass);
    }

    default <T> Iterable<T> findLimit (Query query, int limit, Sort sort, Class<T> entityClass) {
        return findAll(query.limit(limit).sort(sort), entityClass);
    }

    <T> long count (Query query, Class<T> entityClass);

    default<T>  boolean exists (Query query, Class<T> entityClass) {
        return count(query, entityClass) > 0;
    }
}
