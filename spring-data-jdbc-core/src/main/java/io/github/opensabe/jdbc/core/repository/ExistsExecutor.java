package io.github.opensabe.jdbc.core.repository;

import io.github.opensabe.jdbc.core.lambda.Weekend;
import org.springframework.data.domain.Example;

public interface ExistsExecutor<T, ID> {

    boolean existsById (ID id);

    boolean exists (Weekend<T>  weekend);

    boolean exists (Example<T> example);
}
