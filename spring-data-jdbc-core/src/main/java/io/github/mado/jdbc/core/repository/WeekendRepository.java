package io.github.mado.jdbc.core.repository;

import io.github.mado.jdbc.core.lambda.Weekend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;

public interface WeekendRepository<T> {

    long updateSelective(T updater, Weekend<T> query);

    long deleteAll(Weekend<T> query);


    default Optional<T> findOne(Weekend<T> query) {
        return findOne(query, Sort.unsorted());
    }

    Optional<T> findOne(Weekend<T> query, Sort sort);

    default Iterable<T> findAll(Weekend<T> query) {
        return findAll(query);
    }

    Iterable<T> findAll(Weekend<T> query, Sort sort);

    Page<T> findAll(Weekend<T> query, Pageable pageable);

    default Iterable<T> findLimit(Weekend<T> query, int limit) {
        return findLimit(query, limit, Sort.unsorted());
    }

    Iterable<T> findLimit(Weekend<T> query, int limit, Sort sort);

    long count(Weekend<T> query);

    default boolean exists(Weekend<T> query) {
        return count(query) > 0;
    }
}
