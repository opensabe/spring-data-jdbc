package io.github.mado.jdbc.core.repository;

import io.github.mado.jdbc.core.lambda.Weekend;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface ListByLimitExecutor<T> {

    List<T> findLimit (int limit, Sort sort);

    List<T> findLimit (Weekend<T> weekend, int limit, Sort sort);

    List<T> findLimit (Example<T> example, int limit, Sort sort);
}
