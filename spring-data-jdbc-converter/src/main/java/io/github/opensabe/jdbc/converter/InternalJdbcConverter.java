package io.github.opensabe.jdbc.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.convert.PropertyValueConversionService;
import org.springframework.data.jdbc.core.convert.*;
import org.springframework.data.mapping.model.ValueExpressionEvaluator;
import org.springframework.data.relational.core.conversion.RowDocumentAccessor;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;
import org.springframework.data.relational.domain.RowDocument;
import org.springframework.data.util.TypeInformation;

import java.util.List;

/**
 * 重写readValue方法，在mapRow时，不做任何转换，交给 SpecifyConvertingPropertyAccessor去处理
 * @see JdbcConverter#readValue(Object, TypeInformation)
 * @author heng.ma
 */
public class InternalJdbcConverter extends MappingJdbcConverter {

    private final PropertyValueConversionService propertyValueConversionService;


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
    public <R> R readAndResolve(TypeInformation<R> type, RowDocument source, Identifier identifier) {
        return super.readAndResolve(type, source, identifier);
    }

    @Override
    protected RelationalPropertyValueProvider newValueProvider(RowDocumentAccessor documentAccessor, ValueExpressionEvaluator evaluator, ConversionContext context) {
        return new PropertyValueConversionValueProvider(super.newValueProvider(documentAccessor, evaluator, context), documentAccessor, evaluator);
    }


    public class PropertyValueConversionValueProvider implements RelationalPropertyValueProvider {

        private final RelationalPropertyValueProvider delegate;
        private final RowDocumentAccessor documentAccessor;
        private final ValueExpressionEvaluator evaluator;

        public PropertyValueConversionValueProvider(RelationalPropertyValueProvider delegate, RowDocumentAccessor documentAccessor, ValueExpressionEvaluator evaluator) {
            this.delegate = delegate;
            this.documentAccessor = documentAccessor;
            this.evaluator = evaluator;
        }

        @Override
        public boolean hasValue(RelationalPersistentProperty property) {
            return delegate.hasValue(property);
        }

        @Override
        public boolean hasNonEmptyValue(RelationalPersistentProperty property) {
            return delegate.hasNonEmptyValue(property);
        }

        @Override
        public RelationalPropertyValueProvider withContext(ConversionContext context) {
            return new PropertyValueConversionValueProvider(delegate.withContext(context), documentAccessor, evaluator);
        }

        @Override
        public <T> T getPropertyValue(RelationalPersistentProperty property) {
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
