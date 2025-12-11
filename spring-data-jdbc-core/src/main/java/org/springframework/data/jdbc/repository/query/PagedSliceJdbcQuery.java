/*
 * Copyright 2020-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.jdbc.repository.query;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.sql.SQLType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.beans.BeanUtils;
import org.springframework.data.core.TypeInformation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.expression.ValueEvaluationContext;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.data.jdbc.core.convert.JdbcColumnTypes;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.jdbc.core.mapping.JdbcValue;
import org.springframework.data.jdbc.support.JdbcUtil;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.relational.repository.query.RelationalParameterAccessor;
import org.springframework.data.relational.repository.query.RelationalParametersParameterAccessor;
import org.springframework.data.repository.query.Parameter;
import org.springframework.data.repository.query.Parameters;
import org.springframework.data.repository.query.ResultProcessor;
import org.springframework.data.repository.query.ValueExpressionDelegate;
import org.springframework.data.repository.query.ValueExpressionQueryRewriter;
import org.springframework.data.util.Lazy;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * A query to be executed based on a repository method, it's annotated SQL query and the arguments provided to the
 * method.
 *
 * @author Jens Schauder
 * @author Kazuki Shimizu
 * @author Oliver Gierke
 * @author Maciej Walkowiak
 * @author Mark Paluch
 * @author Hebert Coelho
 * @author Chirag Tailor
 * @author Christopher Klein
 * @since 2.0
 */
public class PagedSliceJdbcQuery extends AbstractJdbcQuery {

    private static final String PARAMETER_NEEDS_TO_BE_NAMED = "For queries with named parameters you need to provide names for method parameters; Use @Param for query method parameters, or when on Java 8+ use the javac flag -parameters";

    private final JdbcQueryMethod queryMethod;
    private final JdbcConverter converter;
    private final RowMapperFactory rowMapperFactory;
    private final ValueExpressionQueryRewriter.ParsedQuery parsedQuery;
    private final ValueExpressionDelegate delegate;

    private final CachedRowMapperFactory cachedRowMapperFactory;

    /**
     *
     * Creates a new {@link PagedSliceJdbcQuery} for the given {@link JdbcQueryMethod}, {@link RelationalMappingContext}
     * and {@link RowMapperFactory}.
     *
     * @param queryMethod must not be {@literal null}.
     * @param operations must not be {@literal null}.
     * @param rowMapperFactory must not be {@literal null}.
     * @since 2.3
     */
    public PagedSliceJdbcQuery(JdbcQueryMethod queryMethod, JdbcAggregateOperations operations,
                               RowMapperFactory rowMapperFactory,
                               ValueExpressionDelegate delegate) {

        super(queryMethod, operations.getDataAccessStrategy().getJdbcOperations());

        Assert.notNull(rowMapperFactory, "RowMapperFactory must not be null");

        this.queryMethod = queryMethod;
        this.converter = operations.getConverter();
        this.rowMapperFactory = rowMapperFactory;

        ValueExpressionQueryRewriter rewriter = ValueExpressionQueryRewriter.of(delegate,
                (counter, expression) -> String.format("__$synthetic$__%d", counter + 1), String::concat);


        this.cachedRowMapperFactory = new CachedRowMapperFactory(
                () -> rowMapperFactory.create(queryMethod.getResultProcessor().getReturnedType().getReturnedType()));
        // this.cachedResultSetExtractorFactory = new CachedResultSetExtractorFactory(
        //         this.cachedRowMapperFactory::getRowMapper);


        this.parsedQuery = rewriter.parse(queryMethod.getRequiredQuery());
        this.delegate = delegate;
    }

    @Override
    public Object execute(Object[] objects) {
        RelationalParameterAccessor accessor = new RelationalParametersParameterAccessor(getQueryMethod(), objects);
        ResultProcessor processor = getQueryMethod().getResultProcessor().withDynamicProjection(accessor);

        JdbcQueryExecution<?> queryExecution = createJdbcQueryExecution(accessor, processor);
        MapSqlParameterSource parameterMap = this.bindParameters(accessor);
        queryExecution = wrapPageableQueryExecution(accessor, parameterMap, queryExecution);
        String query = evaluateExpressions(objects, accessor.getBindableParameters(), parameterMap);
        return queryExecution.execute(enhancePageQuery(query), parameterMap);
    }

    private String enhancePageQuery(String query) {
        String original = query.trim().replace(";", "");
        return String.format("%s limit :limit offset :offset", original);
    }

