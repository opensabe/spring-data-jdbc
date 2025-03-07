package io.github.opensabe.jdbc.core.repository;

import io.github.opensabe.jdbc.core.lambda.Weekend;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PageAndSortingExecutor<T> {

    Page<T> findAll (Pageable pageable);
    Page<T> findAll (Weekend<T> weekend, Pageable pageable);
    Page<T> findAll (Example<T> example, Pageable pageable);

}
