package io.github.opensabe.jdbc.core;

import org.springframework.core.convert.ConversionService;
import org.springframework.data.jdbc.repository.QueryMappingConfiguration;
import org.springframework.data.jdbc.repository.config.DefaultQueryMappingConfiguration;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BeanRowMapperFactory {

    private final QueryMappingConfiguration queryMappingConfiguration;
    private final RelationalMappingContext relationalMappingContext;
    private final ConversionService conversionService;
    private final ExecutorService executorService;

    public BeanRowMapperFactory(QueryMappingConfiguration queryMappingConfiguration, RelationalMappingContext relationalMappingContext, ConversionService conversionService) {
        if (Objects.isNull(queryMappingConfiguration)) {
            this.queryMappingConfiguration = QueryMappingConfiguration.EMPTY;
        }else {
            this.queryMappingConfiguration = queryMappingConfiguration;
        }
        this.relationalMappingContext = relationalMappingContext;
        this.executorService = Executors.newSingleThreadExecutor();
        this.conversionService = conversionService;
    }


    /**
     * 获取Bean的RowMapper，暂时不支持自定义Converter
     * @param type 要转换的类型
     * @return  如果type为基本类型（Integer,LocalDateTime等），返回Null
     * @param <T>   要转换的类型
     */
    @Nullable
    public <T> RowMapper<? extends T> getRowMapper (Class<T> type) {
        RowMapper<? extends T> mapper = queryMappingConfiguration.getRowMapper(type);
        if (Objects.nonNull(mapper)) {
            return mapper;
        }
        RelationalPersistentEntity<?> entity = relationalMappingContext.getPersistentEntity(type);
        if (Objects.nonNull(entity)) {
            if (type.isRecord()) {
                mapper = DataClassRowMapper.newInstance(type, conversionService);
            }else {
                mapper = BeanPropertyRowMapper.newInstance(type, conversionService);
            }
            if (queryMappingConfiguration instanceof DefaultQueryMappingConfiguration defaultQueryMappingConfiguration) {
                RowMapper<? extends T> rowMapper = mapper;
                executorService.submit(() -> defaultQueryMappingConfiguration.registerRowMapper(type, rowMapper));
            }
        }
        return mapper;
    }
}