    private String evaluateExpressions(Object[] objects, Parameters<?, ?> bindableParameters,
                                       MapSqlParameterSource parameterMap) {

        if (parsedQuery.hasParameterBindings()) {

            ValueEvaluationContext evaluationContext = delegate.createValueContextProvider(bindableParameters)
                    .getEvaluationContext(objects);

            parsedQuery.getParameterMap().forEach((paramName, valueExpression) -> {
                parameterMap.addValue(paramName, valueExpression.evaluate(evaluationContext));
            });

            return parsedQuery.getQueryString();
        }

        return this.queryMethod.getRequiredQuery();
    }

    private JdbcQueryExecution<?> createJdbcQueryExecution(RelationalParameterAccessor accessor,
                                                           ResultProcessor processor) {
        RowMapper<?> rowMapper = determineRowMapper(processor, accessor.findDynamicProjection() != null);
//        ResultSetExtractor<Object> resultSetExtractor = determineResultSetExtractor(rowMapper);

        return collectionQuery(rowMapper);
    }
    RowMapper<Object> determineRowMapper(ResultProcessor resultProcessor, boolean hasDynamicProjection) {

        if (cachedRowMapperFactory.isConfiguredRowMapper()) {
            return cachedRowMapperFactory.getRowMapper();
        }

        if (hasDynamicProjection) {

            RowMapper<Object> rowMapperToUse = rowMapperFactory.create(resultProcessor.getReturnedType().getDomainType());

            JdbcQueryExecution.ResultProcessingConverter _converter = new JdbcQueryExecution.ResultProcessingConverter(resultProcessor,
                    this.converter.getMappingContext(), this.converter.getEntityInstantiators());
            return new ConvertingRowMapper(rowMapperToUse, _converter);
        }

        return cachedRowMapperFactory.getRowMapper();
    }

    private MapSqlParameterSource bindParameters(RelationalParameterAccessor accessor) {

        Parameters<?, ?> bindableParameters = accessor.getBindableParameters();
        MapSqlParameterSource parameters = new MapSqlParameterSource(
                new LinkedHashMap<>(bindableParameters.getNumberOfParameters(), 1.0f));

        for (Parameter bindableParameter : bindableParameters) {

            Object value = accessor.getBindableValue(bindableParameter.getIndex());
            String parameterName = bindableParameter.getName()
                    .orElseThrow(() -> new IllegalStateException(PARAMETER_NEEDS_TO_BE_NAMED));
            JdbcParameters.JdbcParameter parameter = getQueryMethod().getParameters()
                    .getParameter(bindableParameter.getIndex());

            JdbcValue jdbcValue = writeValue(value, parameter.getTypeInformation(), parameter);
            SQLType jdbcType = jdbcValue.getJdbcType();

            if (jdbcType == null) {
                parameters.addValue(parameterName, jdbcValue.getValue());
            } else {
                parameters.addValue(parameterName, jdbcValue.getValue(), jdbcType.getVendorTypeNumber());
            }
        }

        return parameters;
    }


    private JdbcValue writeValue(@Nullable Object value, TypeInformation<?> typeInformation,
                                 JdbcParameters.JdbcParameter parameter) {

        if (value == null) {
            return JdbcValue.of(value, parameter.getSqlType());
        }

        if (typeInformation.isCollectionLike() && value instanceof Collection<?> collection) {

            TypeInformation<?> actualType = typeInformation.getActualType();

            // allow tuple-binding for collection of byte arrays to be used as BINARY,
            // we do not want to convert to column arrays.
            if (actualType != null && actualType.getType().isArray() && !actualType.getType().equals(byte[].class)) {

                TypeInformation<?> nestedElementType = actualType.getRequiredActualType();
                return writeCollection(collection, parameter.getActualSqlType(),
                        array -> writeArrayValue(parameter, array, nestedElementType));
            }

            // parameter expansion
            return writeCollection(collection, parameter.getActualSqlType(),
                    it -> converter.writeJdbcValue(it, typeInformation.getRequiredActualType(), parameter.getActualSqlType()));
        }

        SQLType sqlType = parameter.getSqlType();
        return converter.writeJdbcValue(value, typeInformation, sqlType);
    }

    private JdbcValue writeCollection(Collection<?> value, SQLType defaultType, Function<Object, Object> mapper) {

        if (value.isEmpty()) {
            return JdbcValue.of(value, defaultType);
        }

        JdbcValue jdbcValue;
        List<Object> mapped = new ArrayList<>(value.size());
        SQLType jdbcType = null;

        for (Object o : value) {

            Object mappedValue = mapper.apply(o);

            if (mappedValue instanceof JdbcValue jv) {
                if (jdbcType == null) {
                    jdbcType = jv.getJdbcType();
                }
                mappedValue = jv.getValue();
            }

            mapped.add(mappedValue);
        }

        jdbcValue = JdbcValue.of(mapped, jdbcType == null ? defaultType : jdbcType);

        return jdbcValue;
    }

