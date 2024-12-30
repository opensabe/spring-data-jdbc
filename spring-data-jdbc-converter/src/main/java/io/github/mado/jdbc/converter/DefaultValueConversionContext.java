package io.github.mado.jdbc.converter;

import org.springframework.data.convert.ValueConversionContext;
import org.springframework.data.mapping.PersistentProperty;


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
    public P getProperty() {
        return (P)property;
    }

}
