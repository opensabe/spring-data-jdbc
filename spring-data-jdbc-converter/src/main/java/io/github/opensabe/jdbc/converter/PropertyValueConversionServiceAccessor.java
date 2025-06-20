package io.github.opensabe.jdbc.converter;

import org.springframework.core.convert.ConversionService;
import org.springframework.data.convert.PropertyValueConversionService;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.mapping.model.ConvertingPropertyAccessor;


/**
 * 查询时设置属性、更新时设置参数经过该对象来转换以后再操作
 * @param <T>
 * @author heng.ma
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class PropertyValueConversionServiceAccessor<T> extends ConvertingPropertyAccessor<T> {

    private final PropertyValueConversionService propertyValueConversionService;

    /**
     * Creates a new {@link ConvertingPropertyAccessor} for the given delegate {@link PersistentPropertyAccessor} and
     * {@link ConversionService}.
     *
     * @param accessor          must not be {@literal null}.
     * @param conversionService must not be {@literal null}.
     */
    public PropertyValueConversionServiceAccessor(PersistentPropertyAccessor<T> accessor, ConversionService conversionService, PropertyValueConversionService propertyValueConversionService) {
        super(accessor, conversionService);
        this.propertyValueConversionService = propertyValueConversionService;
    }

    @Override
    public Object getProperty(@SuppressWarnings("NullableProblems")PersistentProperty<?> property) {
        Object value = super.getProperty(property);
        if (propertyValueConversionService.hasConverter(property)) {
            value = propertyValueConversionService.write(value, (PersistentProperty)property, new DefaultValueConversionContext(property));
        }
        return value;
    }

    @Override
    public void setProperty(@SuppressWarnings("NullableProblems")PersistentProperty<?> property, Object value) {
        if (propertyValueConversionService.hasConverter(property)) {
            value = propertyValueConversionService.read(value, (PersistentProperty)property, new DefaultValueConversionContext(property));
        }
        super.setProperty(property, value);
    }
}
