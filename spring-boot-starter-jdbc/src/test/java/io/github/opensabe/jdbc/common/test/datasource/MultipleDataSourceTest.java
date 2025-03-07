package io.github.opensabe.jdbc.common.test.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.opensabe.jdbc.common.test.App;
import io.github.opensabe.jdbc.common.test.MySQLContainer;
import io.github.opensabe.jdbc.common.test.datasource.his.UserHisRepository;
import io.github.opensabe.jdbc.common.test.datasource.service.UserHisService;
import io.github.opensabe.jdbc.common.test.datasource.service.UserService;
import io.github.opensabe.jdbc.common.test.datasource.user.UserRepository;
import io.github.opensabe.jdbc.core.EnableJdbcRepositories;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * @author heng.ma
 */

@Import({MultipleDataSourceTest.Config1.class, MultipleDataSourceTest.Config2.class})
@Testcontainers
@SpringBootTest(properties = {
        "spring.dynamic.datasource.default[0].username=root",
        "spring.dynamic.datasource.default[0].password=123456",
        "spring.dynamic.datasource.default[0].url=jdbc:mysql://localhost:3306/sys",

        "spring.dynamic.datasource.readonly[0].username=root",
        "spring.dynamic.datasource.readonly[0].password=123456",
        "spring.dynamic.datasource.readonly[0].url=jdbc:mysql://localhost:3307/sys",
}, classes = App.class)
public class MultipleDataSourceTest {
    @Container
    final static MySQLContainer write = new MySQLContainer()
            .withFixedExposedPort(3306, 3306);
    @Container
    final static MySQLContainer read = new MySQLContainer()
            .withFixedExposedPort(3307, 3306);

    @Import(UserHisService.class)
    @EnableJdbcRepositories(basePackageClasses = UserHisRepository.class, name = "readonly")
    public static class Config1 {

    }

    @Import(UserService.class)
    @EnableJdbcRepositories(basePackageClasses = UserRepository.class)
    public static class Config2 {

    }

    @Autowired
    private UserService userService;

    @Autowired
    private UserHisService hisService;

    @Test
    void setup () {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://localhost:3306/sys");
        hikariConfig.setUsername("root");
        hikariConfig.setPassword("123456");
        HikariDataSource dataSource = new HikariDataSource(hikariConfig);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update("truncate table t_user_his");
        jdbcTemplate.update("insert into `t_user_his` (`id`, `name`, `email`, `age`) values (?,?,?,?)",
                "id1", "name1", "email1", 1);
        jdbcTemplate.update("truncate table t_user");
        jdbcTemplate.update("insert into `t_user` (`id`, `name`, `email`, `age`) values (?,?,?,?)",
                "id1", "name1", "email1", 1);
        Assertions.assertEquals(0, hisService.count());
        Assertions.assertEquals(1, userService.count());
    }
}
