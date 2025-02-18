package io.github.mado.jdbc.common.test.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.mado.jdbc.common.test.BaseTest;
import io.github.mado.jdbc.common.test.common.repository.UserRepository;
import io.github.mado.jdbc.common.test.common.service.UserService;
import io.github.mado.jdbc.common.test.vo.User;
import io.github.mado.jdbc.core.EnableJdbcRepositories;
import io.github.mado.jdbc.core.lambda.Weekend;
import io.github.mado.jdbc.core.lambda.WeekendCriteria;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author heng.ma
 */
@Import(UserService.class)
@EnableJdbcRepositories(basePackageClasses = UserRepository.class)
public class QueryTest extends BaseTest {


    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void truncateTable () {
        jdbcTemplate.update("truncate table t_user");
        List<User> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(new User("id"+i, "name"+i, "email"+i, i*10));
        }
        userService.insertList(list);
    }


    @Test
    void testFindOneByNull () {
        Optional<User> optional = userService.selectOne();
        assertThat(optional)
                .isPresent()
                .get()
                .extracting(User::getAge)
                .isEqualTo(0);
    }

    @Test
    void testPageByQueryMethod () {
        Page<User> page = userRepository.selectPage(PageRequest.of(0, 1));
        assertThat(page)
                .hasSize(1);
        Assertions.assertEquals(100, page.getTotalElements());
    }

    @Test
    public void findById() {
        Optional<User> optional = userService.selectById("id" + 1);
        assertThat(optional).isPresent()
                .get().extracting(User::getName, User::getEmail, User::getAge)
                .contains("name1", "email1", 10);
    }

    @Test
    public void findAllByIds() {
        List<User> list = userService.selectByIds(List.of("id1", "id2"));
        assertThat(list)
                .hasSize(2)
                .extracting(User::getName, User::getEmail, User::getAge)
                .containsExactly(Tuple.tuple("name1","email1", 10), Tuple.tuple("name2","email2",20));

    }

    @Test
    public void findByEntity() {
        User query = new User();
        query.setName("name1");
        List<User> list = userService.select(query);
        assertThat(list)
                .hasSize(1)
                .extracting(User::getName, User::getEmail, User::getAge)
                .containsExactly(Tuple.tuple("name1","email1", 10));
    }

    @Test
    public void findByWeekend() {
        Weekend<User> weekend = Weekend.of(User.class);
        weekend.weekendCriteria()
                .andLike(User::getName, "name%");
        List<User> list = userService.select(weekend);
        assertThat(list)
                .hasSize(100)
                .last()
                .extracting(User::getName, User::getEmail, User::getAge)
                .containsExactly("name99","email99", 990);
    }

    @Test
    public void existsById() {
        boolean exists;
        for (int i = 0; i < 100; i++) {
            exists = userService.existsById("id"+i);
            Assertions.assertTrue(exists);
            exists = userService.existsById("ids"+i);
            Assertions.assertFalse(exists);
        }
    }

    @Test
    public void existsByEntity() {
        User query = new User();
        boolean exists;
        for (int i = 0; i < 100; i++) {
            query.setName("name"+i);
            exists = userService.exists(query);
            Assertions.assertTrue(exists);
            query.setName("nameaa"+i);
            exists = userService.exists(query);
            Assertions.assertFalse(exists);
        }


        query.setName("name111");
        exists = userService.exists(query);
        Assertions.assertFalse(exists);
    }

    @Test
    public void existsByWeekend() {
        Weekend<User> weekend = Weekend.of(User.class);
        WeekendCriteria<User, Object> weekendCriteria = weekend.weekendCriteria()
                .andEqualTo(User::getName, "name1");
        boolean exists = userService.exists(weekend);
        Assertions.assertTrue(exists);

        weekendCriteria.andGreaterThan(User::getAge, 10);

        exists = userService.exists(weekend);
        Assertions.assertFalse(exists);
    }

    @Test
    public void countByEntity() {
        User query = new User();
        query.setName("name1");
        long count = userService.count(query);
        Assertions.assertEquals(1, count);
        query.setAge(20);
        count = userService.count(query);
        Assertions.assertEquals(0, count);
    }

    @Test
    public void countByWeekend() {
        Weekend<User> weekend = Weekend.of(User.class);
        WeekendCriteria<User, Object> weekendCriteria = weekend.weekendCriteria()
                .andLike(User::getName, "name%");
        long count = userService.count(weekend);
        Assertions.assertEquals(100, count);
        weekendCriteria.andGreaterThan(User::getAge, 20);
        count = userService.count(weekend);
        Assertions.assertEquals(97, count);

    }

    @Test
    public void findOneByEntity() {
        User query = new User();
        query.setName("name1");
        Optional<User> optional = userService.selectOne(query);
        assertThat(optional)
                .isPresent()
                .get()
                .extracting(User::getAge)
                .isEqualTo(10);
    }

    @Test
    public void findOneByEntityOrder() {
        User user = new User("id111", "name1", "email1", 10);
        userService.insertSelective(user);
        User query = new User();
        query.setName("name1");
        Optional<User> optional = userService.selectOne(query, Sort.Direction.DESC, User::getId);
        assertThat(optional)
                .isPresent()
                .get()
                .extracting(User::getId)
                .isEqualTo("id111");
    }

    @Test
    public void findOneByWeekend() {
        Weekend<User> weekend = Weekend.of(User.class);
        weekend.weekendCriteria()
                .andIn(User::getId, "id1");
        Optional<User> optional = userService.selectOne(weekend);
        assertThat(optional)
                .isPresent()
                .get()
                .extracting(User::getName)
                .isEqualTo("name1");
    }

    @Test
    public void findOneByWeekendOrder() {
        Weekend<User> weekend = Weekend.of(User.class);
        weekend.weekendCriteria()
                .andLike(User::getId, "id%");
        Optional<User> optional = userService.selectOne(weekend, Sort.Direction.DESC, User::getId);
        assertThat(optional)
                .isPresent()
                .get()
                .extracting(User::getName)
                .isEqualTo("name99");
    }

    @Test
    public void testFindAllEntity() {
        User user = new User("id111", "name1", "email1", 10);
        userService.insertSelective(user);
        User query = new User();
        query.setName("name1");
        List<User> list = userService.select(query);
        assertThat(list)
                .hasSize(2)
                .extracting(User::getId)
                .containsExactly("id1","id111");
    }

    @Test
    public void testFindAllWeekend() {
        Weekend<User> weekend = Weekend.of(User.class);
        weekend.weekendCriteria()
                .andGreaterThan(User::getAge, 950);
        List<User> list = userService.select(weekend);
        assertThat(list)
                .hasSize(4)
                .extracting(User::getId, User::getName,User::getEmail,User::getAge)
                .containsExactly(
                        Tuple.tuple("id96","name96","email96", 960),
                        Tuple.tuple("id97","name97","email97", 970),
                        Tuple.tuple("id98","name98","email98", 980),
                        Tuple.tuple("id99","name99","email99", 990)
                );
    }

    @Test
    public void testFindAllEntityOrder() {
        User user = new User("id111", "name1", "email1", 10);
        userService.insertSelective(user);
        User query = new User();
        query.setName("name1");
        List<User> list = userService.select(query, Sort.Direction.DESC, User::getId);
        assertThat(list)
                .hasSize(2)
                .first()
                .extracting(User::getId)
                .isEqualTo("id111");
    }

    @Test
    public void testFindAllWeekendOrder() {
        Weekend<User> weekend = Weekend.of(User.class);
        weekend.weekendCriteria()
                .andGreaterThan(User::getAge, 950);
        List<User> list = userService.select(weekend, Sort.Direction.DESC, User::getName);
        assertThat(list)
                .hasSize(4)
                .first()
                .extracting(User::getName)
                .isEqualTo("name99");
    }

    @Test
    public void testFindPageEntityOrder() throws JsonProcessingException {
        Page<User> page = userService.select(new User(), 1, 10,Sort.Direction.ASC, User::getAge);
        System.out.println(objectMapper.writeValueAsString(page));
        Assertions.assertEquals(100, page.getTotalElements());
        assertThat(page)
                .hasSize(10)
                .map(User::getAge)
                .containsExactly(0, 10, 20, 30, 40, 50, 60, 70, 80, 90);
        page = userService.select(new User(), 2, 10,Sort.Direction.ASC, User::getAge);
        Assertions.assertEquals(100, page.getTotalElements());
        assertThat(page)
                .hasSize(10)
                .map(User::getName)
                .containsExactly("name10", "name11", "name12", "name13", "name14", "name15", "name16", "name17", "name18", "name19");
    }

    @Test
    public void testFindPageWeekendOrder() {
        Weekend<User> weekend = Weekend.of(User.class);
        weekend.weekendCriteria()
                .andGreaterThan(User::getAge, 9 * 10);
        Page<User> page = userService.select(weekend, 1, 10,Sort.Direction.ASC, User::getAge);
        Assertions.assertEquals(90, page.getTotalElements());
        assertThat(page)
                .hasSize(10)
                .map(User::getAge)
                .containsExactly(100,110, 120, 130, 140, 150, 160, 170, 180, 190);
        page = userService.select(weekend, 2, 10,Sort.Direction.ASC, User::getAge);
        Assertions.assertEquals(90, page.getTotalElements());
        assertThat(page)
                .hasSize(10)
                .map(User::getName)
                .containsExactly("name20","name21", "name22", "name23", "name24", "name25", "name26", "name27", "name28", "name29");
    }

    @Test
    public void findLimitByEntity() {
        User user = new User("id111", "name1", "email1", 10);
        User user1 = new User("id222", "name1", "email1", 10);
        userService.insertList(List.of(user1,user));
        User query = new User();
        query.setName("name1");
        query.setAge(10);
        long count = userService.count(query);
        Assertions.assertEquals(3, count);
        List<User> list = userService.selectByLimit(query, 2);
        assertThat(list)
                .hasSize(2)
                .extracting(User::getId)
                .containsExactly("id1", "id111");

    }

    @Test
    public void findLimitByEntityOrder() {
        User user = new User("id111", "name1", "email1", 10);
        User user1 = new User("id222", "name1", "email1", 10);
        userService.insertList(List.of(user1,user));
        User query = new User();
        query.setName("name1");
        query.setAge(10);
        long count = userService.count(query);
        Assertions.assertEquals(3, count);
        List<User> list = userService.selectByLimit(query, 2, Sort.Direction.DESC, User::getId);
        assertThat(list)
                .hasSize(2)
                .extracting(User::getId)
                .containsExactly(  "id222", "id111");
    }

    @Test
    public void findLimitByWeekend() {
        Weekend<User> weekend = Weekend.of(User.class);
        weekend.weekendCriteria()
                .andIn(User::getId, "id1", "id2", "id3");
        List<User> list = userService.selectByLimit(weekend, 2);
        assertThat(list)
                .hasSize(2)
                .extracting(User::getId)
                .containsExactly("id1", "id2");
    }

    @Test
    public void findLimitByWeekendOrder() {
        Weekend<User> weekend = Weekend.of(User.class);
        weekend.weekendCriteria()
                .andIn(User::getId, "id1", "id2", "id3");
        List<User> list = userService.selectByLimit(weekend, 2, Sort.Direction.DESC, User::getId);
        assertThat(list)
                .hasSize(2)
                .extracting(User::getId)
                .containsExactly("id3", "id2");
    }
}
