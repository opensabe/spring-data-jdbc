package io.github.mado.jdbc.core;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.jdbc.core.convert.EntityRowMapper;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.jdbc.core.convert.QueryMapper;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.relational.core.conversion.IdValueSource;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;
import org.springframework.data.relational.core.sql.IdentifierProcessing;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.core.sql.render.SqlRenderer;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.RowMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author heng.ma
 */
public class GlobalSQLGeneratorSource {

    private final RelationalMappingContext context;
    private final SqlRenderer sqlRenderer;
    private final QueryMapper queryMapper;
    private final JdbcConverter converter;
    private final IdentifierProcessing identifierProcessing;
    private final PropertyAccessorCustomizer propertyAccessorCustomizer;
    private final Map<Class<?>, Generator> generators = new ConcurrentHashMap<>();
    public GlobalSQLGeneratorSource(RelationalMappingContext context,
                              SqlRenderer sqlRenderer,
                              QueryMapper queryMapper,
                              JdbcConverter converter,
                              Dialect dialect,
                              ObjectProvider<PropertyAccessorCustomizer> propertyAccessorCustomizers) {
        this.context = context;
        this.sqlRenderer = sqlRenderer;
        this.queryMapper = queryMapper;
        this.converter = converter;
        this.identifierProcessing = dialect.getIdentifierProcessing();
        this.propertyAccessorCustomizer = propertyAccessorCustomizers.stream()
                .reduce(PropertyAccessorCustomizer::then).orElse(p -> p);
    }

    <T> Generator<T> simpleSqlGenerator (Class<T> clazz) {
        return generators.computeIfAbsent(clazz, c -> {
            RelationalPersistentEntity<T> entity = (RelationalPersistentEntity<T>) context.getRequiredPersistentEntity(c);
            return new Generator<>(entity, new EntityRowMapper<>(entity, converter));
        });
    }

    class Generator<T> {

        private final RelationalPersistentEntity<T> entity;

        private final RowMapper<T> rowMapper;
        private final IdValueSource idValueSource;
        private final Table table;

        private final List<RelationalPersistentProperty> insertColumns;
        private final List<RelationalPersistentProperty> updateColumns;
        private final List<RelationalPersistentProperty> selectColumns;

        private final RelationalPersistentProperty id;

        Generator(RelationalPersistentEntity<T> entity, RowMapper<T> rowMapper) {
            this.entity = entity;
            this.rowMapper = rowMapper;
            this.idValueSource = idValueSource(entity.getIdProperty());
            this.table = Table.create(entity.getTableName());

            this.insertColumns = new ArrayList<>();
            this.updateColumns = new ArrayList<>();
            this.selectColumns = new ArrayList<>();


            this.id = entity.getIdProperty();

            entity.doWithAll(property -> {
                selectColumns.add(property);
                if (property.isWritable()) {
                    insertColumns.add(property);
                }
                if (property.isWritable() && !property.isInsertOnly()) {
                    updateColumns.add(property);
                }
            });

        }

        public IdValueSource getIdValueSource() {
            return idValueSource;
        }


        public RelationalPersistentProperty getId() {
            return id;
        }

        public RelationalPersistentEntity<T> getEntity() {
            return entity;
        }

        public PersistentPropertyAccessor<T> persistentPropertyAccessor (T instance) {
            return (PersistentPropertyAccessor<T>) propertyAccessorCustomizer.apply(entity.getPropertyAccessor(instance));
        }

        Pair<String, Object[]> insertSelective (T instance) {
            StringBuilder sql = new StringBuilder("insert into ");
            sql.append(table.getName().toSql(identifierProcessing));
            sql.append(" ");
            List<Object> args = new ArrayList<>(insertColumns.size());
            List<String> properties = new ArrayList<>(insertColumns.size());
            PersistentPropertyAccessor<T> accessor = persistentPropertyAccessor(instance);
            for (RelationalPersistentProperty property : insertColumns) {
                Object value = accessor.getProperty(property);
                if (Objects.nonNull(value)) {
                    args.add(value);
                    properties.add(property.getColumnName().toSql(identifierProcessing));
                }
            }
            sql.append(String.join(",", properties));
            sql.append("(");
            sql.append(Stream.generate(() -> "?").limit(args.size()).collect(Collectors.joining(",")));
            sql.append(")");
            return Pair.of(sql.toString(), args.toArray());
        }

        private IdValueSource idValueSource (RelationalPersistentProperty idProperty) {
            if (Objects.nonNull(idProperty) && idProperty.isWritable()) {
                return IdValueSource.GENERATED;
            }
            //如果没@Id，也有可能是联合主键
            return IdValueSource.PROVIDED;
        }


    }

}
