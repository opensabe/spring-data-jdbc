package io.github.mado.jdbc.core;

import io.github.mado.jdbc.core.lambda.Fn;
import io.github.mado.jdbc.core.lambda.Weekend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author heng.ma
 */
public interface IService<T, ID> {
    Optional<T> selectById (ID id);

    List<T> selectByIds (List<ID> ids);

    T selectInstanceById (ID id);

    Optional<T> selectOne (T entity);

    Optional<T> selectOne (T entity, Sort sort);

    Optional<T> selectOne(T entity, Sort.Direction order, Fn<T, Object>... properties);

    Optional<T> selectOne(T entity, Map<Fn<T, Object>, Sort.Direction> sort);

    Optional<T> selectOne (Weekend<T> weekend);

    Optional<T> selectOne (Weekend<T> weekend, Sort sort);

    Optional<T> selectOne(Weekend<T> weekend, Sort.Direction order, Fn<T, Object>... properties);

    Optional<T> selectOne(Weekend<T> weekend, Map<Fn<T, Object>, Sort.Direction> sort);

    List<T> select (T entity);

    List<T> select (T entity, Sort.Direction direction, Fn<T, Object>... order);

    List<T> select (T entity, Map<Fn<T, Object>, Sort.Direction> sort);

    List<T> select (Weekend<T> weekend);

    List<T> select (Weekend<T> weekend, Sort.Direction direction, Fn<T, Object>... order);

    List<T> select (Weekend<T> weekend, Map<Fn<T, Object>, Sort.Direction> sort);

    Page<T> select (T entity, Pageable pageable);

    Page<T> select (T entity, int pageNum, int pageSize);

    Page<T> select(T entity, int pageNum, int pageSize, Sort.Direction order, Fn<T, Object>... properties);

    Page<T> select (T entity, int pageNum, int pageSize, Map<Fn<T, Object>, Sort.Direction> order);

    Page<T> select(Weekend<T> weekend, int pageNum, int pageSize, Sort.Direction order, Fn<T, Object>... properties);

    Page<T> select (Weekend<T> weekend, int pageNum, int pageSize, Map<Fn<T, Object>, Sort.Direction> order);

    List<T> selectByLimit (Weekend<T> weekend, int limit);

    List<T> selectByLimit(Weekend<T> weekend, int limit, Sort.Direction order, Fn<T, Object>... properties);

    List<T> selectByLimit (Weekend<T> weekend, int limit, Map<Fn<T, Object>, Sort.Direction> order);

    List<T> selectByLimit (T entity, int limit);

    List<T> selectByLimit(T entity, int limit, Sort.Direction order, Fn<T, Object>... properties);

    List<T> selectByLimit (T entity, int limit, Map<Fn<T, Object>, Sort.Direction> order);

    boolean existsById (ID id);

    long count (T entity);

    long count (Weekend<T> weekend);

    boolean exists (T entity);

    boolean exists (Weekend<T> weekend);

}
