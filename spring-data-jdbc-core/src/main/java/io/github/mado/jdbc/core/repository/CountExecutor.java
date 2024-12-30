package io.github.mado.jdbc.core.repository;

import io.github.mado.jdbc.core.lambda.Weekend;
import org.springframework.data.domain.Example;

public interface CountExecutor <T> {
    long count ();
    long count (Example<T> example);
    long count (Weekend<T> weekend);
}
