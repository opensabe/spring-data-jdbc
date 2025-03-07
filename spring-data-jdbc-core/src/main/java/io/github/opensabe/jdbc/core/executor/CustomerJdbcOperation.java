package io.github.opensabe.jdbc.core.executor;

public interface CustomerJdbcOperation extends CriteriaJdbcOperation, AssignmentJdbcOperation {

    int deleteById (Object id, Class<?> entityClass);

    int deleteAllById (Iterable<?> ids, Class<?> entityClass);

    long deleteAll (Class<?> entityClass);

    int deleteAllById (Object[] ids, Class<?> entityClass);

}
