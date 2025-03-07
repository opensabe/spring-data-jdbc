package io.github.opensabe.jdbc.core.repository;

import java.util.List;

public interface ListQueryExecutor<T, ID> {

    List<T> findAll ();

    List<T> findAllById (Iterable<ID> ids);
}
