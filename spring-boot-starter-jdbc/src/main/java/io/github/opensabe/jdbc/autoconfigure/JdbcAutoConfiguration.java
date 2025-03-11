package io.github.opensabe.jdbc.autoconfigure;

import io.github.opensabe.jdbc.autoconfigure.config.ConverterConfiguration;
import io.github.opensabe.jdbc.autoconfigure.config.DynamicDataSourceConfiguration;
import io.github.opensabe.jdbc.autoconfigure.config.GenerateConfiguration;
import io.github.opensabe.jdbc.autoconfigure.config.ObservationConfiguration;
import org.springframework.context.annotation.Import;

/**
 * @author heng.ma
 */
@Import({
        GenerateConfiguration.class,
        DynamicDataSourceConfiguration.class,
        ObservationConfiguration.class,
        ConverterConfiguration.class
})
public class JdbcAutoConfiguration {
}
