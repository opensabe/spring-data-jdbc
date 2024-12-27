package io.github.mado.jdbc.core.repository;

import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface PersistentRepository<T, Q> {

    int insertSelective (T entity);

    long insertList (Iterable<T> entities);

    long updateByIdSelective (T entity);

    long updateSelective (T updater, Q query);

    long deleteAll (Q query);
}
