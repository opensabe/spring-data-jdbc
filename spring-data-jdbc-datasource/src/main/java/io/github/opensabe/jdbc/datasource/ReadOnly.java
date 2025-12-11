package io.github.opensabe.jdbc.datasource;

import java.lang.annotation.*;


/**
 * 放在repository上，查只读库
 * @author maheng
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ReadOnly {
}
