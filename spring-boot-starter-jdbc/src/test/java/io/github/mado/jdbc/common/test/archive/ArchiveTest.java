package io.github.mado.jdbc.common.test.archive;

import io.github.mado.jdbc.common.test.BaseTest;
import io.github.mado.jdbc.common.test.datasource.service.UserService;
import io.github.mado.jdbc.common.test.datasource.user.UserRepository;
import io.github.mado.jdbc.common.test.vo.User;
import io.github.mado.jdbc.core.ArchiveService;
import io.github.mado.jdbc.core.EnableJdbcRepositories;
import io.github.mado.jdbc.core.lambda.Weekend;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Optional;

/**
 * @author heng.ma
 */
@Import(UserService.class)
@EnableJdbcRepositories(basePackageClasses = UserRepository.class)
public class ArchiveTest extends BaseTest {

    private ArchiveService<User, String> service;

    @Autowired
    private UserService userService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void truncateTable () {
        jdbcTemplate.update("truncate table t_user_his");
        for (int i = 1; i < 11; i++) {
            jdbcTemplate.update("insert into `t_user_his` (`id`, `name`, `email`, `age`) values (?,?,?,?)",
                    "id"+i, "name"+i, "email"+i, i);
        }
        this.service = userService.archive();
    }

    @Test
    void testFindById () {
        Optional<User> optional = service.selectById("id1");
        Assertions.assertThat(optional)
                .isPresent()
                .get()
                .extracting(User::getAge)
                .isEqualTo(1);

    }
    @Test
    void testFindByIds () {
        List<User> list = service.selectByIds(List.of("id1", "id2"));
        Assertions.assertThat(list)
                .hasSize(2)
                .extracting(User::getAge)
                .containsExactly(1,2);

    }

    @Test
    void testFindAllByQuery () {
        Weekend<User> weekend = Weekend.of(User.class);
        weekend.weekendCriteria()
                .andIn(User::getId, "id1", "id2");
        List<User> list = service.select(weekend);
        Assertions.assertThat(list)
                .hasSize(2)
                .extracting(User::getAge)
                .containsExactly(1,2);
    }

    @Test
    void testCountByQuery() {
        Weekend<User> weekend = Weekend.of(User.class);
        weekend.weekendCriteria()
                .andIn(User::getId, "id1", "id2");
        long count = service.count(weekend);
        Assertions.assertThat(count).isEqualTo(2);
    }

    @Test
    void testPage () {
        Weekend<User> weekend = Weekend.of(User.class);
        weekend.weekendCriteria()
                .andGreaterThan(User::getAge, 4);
        Page<User> page = service.select(weekend, 0, 2, Sort.Direction.DESC, User::getAge);
        Assertions.assertThat(page).hasSize(2)
                .extracting(User::getAge)
                .containsExactly(10, 9);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(6);
    }

    @Test
    void testExists () {
        Weekend<User> weekend = Weekend.of(User.class);
        weekend.weekendCriteria()
                .andGreaterThan(User::getAge, 4);
        boolean exists = service.exists(weekend);
        Assertions.assertThat(exists).isTrue();
    }

    @Test
    void testExistsById () {
        boolean exists = service.existsById("id1");
        Assertions.assertThat(exists).isTrue();
    }
}
