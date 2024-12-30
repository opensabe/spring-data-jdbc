package io.github.mado.jdbc.core.repository;

import io.github.mado.jdbc.core.lambda.Weekend;
import org.springframework.data.domain.Example;

public interface ExistsExecutor<T, ID> {

    boolean existsById (ID id);

    boolean existsById (Weekend<T>  weekend);

    boolean existsById (Example<T> example);
}
