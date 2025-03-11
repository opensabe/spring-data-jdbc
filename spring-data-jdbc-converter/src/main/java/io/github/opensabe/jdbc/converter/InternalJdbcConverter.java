package io.github.opensabe.jdbc.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.convert.PropertyValueConversionService;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.jdbc.core.convert.JdbcTypeFactory;
import org.springframework.data.jdbc.core.convert.MappingJdbcConverter;
import org.springframework.data.jdbc.core.convert.RelationResolver;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentPropertyPathAccessor;
import org.springframework.data.mapping.model.ValueExpressionEvaluator;
import org.springframework.data.relational.core.conversion.RowDocumentAccessor;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;
import org.springframework.data.util.TypeInformation;
import org.springframework.lang.NonNull;

import java.util.List;

/**
 * 重写readValue方法，在mapRow时，不做任何转换，交给 SpecifyConvertingPropertyAccessor去处理
 * @see JdbcConverter#readValue(Object, TypeInformation)
 * @author heng.ma
 */
public class InternalJdbcConverter extends MappingJdbcConverter {

    private final PropertyValueConversionService propertyValueConversionService;


    @SuppressWarnings("rawtypes")
    public InternalJdbcConverter(RelationalMappingContext context,
                                 RelationResolver relationResolver,
                                 CustomConversions conversions,
                                 JdbcTypeFactory typeFactory, List<Converter> converters, PropertyValueConversionService propertyValueConversionService) {
        super(context, relationResolver, conversions, typeFactory);
        this.propertyValueConversionService = propertyValueConversionService;
        if (getConversionService() instanceof ConverterRegistry registry) {
            converters.forEach(registry::addConverter);
        }
    }

    @Override
    public <T> PersistentPropertyPathAccessor<T> getPropertyAccessor(PersistentEntity<T, ?> persistentEntity, T instance) {
        return new PropertyValueConversionServiceAccessor<>(persistentEntity.getPropertyPathAccessor(instance), getConversionService(), propertyValueConversionService);
    }

    @Override
    @NonNull
    protected RelationalPropertyValueProvider newValueProvider(@NonNull RowDocumentAccessor documentAccessor, @NonNull ValueExpressionEvaluator evaluator, @NonNull ConversionContext context) {
        return new PropertyValueConversionValueProvider(super.newValueProvider(documentAccessor, evaluator, context), documentAccessor, evaluator);
    }


    class PropertyValueConversionValueProvider implements RelationalPropertyValueProvider {

        private final RelationalPropertyValueProvider delegate;
        private final RowDocumentAccessor documentAccessor;
        private final ValueExpressionEvaluator evaluator;

        public PropertyValueConversionValueProvider(RelationalPropertyValueProvider delegate, RowDocumentAccessor documentAccessor, ValueExpressionEvaluator evaluator) {
            this.delegate = delegate;
            this.documentAccessor = documentAccessor;
            this.evaluator = evaluator;
        }

        @Override
        public boolean hasValue(@NonNull RelationalPersistentProperty property) {
            return delegate.hasValue(property);
        }

        @Override
        public boolean hasNonEmptyValue(@NonNull RelationalPersistentProperty property) {
            return delegate.hasNonEmptyValue(property);
        }

        @Override
        @NonNull
        public RelationalPropertyValueProvider withContext(@NonNull ConversionContext context) {
            return new PropertyValueConversionValueProvider(delegate.withContext(context), documentAccessor, evaluator);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T getPropertyValue(@NonNull RelationalPersistentProperty property) {
            if (propertyValueConversionService.hasConverter(property)) {
                String expression = property.getSpelExpression();
                Object value = expression != null ? evaluator.evaluate(expression) : documentAccessor.get(property);

                if (value == null) {
                    return null;
                }

                return (T)propertyValueConversionService.read(value, property, new DefaultValueConversionContext<>(property));
            }
            return delegate.getPropertyValue(property);
        }
    }

}
