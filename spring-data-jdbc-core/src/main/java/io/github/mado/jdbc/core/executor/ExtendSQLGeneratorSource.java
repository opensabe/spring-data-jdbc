package io.github.mado.jdbc.core.executor;

import org.springframework.data.jdbc.core.convert.EntityRowMapper;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.jdbc.core.convert.QueryMapper;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.relational.core.conversion.IdValueSource;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.relational.core.dialect.RenderContextFactory;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.sql.*;
import org.springframework.data.relational.core.sql.render.SqlRenderer;
import org.springframework.data.util.Lazy;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author heng.ma
 */
public class ExtendSQLGeneratorSource {

    private final RelationalMappingContext context;
    private final SqlRenderer sqlRenderer;
    private final QueryMapper queryMapper;
    private final JdbcConverter converter;
    private final IdentifierProcessing identifierProcessing;
    private final PropertyAccessorCustomizer propertyAccessorCustomizer;
    @SuppressWarnings("rawtypes")
    private final Map<Class<?>, Generator> generators = new ConcurrentHashMap<>();
    public ExtendSQLGeneratorSource(RelationalMappingContext context,
                              JdbcConverter converter,
                              Dialect dialect,
                              List<PropertyAccessorCustomizer> propertyAccessorCustomizers) {
        this.context = context;
        this.converter = converter;

        this.queryMapper = new QueryMapper(dialect, converter);;
        this.sqlRenderer = SqlRenderer.create(new RenderContextFactory(dialect).createRenderContext());;
        this.identifierProcessing = dialect.getIdentifierProcessing();
        this.propertyAccessorCustomizer = propertyAccessorCustomizers.stream()
                .reduce(PropertyAccessorCustomizer::then).orElse(p -> p);
    }

    @SuppressWarnings("unchecked")
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


        private final String insertPrefix;




        private final Lazy<String> deleteById = Lazy.of(this::getDeleteById);
        private final Lazy<String> deleteAll = Lazy.of(this::getDeleteAll);








        Generator(RelationalPersistentEntity<T> entity, RowMapper<T> rowMapper) {
            this.entity = entity;
            this.rowMapper = rowMapper;
            this.idValueSource = idValueSource(entity.getIdProperty());
            this.table = Table.create(entity.getTableName());

            this.insertColumns = new ArrayList<>();
            this.updateColumns = new ArrayList<>();
            this.selectColumns = new ArrayList<>();


            this.id = entity.getIdProperty();

            StringBuilder insert = new StringBuilder("insert into ")
                    .append(table.getName().toSql(identifierProcessing))
                    .append(" ");

            entity.doWithAll(property -> {
                selectColumns.add(property);
                if (property.isWritable()) {
                    insertColumns.add(property);
                }
                if (property.isWritable() && !property.isInsertOnly()) {
                    updateColumns.add(property);
                }
            });

            String inserts = insertColumns.stream().map(p -> p.getColumnName().getReference(identifierProcessing)).collect(Collectors.joining(","));

            insert.append("(").append(inserts).append(")").append(" values ");

            this.insertPrefix = insert.toString();
        }

        public IdValueSource getIdValueSource() {
            return idValueSource;
        }


        public RelationalPersistentProperty getId() {
            return id;
        }


        public String deleteById () {
            return deleteById.get();
        }

        public String deleteAll () {
            return deleteAll.get();
        }

        public String deleteByIds (int count) {
            StringBuilder sql = new StringBuilder("delete from ")
                    .append(table.getReferenceName().toSql(identifierProcessing))
                    .append(" where ")
                    .append(id.getColumnName().toSql(identifierProcessing))
                    .append(" in (");
            String params = Stream.generate(() -> "?").limit(count).collect(Collectors.joining(","));
            sql.append(params).append(")");
            return sql.toString();
        }


        @SuppressWarnings("unchecked")
        public PersistentPropertyAccessor<T> persistentPropertyAccessor (T instance) {
            return (PersistentPropertyAccessor<T>) propertyAccessorCustomizer.apply(entity.getPropertyAccessor(instance));
        }

        @SuppressWarnings("unchecked")
        Triple<String, Object[], PersistentPropertyAccessor<T>> insertSelective (T instance) {
            StringBuilder sql = new StringBuilder("insert into ");
            sql.append(table.getName().toSql(identifierProcessing));
            sql.append(" (");
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
            sql.append(") values (");
            sql.append(Stream.generate(() -> "?").limit(args.size()).collect(Collectors.joining(",")));
            sql.append(")");

            return Triple.of(sql.toString(), args.toArray(), accessor);
        }

