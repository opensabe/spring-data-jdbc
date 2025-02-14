package io.github.mado.jdbc.common.test.common;

import io.github.mado.jdbc.common.test.BaseTest;
import io.github.mado.jdbc.common.test.common.repository.UserRepository;
import io.github.mado.jdbc.common.test.common.service.UserService;
import io.github.mado.jdbc.common.test.vo.User;
import io.github.mado.jdbc.core.EnableJdbcRepositories;
import io.github.mado.jdbc.core.lambda.Weekend;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author heng.ma
 */
@Import(UserService.class)
@EnableJdbcRepositories(basePackageClasses = UserRepository.class)
public class UpdateTest extends BaseTest {

    @Autowired
    private UserService userService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void truncateTable () {
        jdbcTemplate.update("truncate table t_user");
    }


    @Test
    public void testUpdateNoExesits () {
        User user = new User();
        user.setId("11");
        user.setAge(10);
        int i = userService.updateByIdSelective(user);
        Assertions.assertEquals(0, i);
    }

    @Test
    public void insertSelective() {

        String  id = UUID.randomUUID().toString();

        User user = new User();
        user.setId(id);
        user.setName("name1");

        LocalDateTime useless = LocalDateTime.now().minusDays(1);

        user.setCreateTime(useless);
        user.setUpdateTime(useless);

        userService.insertSelective(user);

        Optional<User> optional = userService.selectById(id);

        org.assertj.core.api.Assertions.assertThat(optional)
                .isPresent()
                .get()
                .matches(u -> id.equals(u.getId()))
                .matches(u -> u.getCreateTime().isAfter(useless))
                .matches(u -> u.getUpdateTime().isAfter(useless));

    }

    @Test
    public void updateByPrimaryKeySelective() {
        User old = new User(UUID.randomUUID().toString(), "u1", null, 10);
        userService.insertSelective(old);

        //测试createTime跟updateTime默认值，insert时值相同
        User before = userService.selectInstanceById(old.getId());
        Assertions.assertEquals(before.getCreateTime(), before.getUpdateTime());

        //执行update
        User update = new User(old.getId(), "name1", null, null);
        userService.updateByIdSelective(update);

        //更新以后得数据
        User user = userService.selectInstanceById(old.getId());

        Assertions.assertNotNull(user);
        Assertions.assertEquals(user.getName(), update.getName());
        Assertions.assertEquals(user.getEmail(), old.getEmail());
        Assertions.assertEquals(user.getAge(), old.getAge());
        //测试updateTime在更新时会自动更新
        Assertions.assertTrue(user.getUpdateTime().isAfter(user.getCreateTime()));
    }

    @Test
    public void deleteById() {
        User user = new User(UUID.randomUUID().toString(), "name", "email", 10);

        userService.insertSelective(user);
        long count = userService.getRepository().count();
        //before delete, the count in db is 1
        Assertions.assertEquals(1, count);

        userService.deleteById(user.getId());

        //after delete assert the count is 0
        long zero = userService.getRepository().count();
        Assertions.assertEquals(0, zero);
    }

    @Test
    public void deleteByEntity() {
        List<User> list = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            list.add(new User("id"+i, "name"+i, "email", i));
        }
        userService.insertList(list);

        long delete = userService.deleteAll(new User(null, "name1", null, 1));

        Assertions.assertEquals(1,delete);
    }

    @Test
    public void deleteByIds() {
        List<User> list = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            list.add(new User("id"+i, "name"+i, "email", i));
        }
        userService.insertList(list);

        long count = userService.getRepository().count();

        Assertions.assertEquals(list.size(), count);

        userService.deleteAllById(list.stream().map(User::getId).limit(3).toList());

        long remain = userService.getRepository().count();

        Assertions.assertEquals(list.size() - 3, remain);

    }


    @Test
    public void deleteByWeekend() {
        List<User> list = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            list.add(new User("id"+i, "name"+i, "email", i));
        }
        userService.insertList(list);

        Weekend<User> weekend = Weekend.of(User.class);
        weekend.weekendCriteria()
                .andEqualTo(User::getId, "id1")
                .andEqualTo(User::getAge, 1);
        long delete = userService.deleteAll(weekend);


        Assertions.assertEquals(1, delete);
    }

    @Test
    public void updateSelectiveByWeekend() {
        User user = new User(UUID.randomUUID().toString(), "name1", "email1", 10);
        userService.insertSelective(user);

        User update = new User(null, "name", null, 11);
        Weekend<User> weekend = Weekend.of(User.class);
        weekend.weekendCriteria()
                .andEqualTo(User::getId, user.getId())
                .andEqualTo(User::getName, "name1");

        long i = userService.updateSelective(update, weekend);

        Assertions.assertTrue(i > 0);

        User current = userService.selectInstanceById(user.getId());

        Assertions.assertEquals(update.getName(), current.getName());
        Assertions.assertEquals(update.getAge(), current.getAge());
        Assertions.assertEquals(user.getEmail(), current.getEmail());

    }

    @Test
    public void updateSelectiveByEntity() {
        List<User> list = new ArrayList<>(100);
        for (int i = 0; i < 100; i++) {
            list.add(new User("id"+i, "name"+i, "email@"+i, i%10));
        }
        long l = userService.insertList(list);
        Assertions.assertEquals(100, l);

        User where = new User();
        where.setAge(0);
        User update = new User();
        update.setName("new Name");

        long i = userService.updateSelective(update, where);
        Assertions.assertEquals(10, i);

        List<User> select = userService.select(update);

        Assertions.assertEquals(10, select.size());
    }


    @Test
    public void insertList() {
        List<User> list = new ArrayList<>(100);
        for (int i = 0; i < 100; i++) {
            list.add(new User("id"+i, "name"+i, "email@"+i, i+10));
        }
        long l = userService.insertList(list);
        Assertions.assertEquals(100, l);

        long realCount = userService.getRepository().count();

        Assertions.assertEquals(100, realCount);
    }
}
