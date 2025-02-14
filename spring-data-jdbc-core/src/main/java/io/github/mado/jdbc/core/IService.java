package io.github.mado.jdbc.core;

import io.github.mado.jdbc.core.lambda.Weekend;

import java.util.List;

/**
 * @author heng.ma
 */
public interface IService<T, ID> extends QueryService<T, ID> {

    int insertSelective (T entity);

    long insertList (List<T> entities);

    T updateById (T entity);

    int updateByIdSelective (T entity);

    long updateSelective (T entity, T query);

    long updateSelective (T entity, Weekend<T> weekend);

    int deleteById (ID id);

    int deleteAllById (Iterable<ID> ids);
    int deleteAllById (ID ... ids);

    long deleteAll (T entity);

    long deleteAll (Weekend<T> weekend);
}
