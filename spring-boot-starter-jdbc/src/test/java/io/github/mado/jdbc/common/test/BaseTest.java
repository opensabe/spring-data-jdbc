package io.github.mado.jdbc.common.test;

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
                "spring.datasource.url=jdbc:mysql://localhost:3306/sys",
        },
        classes = App.class)
public class BaseTest {

    @Container
    final static MySQLContainer mysql = new MySQLContainer()
            .withFixedExposedPort(3306, 3306);


}
