package io.github.opensabe.jdbc.core.repository;

import io.github.opensabe.jdbc.core.lambda.Weekend;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface AssignmentTableQueryRepository<T, ID>  {

    Optional<T> findById (ID id, String table);
    List<T> findAllById (Iterable<ID> ids, String table);

    List<T> findAll (Example<T> example, String table);

    List<T> findAll (Example<T> example, Sort sort, String table);

    default Optional<T> findOne (Example<T> example, String table) {
        return findAll(example, table).stream().limit(1).findFirst();
    }

    List<T> findAll (String table);

    List<T> findAll (Sort sort, String table);

    default Optional<T> findOne (Sort sort, String table) {
        return findAll(sort, table).stream().limit(1).findFirst();
    }


    List<T> findAll (Weekend<T> weekend, String table);
    List<T> findAll (Weekend<T> weekend, Sort sort, String table);


    default Optional<T> findOne (Weekend<T> weekend, Sort sort, String table) {
        return findAll(weekend, sort, table).stream().limit(1).findFirst();
    }

    Page<T> findAll (Pageable pageable, String table);
    Page<T> findAll (Weekend<T> weekend, Pageable pageable, String table);
    Page<T> findAll (Example<T> example, Pageable pageable, String table);

    List<T> findLimit (int limit, Sort sort, String table);
    List<T> findLimit (Weekend<T> weekend, int limit, Sort sort, String table);
    List<T> findLimit (Example<T> example, int limit, Sort sort, String table);

    boolean existsById (ID id, String table);

    boolean exists (Weekend<T> weekend, String table);
    boolean exists (Example<T> example, String table);

    long count (Weekend<T> weekend, String table);
    long count (Example<T> example, String table);
    long count (String table);



}
