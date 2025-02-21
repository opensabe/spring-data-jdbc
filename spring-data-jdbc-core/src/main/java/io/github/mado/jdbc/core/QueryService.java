package io.github.mado.jdbc.core;

import io.github.mado.jdbc.core.lambda.Fn;
import io.github.mado.jdbc.core.lambda.Weekend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface QueryService<T, ID> {

    Optional<T> selectById (ID id);

    List<T> selectByIds (List<ID> ids);

    @Nullable
    default T selectInstanceById (ID id) {
        return selectById(id).orElse(null);
    }

    default Optional<T> selectOne () {
        return selectOne(Sort.unsorted());
    }

    Optional<T> selectOne (Sort sort);

    default Optional<T> selectOne(Sort.Direction order, Fn<T, Object>... properties) {
        return selectOne(SortUtils.formArray(order, properties));
    }

    default Optional<T> selectOne(Map<Fn<T, Object>, Sort.Direction> sort) {
        return selectOne(SortUtils.formMap(sort));
    }

    default Optional<T> selectOne (T entity) {
        return selectOne(entity, Sort.unsorted());
    }

    Optional<T> selectOne (T entity, Sort sort);

    default Optional<T> selectOne(T entity, Sort.Direction order, Fn<T, Object>... properties) {
        return selectOne(entity, SortUtils.formArray(order, properties));
    }
    default Optional<T> selectOne(T entity, Map<Fn<T, Object>, Sort.Direction> sort) {
        return selectOne(entity, SortUtils.formMap(sort));
    }

    default Optional<T> selectOne (Weekend<T> weekend) {
        return selectOne(weekend, Sort.unsorted());
    }

    Optional<T> selectOne (Weekend<T> weekend, Sort sort);

    default Optional<T> selectOne(Weekend<T> weekend, Sort.Direction order, Fn<T, Object>... properties) {
        return selectOne(weekend, SortUtils.formArray(order, properties));
    }

    default Optional<T> selectOne(Weekend<T> weekend, Map<Fn<T, Object>, Sort.Direction> sort) {
        return selectOne(weekend, SortUtils.formMap(sort));
    }

    List<T> select ();

    List<T> select (Sort sort);

    default List<T> select (Sort.Direction direction, Fn<T, Object>... order) {
        return select(SortUtils.formArray(direction, order));
    }

    default List<T> select (Map<Fn<T, Object>, Sort.Direction> sort) {
        return select(SortUtils.formMap(sort));
    }

    default List<T> select (T entity) {
        return select(entity, Sort.unsorted());
    }

    List<T> select (T entity, Sort sort);

    default List<T> select (T entity, Sort.Direction direction, Fn<T, Object>... order) {
        return select(entity, SortUtils.formArray(direction, order));
    }

    default List<T> select (T entity, Map<Fn<T, Object>, Sort.Direction> sort) {
        return select(entity, SortUtils.formMap(sort));
    }

    default List<T> select (Weekend<T> weekend) {
        return select(weekend, Sort.unsorted());
    }

    List<T> select (Weekend<T> weekend, Sort sort);

    default List<T> select (Weekend<T> weekend, Sort.Direction direction, Fn<T, Object>... order) {
        return select(weekend, SortUtils.formArray(direction, order));
    }

    default List<T> select (Weekend<T> weekend, Map<Fn<T, Object>, Sort.Direction> sort) {
        return select(weekend, SortUtils.formMap(sort));
    }

    Page<T> select (T entity, Pageable pageable);

    default Page<T> select (T entity, int pageNum, int pageSize) {
        return select(entity, PageRequest.of(pageNum, pageSize));
    }

    default Page<T> select(T entity, int pageNum, int pageSize, Sort.Direction order, Fn<T, Object>... properties) {
        return select(entity, PageRequest.of(pageNum, pageSize).withSort(SortUtils.formArray(order, properties)));
    }

    default Page<T> select (T entity, int pageNum, int pageSize, Map<Fn<T, Object>, Sort.Direction> order) {
        return select(entity, PageRequest.of(pageNum, pageSize).withSort(SortUtils.formMap(order)));
    }
    Page<T> select (Pageable pageable);

    default Page<T> select (int pageNum, int pageSize) {
        return select(PageRequest.of(pageNum, pageSize));
    }

    default Page<T> select(int pageNum, int pageSize, Sort.Direction order, Fn<T, Object>... properties) {
        return select(PageRequest.of(pageNum, pageSize).withSort(SortUtils.formArray(order, properties)));
    }

    default Page<T> select (int pageNum, int pageSize, Map<Fn<T, Object>, Sort.Direction> order) {
        return select(PageRequest.of(pageNum, pageSize).withSort(SortUtils.formMap(order)));
    }

    Page<T> select (Weekend<T> weekend, Pageable pageable);

    default Page<T> select(Weekend<T> weekend, int pageNum, int pageSize, Sort.Direction order, Fn<T, Object>... properties) {
        return select(weekend, PageRequest.of(pageNum, pageSize).withSort(SortUtils.formArray(order, properties)));
    }

    default Page<T> select (Weekend<T> weekend, int pageNum, int pageSize, Map<Fn<T, Object>, Sort.Direction> order) {
        return select(weekend, PageRequest.of(pageNum, pageSize).withSort(SortUtils.formMap(order)));
    }

    default List<T> selectByLimit (Weekend<T> weekend, int limit) {
        return selectByLimit(weekend, limit, Sort.unsorted());
    }
    List<T> selectByLimit (Weekend<T> weekend, int limit, Sort sort);

    default List<T> selectByLimit(Weekend<T> weekend, int limit, Sort.Direction order, Fn<T, Object>... properties) {
        return selectByLimit(weekend, limit, SortUtils.formArray(order, properties));
    }

    default List<T> selectByLimit (Weekend<T> weekend, int limit, Map<Fn<T, Object>, Sort.Direction> order) {
        return selectByLimit(weekend, limit, SortUtils.formMap(order));
    }

    default List<T> selectByLimit (T entity, int limit) {
        return selectByLimit(entity, limit, Sort.unsorted());
    }

    List<T> selectByLimit (T entity, int limit, Sort sort);

    default List<T> selectByLimit(T entity, int limit, Sort.Direction order, Fn<T, Object>... properties) {
        return selectByLimit(entity, limit, SortUtils.formArray(order, properties));
    }

    default List<T> selectByLimit (T entity, int limit, Map<Fn<T, Object>, Sort.Direction> order) {
        return selectByLimit(entity, limit, SortUtils.formMap(order));
    }

    default List<T> selectByLimit (int limit) {
        return selectByLimit(limit, Sort.unsorted());
    }

    List<T> selectByLimit (int limit, Sort sort);

    default List<T> selectByLimit(int limit, Sort.Direction order, Fn<T, Object>... properties) {
        return selectByLimit(limit, SortUtils.formArray(order, properties));
    }

    default List<T> selectByLimit (int limit, Map<Fn<T, Object>, Sort.Direction> order) {
        return selectByLimit(limit, SortUtils.formMap(order));
    }

    boolean existsById (ID id);

    long count (T entity);

    long count ();

    long count (Weekend<T> weekend);

    boolean exists (T entity);

    boolean exists (Weekend<T> weekend);

}
