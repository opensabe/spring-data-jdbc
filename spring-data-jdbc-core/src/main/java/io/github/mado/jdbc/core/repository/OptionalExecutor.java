package io.github.mado.jdbc.core.repository;

import io.github.mado.jdbc.core.lambda.Weekend;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;

import java.util.Optional;

public interface OptionalExecutor<T, ID> {

    Optional<T> findById (ID id);

    Optional<T> findOne (Sort sort);

    Optional<T> findOne (Example<T> example);
    Optional<T> findOne (Example<T> example, Sort sort);

    Optional<T> findOne (Weekend<T> weekend);
    Optional<T> findOne (Weekend<T> weekend, Sort sort);
}
