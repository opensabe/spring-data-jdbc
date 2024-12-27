package io.github.mado.jdbc.core.repository;

import org.springframework.data.repository.CrudRepository;

public interface BaseRepository<T, ID> extends CrudRepository<T, ID>, WeekendRepository<T>, ExampleRepository<T> {
}
