package io.github.mado.jdbc.core.repository;

import io.github.mado.jdbc.core.lambda.Weekend;
import org.springframework.data.domain.Example;

/**
 * @author heng.ma
 */
public interface DeleteRepository <T, ID> {

    long deleteAll ();

    int deleteAllById (Iterable<ID> ids);

    int deleteAllById (ID ... ids);

    int deleteById (ID id);

    long deleteAll (Example<T> example);
    long deleteAll (Example<T> example, int limit);

    long deleteAll (Weekend<T> weekend);
    long deleteAll (Weekend<T> weekend, int limit);
}
