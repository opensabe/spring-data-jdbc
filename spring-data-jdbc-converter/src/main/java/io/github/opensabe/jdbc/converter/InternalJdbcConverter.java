package io.github.opensabe.jdbc.converter;

import io.github.opensabe.jdbc.core.executor.PropertyAccessorCustomizer;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.jdbc.core.convert.BasicJdbcConverter;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.jdbc.core.convert.JdbcTypeFactory;
import org.springframework.data.jdbc.core.convert.RelationResolver;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;
import org.springframework.data.relational.core.sql.IdentifierProcessing;
import org.springframework.data.util.TypeInformation;

/**
 * 重写readValue方法，在mapRow时，不做任何转换，交给 SpecifyConvertingPropertyAccessor去处理
 * @see JdbcConverter#readValue(Object, TypeInformation)
 * @author heng.ma
 */
public class InternalJdbcConverter extends BasicJdbcConverter {
    private final PropertyAccessorCustomizer propertyAccessorCustomizer;
    public InternalJdbcConverter(MappingContext<? extends RelationalPersistentEntity<?>, ? extends RelationalPersistentProperty> context,
                                 RelationResolver relationResolver,
                                 CustomConversions conversions,
                                 JdbcTypeFactory typeFactory,
                                 IdentifierProcessing identifierProcessing,
                                 PropertyAccessorCustomizer propertyAccessorCustomizer) {
        super(context, relationResolver, conversions, typeFactory, identifierProcessing);
        this.propertyAccessorCustomizer = propertyAccessorCustomizer;
    }


    @Override
    public Object readValue(Object value, TypeInformation<?> type) {
        return value;
    }

    @Override
    public <T> PersistentPropertyAccessor<T> getPropertyAccessor(PersistentEntity<T, ?> persistentEntity, T instance) {
        return (PersistentPropertyAccessor<T>)propertyAccessorCustomizer.apply(persistentEntity.getPropertyAccessor(instance));
    }
}
