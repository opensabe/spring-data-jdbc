package io.github.opensabe.jdbc.converter;

import org.springframework.data.convert.PropertyValueConversionService;
import org.springframework.data.domain.Example;
import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.repository.query.RelationalExampleMapper;
import org.springframework.data.support.ExampleMatcherAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 在 Spring Data 标准 QBE 结果上补充属性级 {@link Converter} 条件。
 */
public class ConverterAwareRelationalExampleMapper extends RelationalExampleMapper {

    private final MappingContext<? extends RelationalPersistentEntity<?>, ? extends RelationalPersistentProperty> mappingContext;
    private final PropertyValueConversionService conversionService;

    public ConverterAwareRelationalExampleMapper(
            MappingContext<? extends RelationalPersistentEntity<?>, ? extends RelationalPersistentProperty> mappingContext,
            PropertyValueConversionService conversionService) {
        super(mappingContext);
        this.mappingContext = mappingContext;
        this.conversionService = conversionService;
    }

    @Override
    public <T> Query getMappedExample(Example<T> example) {
        Query mappedQuery = super.getMappedExample(example);
        RelationalPersistentEntity<?> entity = mappingContext.getRequiredPersistentEntity(example.getProbeType());
        PersistentPropertyAccessor<T> propertyAccessor = entity.getPropertyAccessor(example.getProbe());
        ExampleMatcherAccessor matcherAccessor = new ExampleMatcherAccessor(example.getMatcher());
        List<Criteria> converterCriteria = new ArrayList<>();

        entity.doWithAssociations((Association<RelationalPersistentProperty> association) -> {
            RelationalPersistentProperty property = association.getInverse();
            if (property.findAnnotation(Converter.class) == null
                    || matcherAccessor.isIgnoredPath(property.getName())) {
                return;
            }

            Optional<?> transformedValue = matcherAccessor
                    .getValueTransformerForPath(property.getName())
                    .apply(Optional.ofNullable(propertyAccessor.getProperty(property)));
            if (transformedValue.isEmpty()) {
                return;
            }

            Object storeValue = conversionService.write(
                    transformedValue.get(), property, new DefaultValueConversionContext<>(property));
            boolean ignoreCase = matcherAccessor.isIgnoreCaseForPath(property.getName());
            String column = property.getName();

            Criteria criteria = switch (matcherAccessor.getStringMatcherForPath(property.getName())) {
                case DEFAULT, EXACT -> Criteria.where(column).is(storeValue).ignoreCase(ignoreCase);
                case ENDING -> Criteria.where(column).like("%" + storeValue).ignoreCase(ignoreCase);
                case STARTING -> Criteria.where(column).like(storeValue + "%").ignoreCase(ignoreCase);
                case CONTAINING -> Criteria.where(column).like("%" + storeValue + "%").ignoreCase(ignoreCase);
                default -> throw new IllegalStateException(
                        example.getMatcher().getDefaultStringMatcher() + " is not supported");
            };
            converterCriteria.add(criteria);
        });

        if (converterCriteria.isEmpty()) {
            return mappedQuery;
        }

        Criteria criteria = Criteria.empty();
        if (mappedQuery.getCriteria().isPresent()) {
            criteria = criteria.and(mappedQuery.getCriteria().get());
        }
        for (Criteria propertyCriteria : converterCriteria) {
            criteria = example.getMatcher().isAllMatching()
                    ? criteria.and(propertyCriteria)
                    : criteria.or(propertyCriteria);
        }
        return Query.query(criteria);
    }
}
