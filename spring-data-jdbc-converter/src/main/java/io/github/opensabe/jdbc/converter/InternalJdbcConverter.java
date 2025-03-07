package io.github.opensabe.jdbc.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.jdbc.core.convert.*;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.relational.domain.RowDocument;
import org.springframework.data.util.TypeInformation;

import java.util.List;

/**
 * 重写readValue方法，在mapRow时，不做任何转换，交给 SpecifyConvertingPropertyAccessor去处理
 * @see JdbcConverter#readValue(Object, TypeInformation)
 * @author heng.ma
 */
public class InternalJdbcConverter extends MappingJdbcConverter {



    public InternalJdbcConverter(RelationalMappingContext context,
                                 RelationResolver relationResolver,
                                 CustomConversions conversions,
                                 JdbcTypeFactory typeFactory, List<Converter> converters) {
        super(context, relationResolver, conversions, typeFactory);
        if (getConversionService() instanceof ConverterRegistry registry) {
            converters.forEach(registry::addConverter);
        }
    }

    @Override
    public <R> R readAndResolve(TypeInformation<R> type, RowDocument source, Identifier identifier) {
        return super.readAndResolve(type, source, identifier);
    }
}
