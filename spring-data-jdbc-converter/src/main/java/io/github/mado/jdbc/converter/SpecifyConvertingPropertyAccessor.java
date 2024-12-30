package io.github.mado.jdbc.converter;

import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.convert.PropertyValueConversionService;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.mapping.PersistentPropertyPath;
import org.springframework.data.mapping.model.ConvertingPropertyAccessor;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.data.util.Lazy;

import java.sql.ResultSet;


/**
 * 查询时设置属性、更新时设置参数经过该对象来转换以后再操作
 * @param <T>
 * @author heng.ma
 */
public class SpecifyConvertingPropertyAccessor<T> extends ConvertingPropertyAccessor<T> {

    private final Lazy<PropertyValueConversionService> propertyValueConversionService;

    public SpecifyConvertingPropertyAccessor(PersistentPropertyAccessor<T> accessor, ConversionService conversionService, ApplicationContext applicationContext) {
        super(accessor, conversionService);
        this.propertyValueConversionService = Lazy.of(() -> applicationContext.getBean(PropertyValueConversionService.class));
    }

    /**
     * @see org.springframework.data.jdbc.core.convert.JdbcConverter#mapRow(RelationalPersistentEntity, ResultSet, Object)
     * @param property must not be {@literal null}.
     * @param value can be {@literal null}.
     */
    @Override
    public void setProperty(PersistentProperty<?> property, Object value) {
        PropertyValueConversionService service = propertyValueConversionService.get();
        if (service.hasConverter(property)) {
            value = service.read(value, (PersistentProperty)property, new DefaultValueConversionContext(property));
        }
        super.setProperty(property, value);
    }


    /**
     * see SimpleSqlGenerator
     * @param property must not be {@literal null}.
     * @return
     */
    @Override
    public Object getProperty(PersistentProperty<?> property) {
        Object value = super.getProperty(property);
        PropertyValueConversionService service = propertyValueConversionService.get();
        if (service.hasConverter(property)) {
            value = service.write(value, (PersistentProperty) property, new DefaultValueConversionContext(property));
        }
        return value;
    }

    @Override
    public Object getProperty(PersistentPropertyPath<? extends PersistentProperty<?>> path) {
        return this.getProperty(path.getRequiredLeafProperty());
    }
}