    private JdbcValue writeArrayValue(JdbcParameters.JdbcParameter parameter, Object array,
                                      TypeInformation<?> nestedElementType) {

        int length = Array.getLength(array);
        Object[] mappedArray = new Object[length];
        SQLType sqlType = null;

        for (int i = 0; i < length; i++) {

            Object element = Array.get(array, i);
            JdbcValue converted = converter.writeJdbcValue(element, nestedElementType, parameter.getActualSqlType());

            if (sqlType == null && converted.getJdbcType() != null) {
                sqlType = converted.getJdbcType();
            }
            mappedArray[i] = converted.getValue();
        }

        if (sqlType == null) {
            sqlType = JdbcUtil.targetSqlTypeFor(JdbcColumnTypes.INSTANCE.resolvePrimitiveType(nestedElementType.getType()));
        }

        return JdbcValue.of(mappedArray, sqlType);
    }

    @SuppressWarnings("unchecked")
    private JdbcQueryExecution<?> wrapPageableQueryExecution(RelationalParameterAccessor accessor, MapSqlParameterSource parameterMap, JdbcQueryExecution<?> queryExecution) {
        Pageable pageable = accessor.getPageable();
        parameterMap.addValue("offset", pageable.getOffset());
        if (queryMethod.isSliceQuery()) {
            parameterMap.addValue("limit", pageable.getPageSize() + 1);
            queryExecution = new PartTreeJdbcQuery.SliceQueryExecution<>((JdbcQueryExecution<Collection<Object>>) queryExecution, pageable);
        }else if (queryMethod.isPageQuery()) {
            parameterMap.addValue("limit", pageable.getPageSize());
            queryExecution =  new PartTreeJdbcQuery.PageQueryExecution<>((JdbcQueryExecution<Collection<Object>>) queryExecution, pageable,
                    () -> {
                        String querySql = Objects.requireNonNull(getQueryMethod().getDeclaredQuery());
                        String countQuerySql = querySql.replaceFirst("(?i)select .*? from", "select count(*) from")
                                .replaceFirst("(?i) order by .*", "");
                        Object count = singleObjectQuery((rs, i) -> rs.getLong(1)).execute(countQuerySql, parameterMap);
                        return this.converter.getConversionService().convert(count, Long.class);
                    });
        }
        return queryExecution;
    }



    class CachedRowMapperFactory {

        private final Lazy<RowMapper<Object>> cachedRowMapper;
        private final boolean configuredRowMapper;
        private final @Nullable Constructor<?> constructor;

        @SuppressWarnings("unchecked")
        public CachedRowMapperFactory(Supplier<RowMapper<Object>> defaultMapper) {

            String rowMapperRef = getQueryMethod().getRowMapperRef();
            Class<?> rowMapperClass = getQueryMethod().getRowMapperClass();

            if (!ObjectUtils.isEmpty(rowMapperRef) && !isUnconfigured(rowMapperClass, RowMapper.class)) {
                throw new IllegalArgumentException(
                        "Invalid RowMapper configuration. Configure either one but not both via @Query(rowMapperRef = …, rowMapperClass = …) for query method "
                                + getQueryMethod());
            }

            this.configuredRowMapper = !ObjectUtils.isEmpty(rowMapperRef) || !isUnconfigured(rowMapperClass, RowMapper.class);
            this.constructor = rowMapperClass != null ? StringBasedJdbcQuery.findPrimaryConstructor(rowMapperClass) : null;
            this.cachedRowMapper = Lazy.of(() -> {

                if (!ObjectUtils.isEmpty(rowMapperRef)) {
                    return rowMapperFactory.getRowMapper(rowMapperRef);
                }

                if (isUnconfigured(rowMapperClass, RowMapper.class)) {
                    return defaultMapper.get();
                }

                return (RowMapper<Object>) BeanUtils.instantiateClass(constructor);
            });
        }

        public boolean isConfiguredRowMapper() {
            return configuredRowMapper;
        }

        public RowMapper<Object> getRowMapper() {
            return cachedRowMapper.get();
        }
    }

    private static boolean isUnconfigured(@Nullable Class<?> configuredClass, Class<?> defaultClass) {
        return configuredClass == null || configuredClass == defaultClass;
    }

}
