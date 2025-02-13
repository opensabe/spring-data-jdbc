package io.github.mado.jdbc.core.repository;

import io.github.mado.jdbc.core.lambda.Weekend;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface ListPageAndSortingExecutor <T> {

    List<T> findAll (Sort sort);
    List<T> findAll (Weekend<T> weekend);
    List<T> findAll (Weekend<T> weekend, Sort sort);

    List<T> findAll (Example<T> example);
    List<T> findAll (Example<T> example, Sort sort);
}
