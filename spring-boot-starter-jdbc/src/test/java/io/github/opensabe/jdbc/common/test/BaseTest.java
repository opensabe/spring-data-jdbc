package io.github.opensabe.jdbc.common.test;

import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * @author heng.ma
 */
@Testcontainers
@SpringBootTest(
        properties = {
                "spring.datasource.username=root",
                "spring.datasource.password=123456",
                "spring.datasource.url=jdbc:p6spy:mysql://localhost:3306/sys",
                "spring.datasource.driver-class-name=com.p6spy.engine.spy.P6SpyDriver",
        },
        classes = App.class)
public class BaseTest {

    @Container
    @SuppressWarnings("unused")
    final static MySQLContainer mysql = new MySQLContainer()
            .withFixedExposedPort(3306, 3306);


}
