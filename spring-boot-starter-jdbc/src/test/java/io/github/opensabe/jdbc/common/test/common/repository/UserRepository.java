package io.github.opensabe.jdbc.common.test.common.repository;

import io.github.opensabe.jdbc.common.test.vo.User;
import io.github.opensabe.jdbc.core.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends BaseRepository<User, String> {

    @Query("select * from t_user")
    Page<User> selectPage (Pageable pageable);

    @Query("select * from t_user where name = :#{#query.name} and age = :#{#query.age}")
    Page<User> selectPageByQuery (User query, Pageable pageable);

    @Query("select * from t_user where id = :id and age = :age")
    Page<User> selectPageByParam (String id, int age, Pageable pageable);


    @Query("select * from t_user where id = :id and age = :age")
    List<User> selectByIdAndAge (String id, int age);


    @Query("select * from t_user where name = :#{#user.name} and age = :#{#user.age}")
    List<User> selectByQuery(User user);
}
