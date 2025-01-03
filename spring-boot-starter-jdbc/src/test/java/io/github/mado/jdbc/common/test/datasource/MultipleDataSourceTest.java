package io.github.mado.jdbc.common.test.datasource;

import io.github.mado.jdbc.common.test.App;
import io.github.mado.jdbc.common.test.MySQLContainer;
import io.github.mado.jdbc.common.test.datasource.his.UserHisRepository;
import io.github.mado.jdbc.common.test.datasource.service.UserHisService;
import io.github.mado.jdbc.common.test.datasource.service.UserService;
import io.github.mado.jdbc.common.test.datasource.user.UserRepository;
import io.github.mado.jdbc.core.EnableJdbcRepositories;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
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
        userService.getRepository().deleteAll();
        hisService.getRepository().deleteAll();
    }

    @Test
    void testInsert () {

    }
}
