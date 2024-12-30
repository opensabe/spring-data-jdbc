package io.github.mado.jdbc.converter;

import org.springframework.data.annotation.Reference;
import org.springframework.data.convert.PropertyValueConverter;

import java.lang.annotation.*;

/**
 * 指定类型转换器
 * @author heng.ma
 */
@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Reference
public @interface Converter {

    Class<? extends PropertyValueConverter> value() default PropertyValueConverter.class;
}
