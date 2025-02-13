package io.github.mado.jdbc.common.test.common.repository;

import io.github.mado.jdbc.common.test.vo.User;
import io.github.mado.jdbc.core.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;

public interface UserRepository extends BaseRepository<User, String> {

    @Query("select * from t_user")
    Page<User> selectPage (Pageable pageable);
}
