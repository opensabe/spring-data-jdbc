package io.github.mado.jdbc.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

/**
 * @author heng.ma
 */
@NoRepositoryBean
public interface QueryRepository<T, Q> {

    /**
     * select limit 1
     */
    default Optional<T> findOne (Q query) {
        return findOne(query, Sort.unsorted());
    }

    Optional<T> findOne (Q query, Sort sort);

    default Iterable<T> findAll (Q query) {
        return findAll(query, Sort.unsorted());
    }

    Iterable<T> findAll (Q query, Sort sort);

    Page<T> findAll (Q query, Pageable pageable);

    default Iterable<T> findLimit (Q query, int limit) {
        return findLimit(query, limit, Sort.unsorted());
    }

    Iterable<T> findLimit (Q query, int limit, Sort sort);

    long count (Q query);

    default boolean exists (Q query) {
        return count(query) > 0;
    }
}
