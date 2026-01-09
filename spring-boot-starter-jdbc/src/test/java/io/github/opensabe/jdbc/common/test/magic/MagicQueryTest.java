package io.github.opensabe.jdbc.common.test.magic;

import io.github.opensabe.jdbc.common.test.BaseTest;
import io.github.opensabe.jdbc.common.test.common.repository.UserRepository;
import io.github.opensabe.jdbc.common.test.common.service.UserService;
import io.github.opensabe.jdbc.common.test.vo.User;
import io.github.opensabe.jdbc.core.EnableJdbcRepositories;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Import(UserService.class)
@EnableJdbcRepositories(basePackageClasses = UserRepository.class)
public class MagicQueryTest extends BaseTest {

    @Autowired
    private UserService userService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void before () {
        jdbcTemplate.execute("truncate table t_user");
    }

    @Test
    void testQueryLong () {
        Optional<Long> count = userService.selectOne("select 1", new Object(), Long.class);
        Assertions.assertThat(count)
                .isPresent()
                .get()
                .isEqualTo(1L);
    }

    @Test
    void testQueryTime () {
        User user = new User("id", "name", "email", 1);
        userService.insertSelective(user);
        Optional<LocalDateTime> optional = userService.selectOne("select create_time from t_user where id = :id limit 1", user , LocalDateTime.class);
        Assertions.assertThat(optional)
                .isPresent();
    }

    @Test
    void testQueryBean () {
        User user = new User("id", "name", "email", 1);
        userService.insertSelective(user);
        Optional<User> optional = userService.selectOne("select * from t_user where id = :id limit 1", Map.of("id", "id"), User.class);
        Assertions.assertThat(optional)
                .isPresent()
                .get()
                .extracting(User::getId, User::getName, User::getEmail, User::getAge)
                .containsExactly("id", "name", "email", 1);
    }

    @Test
    void testQueryPage () {
        User user = new User("id", "name", "email", 1);
        userService.insertSelective(user);
        Page<User> page = userService.selectPage("select * from t_user", null, PageRequest.of(0, 10), User.class);
        Assertions.assertThat(page)
                .isNotNull()
                .hasSize(1)
                .first()
                .extracting(User::getId, User::getName, User::getEmail, User::getAge)
                .containsExactly("id", "name", "email", 1);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(1);
    }

    @Test
    void testQueryByObject () {
        userService.insertSelective(new User("id", "name", "email", 1));
        User query = new User();
        query.setId("id");
        Page<User> page = userService.selectPage("select * from t_user where id = :id", query, PageRequest.of(0, 10), User.class);
        Assertions.assertThat(page)
                .isNotNull()
                .hasSize(1)
                .first()
                .extracting(User::getId, User::getName, User::getEmail, User::getAge)
                .containsExactly("id", "name", "email", 1);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(1);
    }
}
