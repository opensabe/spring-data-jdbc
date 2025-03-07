package io.github.opensabe.jdbc.converter;

import org.springframework.data.convert.PropertyValueConverter;

/**
 * 自己写Converter时，为了简化泛型，屏蔽Context泛型
 * @param <D>   java类型，映射的java对象类型
 * @param <S>   jdbc类型，数据库保存的类型
 */
public interface InternalPropertyValueConverter<D, S> extends PropertyValueConverter<D, S, DefaultValueConversionContext<?>> {
}
