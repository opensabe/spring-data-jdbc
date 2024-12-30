package io.github.mado.jdbc.converter;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.model.Property;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.relational.core.mapping.BasicRelationalPersistentProperty;
import org.springframework.data.relational.core.mapping.NamingStrategy;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;

/**
 * 表示对应数据库字段的对象属性，重写了isMap方法，jdbcConverter在映射属性时，如果是Map就强行关联查询
 * 如果包含Converter注解，不认为是Map，不再关联查询
 * @author heng.ma
 */
public class FixedIsMapPersistentProperty extends BasicRelationalPersistentProperty {
    private final boolean hasConvert;

    public FixedIsMapPersistentProperty(Property property, PersistentEntity<?, RelationalPersistentProperty> owner, SimpleTypeHolder simpleTypeHolder, NamingStrategy namingStrategy) {
        super(property, owner, simpleTypeHolder, namingStrategy);
        this.hasConvert = property.getField().map(f -> AnnotatedElementUtils.hasAnnotation(f, Converter.class)).orElse(false);
    }

    @Override
    public boolean isMap() {
        return (!hasConvert) && super.isMap();
    }
}
