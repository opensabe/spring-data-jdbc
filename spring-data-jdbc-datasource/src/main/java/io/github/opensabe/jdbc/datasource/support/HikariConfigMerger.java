package io.github.opensabe.jdbc.datasource.support;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.lang.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author heng.ma
 */
abstract class HikariConfigMerger {

    /**
     * Connection identity and runtime state, they are never shared between datasources.
     */
    private static final Set<String> CONNECTION_FIELDS = Set.of(
            "jdbcUrl",
            "username",
            "password",
            "driverClassName",
            "dataSource",
            "dataSourceClassName",
            "dataSourceJNDI",
            "dataSourceProperties",
            "poolName",
            "isReadOnly",
            "sealed"
    );

    private static final HikariConfig DEFAULTS = new HikariConfig();

    /**
     * Take the pool settings (capacity, timeouts, etc.) from {@code template} for every property
     * the datasource left untouched, url and credentials always stay on the datasource itself.
     */
    static HikariDataSource mergeDefaults (@Nullable HikariDataSource target, HikariConfig template) {
        HikariDataSource merged = Objects.nonNull(target) ? target : new HikariDataSource();
        eachCopyableField(field -> {
            if (CONNECTION_FIELDS.contains(field.getName())) {
                return;
            }
            Object configured = get(field, merged);
            if (Objects.equals(configured, get(field, DEFAULTS))) {
                Object templateValue = get(field, template);
                if (Objects.nonNull(templateValue) && !Objects.equals(templateValue, configured)) {
                    set(field, merged, templateValue);
                }
            }
        });
        return merged;
    }

    /**
     * Copy every configured value of {@code source} onto {@code target}.
     */
    static void copyState (HikariConfig source, HikariConfig target) {
        eachCopyableField(field -> {
            Object o = get(field, source);
            if (Objects.nonNull(o)) {
                set(field, target, o);
            }
        });
    }

    private static void eachCopyableField (Consumer<Field> consumer) {
        for (Field field : HikariConfig.class.getDeclaredFields()) {
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
                continue;
            }
            field.setAccessible(true);
            consumer.accept(field);
        }
    }

    private static Object get (Field field, Object config) {
        try {
            return field.get(config);
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to read HikariConfig state: " + e.getMessage(), e);
        }
    }

    private static void set (Field field, Object config, Object value) {
        try {
            field.set(config, value);
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to copy HikariConfig state: " + e.getMessage(), e);
        }
    }
}