        @SuppressWarnings("unchecked")
        Triple<String, Object[], Map<T, PersistentPropertyAccessor<T>>> insertList (Collection<T> instances) {
            int size = instances.size();
            Map<T, PersistentPropertyAccessor<T>> accessors = new IdentityHashMap<>(size);
            String params = Stream.generate(() -> "?").limit(insertColumns.size()).collect(Collectors.joining(","));
            String line = "(" + params + ")";
            String lines = Stream.generate(() -> line).limit(size).collect(Collectors.joining(","));
            String sql = insertPrefix + lines;
            List<Object> args = new ArrayList<>(insertColumns.size() * size);
            instances.forEach(entity -> insertColumns.forEach(property -> {
                PersistentPropertyAccessor<T> accessor = accessors.computeIfAbsent(entity, this::persistentPropertyAccessor);
                args.add(accessor.getProperty(property));
            }));
            return Triple.of(sql, args.toArray(), accessors);
        }


        Pair<String, MapSqlParameterSource> deleteAll (Query query) {
            DeleteBuilder.DeleteWhere deleteWhere = Delete.builder().from(table);
            MapSqlParameterSource parameterSource = new MapSqlParameterSource();
            if (Objects.isNull(query) || query.getCriteria().isEmpty()) {
                return Pair.of(sqlRenderer.render(deleteWhere.build()), parameterSource);
            }
            Condition condition = queryMapper.getMappedObject(parameterSource, query.getCriteria().get(), table, entity);
            Delete delete = deleteWhere.where(condition).build();
            return Pair.of(sqlRenderer.render(delete), parameterSource);
        }

        Pair<String, MapSqlParameterSource> updateByIdSelective (T instance) {
            UpdateBuilder.UpdateAssign assign = Update.builder().table(table);
            PersistentPropertyAccessor<T> accessor = persistentPropertyAccessor(instance);
            MapSqlParameterSource parameterSource = new MapSqlParameterSource();
            List<AssignValue> set = new ArrayList<>(updateColumns.size());
            for (RelationalPersistentProperty property : updateColumns) {
                Object value = accessor.getProperty(property);
                if (Objects.nonNull(value)) {
                    String name = "set"+property.getName();
                    parameterSource.addValue(name, value);
                    set.add(Assignments.value(table.column(property.getColumnName()), SQL.bindMarker(":"+name)));
                }
            }
            Update update = assign.set(set).where(table.column(id.getColumnName()).isEqualTo(SQL.bindMarker(":" + id.getColumnName().getReference(identifierProcessing)))).build();
            parameterSource.addValue(id.getColumnName().getReference(identifierProcessing), accessor.getProperty(id));
            return Pair.of(sqlRenderer.render(update), parameterSource);
        }

        Pair<String, MapSqlParameterSource> update (T instance, Query query) {
            UpdateBuilder.UpdateAssign assign = Update.builder().table(table);
            PersistentPropertyAccessor<T> accessor = persistentPropertyAccessor(instance);
            MapSqlParameterSource parameterSource = new MapSqlParameterSource();
            List<AssignValue> set = new ArrayList<>(updateColumns.size());
            for (RelationalPersistentProperty property : updateColumns) {
                Object value = accessor.getProperty(property);
                if (Objects.nonNull(value)) {
                    String name = "set"+property.getName();
                    parameterSource.addValue(name, value);
                    set.add(Assignments.value(table.column(property.getColumnName()), SQL.bindMarker(":"+name)));
                }
            }
            UpdateBuilder.UpdateWhere where = assign.set(set);
            Condition condition = queryMapper.getMappedObject(parameterSource, query.getCriteria().orElseThrow(), table, entity);
            Update update = where.where(condition).build();
            return Pair.of(sqlRenderer.render(update), parameterSource);
        }

        private IdValueSource idValueSource (RelationalPersistentProperty idProperty) {
            if (Objects.nonNull(idProperty) && !idProperty.isWritable()) {
                return IdValueSource.GENERATED;
            }
            //如果没@Id，也有可能是联合主键
            return IdValueSource.PROVIDED;
        }

        private String getDeleteById () {
            Delete delete = Delete.builder()
                    .from(table)
                    .where(table.column(id.getColumnName()).isEqualTo(Expressions.just("?")))
                    .build();
            return sqlRenderer.render(delete);
        }
        private String getDeleteAll () {
            Delete delete = Delete.builder()
                    .from(table)
                    .build();
            return sqlRenderer.render(delete);
        }

    }

}
