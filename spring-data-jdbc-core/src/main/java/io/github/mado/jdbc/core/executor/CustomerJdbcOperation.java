package io.github.mado.jdbc.core.executor;

public interface CustomerJdbcOperation extends CriteriaJdbcOperation {

    int deleteById (Object id, Class<?> entityClass);

    long deleteAllById (Iterable<Object> ids, Class<?> entityClass);

    long deleteAll (Class<?> entityClass);

    int deleteAllById (Object[] ids, Class<?> entityClass);

}
