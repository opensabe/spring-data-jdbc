package io.github.opensabe.jdbc.converter;

import org.springframework.data.convert.ValueConversionContext;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.lang.NonNull;


/**
 * 保存Property类型
 * @param <P>   PersistentProperty代表的属性类型
 * @author heng.ma
 */
public class DefaultValueConversionContext<P extends PersistentProperty<P>> implements ValueConversionContext<P> {

    private final PersistentProperty<P> property;

    public DefaultValueConversionContext(PersistentProperty<P> property) {
        this.property = property;
    }

    @Override
    @SuppressWarnings("unchecked")
    @NonNull
    public P getProperty() {
        return (P)property;
    }

}
