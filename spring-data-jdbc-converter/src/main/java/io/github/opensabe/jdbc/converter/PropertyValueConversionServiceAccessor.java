package io.github.opensabe.jdbc.converter;

import org.springframework.data.convert.PropertyValueConversionService;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.mapping.PersistentPropertyPath;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;

import java.sql.ResultSet;


/**
 * 查询时设置属性、更新时设置参数经过该对象来转换以后再操作
 * @param <T>
 * @author heng.ma
 */
public class PropertyValueConversionServiceAccessor<T> implements PersistentPropertyAccessor<T> {

    private final PersistentPropertyAccessor<T> delegate;
    private final PropertyValueConversionService propertyValueConversionService;

    public PropertyValueConversionServiceAccessor(PersistentPropertyAccessor<T> delegate, PropertyValueConversionService propertyValueConversionService) {
        this.delegate = delegate;
        this.propertyValueConversionService = propertyValueConversionService;
    }

    /**
     * @see org.springframework.data.jdbc.core.convert.JdbcConverter#mapRow(RelationalPersistentEntity, ResultSet, Object)
     * @param property must not be {@literal null}.
     * @param value can be {@literal null}.
     */
    @Override
    public void setProperty(PersistentProperty<?> property, Object value) {
        PropertyValueConversionService service = propertyValueConversionService;
        if (service.hasConverter(property)) {
            value = service.read(value, (PersistentProperty)property, new DefaultValueConversionContext(property));
        }
        delegate.setProperty(property, value);
    }


    /**
     * see SimpleSqlGenerator
     * @param property must not be {@literal null}.
     * @return
     */
    @Override
    public Object getProperty(PersistentProperty<?> property) {
        Object value = delegate.getProperty(property);
        PropertyValueConversionService service = propertyValueConversionService;
        if (service.hasConverter(property)) {
            value = service.write(value, (PersistentProperty) property, new DefaultValueConversionContext(property));
        }
        return value;
    }

    @Override
    public Object getProperty(PersistentPropertyPath<? extends PersistentProperty<?>> path) {
        return this.getProperty(path.getRequiredLeafProperty());
    }

    @Override
    public T getBean() {
        return delegate.getBean();
    }
}
