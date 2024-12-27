package io.github.mado.jdbc.core.repository;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface ExampleRepository<T> {
    
    int insertSelective(T entity);
    
    long insertList(Iterable<T> entities);
    
    long updateByIdSelective(T entity);

    long updateSelective(T updater, Example<T> query);

    long deleteAll(Example<T> query);
    
    Optional<T> findOne(Example<T> query, Sort sort);

    Iterable<T> findLimit(Example<T> query, int limit, Sort sort);

}
