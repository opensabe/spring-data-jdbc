package io.github.mado.jdbc.core.repository;

import io.github.mado.jdbc.core.lambda.Weekend;
import org.springframework.data.domain.Example;

/**
 * @author heng.ma
 */
public interface UpdateRepository<T> {

    T updateById (T entity);

    int updateByIdSelective (T entity);

    long updateSelective (T entity, Weekend<T> weekend);
    long updateSelective (T entity, Weekend<T> weekend, int limit);

    long updateSelective (T entity, Example<T> example);
    long updateSelective (T entity, Example<T> example, int limit);
}
