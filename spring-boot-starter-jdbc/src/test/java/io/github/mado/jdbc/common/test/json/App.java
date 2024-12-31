package io.github.mado.jdbc.common.test.json;

import io.github.mado.jdbc.core.EnableJdbcRepositories;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author heng.ma
 */
@EnableJdbcRepositories("io.github.mado.jdbc.common.test")
@SpringBootApplication(scanBasePackages = "io.github.mado.jdbc.autoconfigure")
public class App {
}
