package io.github.mado.jdbc.core;

import org.springframework.data.mapping.PersistentPropertyAccessor;

import java.util.function.Function;

@FunctionalInterface
public interface PropertyAccessorCustomizer extends Function<PersistentPropertyAccessor<?>,PersistentPropertyAccessor<?>> {

    default  PropertyAccessorCustomizer then (PropertyAccessorCustomizer after) {
        return p -> after.apply(apply(p));
    }
}
