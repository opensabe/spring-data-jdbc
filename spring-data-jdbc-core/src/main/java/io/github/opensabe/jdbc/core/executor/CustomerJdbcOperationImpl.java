package io.github.opensabe.jdbc.core.executor;

import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.relational.core.conversion.IdValueSource;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.relational.core.dialect.IdGeneration;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.sql.IdentifierProcessing;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.util.*;

/**
 * @author heng.ma
 */
public class CustomerJdbcOperationImpl implements CustomerJdbcOperation {

    private final JdbcAggregateOperations jdbcAggregateTemplate;

    private final ExtendSQLGeneratorSource extendSQLGeneratorSource;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final IdGeneration idGeneration;

    private final IdentifierProcessing identifierProcessing;

    public CustomerJdbcOperationImpl(JdbcAggregateOperations jdbcAggregateTemplate, ExtendSQLGeneratorSource extendSQLGeneratorSource, NamedParameterJdbcTemplate namedParameterJdbcTemplate, Dialect dialect) {
        this.jdbcAggregateTemplate = jdbcAggregateTemplate;
        this.extendSQLGeneratorSource = extendSQLGeneratorSource;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.idGeneration = dialect.getIdGeneration();
         identifierProcessing = dialect.getIdentifierProcessing();
    }


    @Override
    public <T> int insertSelective(T entity, Class<T> entityClass) {
        var generator = extendSQLGeneratorSource.simpleSqlGenerator(entityClass);
        Triple<String, Object[], PersistentPropertyAccessor<T>> triple = generator.insertSelective(entity);
        int i;
        if (IdValueSource.GENERATED.equals(generator.getIdValueSource())) {
            Object key;
            KeyHolder keyHolder = new GeneratedKeyHolder();
            RelationalPersistentProperty id = generator.getId();
            String[] keyNames = null;
            if (idGeneration.driverRequiresKeyColumnNames()) {
                 keyNames = new String[]{id.getColumnName().toSql(identifierProcessing)};
            }
            i = namedParameterJdbcTemplate.getJdbcTemplate().update(new ArgumentPreparedStatementCreator(triple.first(), triple.second(), keyNames), keyHolder);
            try {
                key  = keyHolder.getKey();
            }catch (DataRetrievalFailureException | InvalidDataAccessApiUsageException e) {
                key = Optional.ofNullable(keyHolder.getKeys()).map(m -> m.get(id.getColumnName().toSql(identifierProcessing))).orElseThrow();
            }
            triple.third().setProperty(id, key);

        }else {
            i = namedParameterJdbcTemplate.getJdbcTemplate().update(triple.first(), triple.second());
        }
        return i;
    }


    @Override
    public <T> long insertList(Iterable<T> entities, Class<T> entityClass) {
        var generator = extendSQLGeneratorSource.simpleSqlGenerator(entityClass);
        Collection<T> collection;
        if (entities instanceof Collection<T> c) {
            collection = c;
        }else {
            collection = new ArrayList<>();
            entities.forEach(collection::add);
        }
        Triple<String, Object[], Map<T, PersistentPropertyAccessor<T>>> triple = generator.insertList(collection);
        int i;
        if (IdValueSource.GENERATED.equals(generator.getIdValueSource())) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            RelationalPersistentProperty id = generator.getId();
            String reference = id.getColumnName().toSql(identifierProcessing);
            Map<T, PersistentPropertyAccessor<T>> accessors = triple.third();
            if (idGeneration.driverRequiresKeyColumnNames()) {
                i = namedParameterJdbcTemplate.getJdbcTemplate().update(new ArgumentPreparedStatementCreator(triple.first(), triple.second(), new String[]{reference}), keyHolder);
            }else {
                i = namedParameterJdbcTemplate.getJdbcTemplate().update(new ArgumentPreparedStatementCreator(triple.first(), triple.second(), null), keyHolder);
            }
            List<Map<String, Object>> keys = keyHolder.getKeyList();
            int l = 0;
            for (T entity : entities) {
                Object value = keys.get(l).get(reference);
                if (Objects.nonNull(value)) {
                    accessors.get(entity).setProperty(id, value);
                }
                l ++;
            }
        }else {
            i = namedParameterJdbcTemplate.getJdbcTemplate().update(triple.first(), triple.second());
        }
        return i;
    }

    @Override
    public <T> int updateByIdSelective(T entity, Class<T> entityClass) {
        ExtendSQLGeneratorSource.Generator<T> generator = extendSQLGeneratorSource.simpleSqlGenerator(entityClass);
        Pair<String, MapSqlParameterSource> pair = generator.updateByIdSelective(entity);
        return namedParameterJdbcTemplate.update(pair.getFirst(), pair.getSecond());
    }

    @Override
    public <T> long updateSelective(T updater, Query query, Class<T> entityClass) {
        ExtendSQLGeneratorSource.Generator<T> generator = extendSQLGeneratorSource.simpleSqlGenerator(entityClass);
        Pair<String, MapSqlParameterSource> pair = generator.update(updater, query);
        return namedParameterJdbcTemplate.update(pair.getFirst(), pair.getSecond());
    }

    @Override
    public <T> long deleteAll(Query query, Class<T> entityClass) {
        var generator = extendSQLGeneratorSource.simpleSqlGenerator(entityClass);
        Pair<String, MapSqlParameterSource> pair = generator.deleteAll(query);
        return namedParameterJdbcTemplate.update(pair.getFirst(), pair.getSecond());
    }

    @Override
    public <T> Optional<T> findOne(Query query, Class<T> entityClass) {
        return jdbcAggregateTemplate.findOne(query.limit(1), entityClass);
    }

    @Override
    public <T> List<T> findAll(Query query, Class<T> entityClass) {
        return new ArrayList<>(jdbcAggregateTemplate.findAll(query, entityClass));
    }

    @Override
    public <T> Page<T> findAll(Query query, Pageable pageable, Class<T> entityClass) {
        return jdbcAggregateTemplate.findAll(query, entityClass, pageable);
    }

    @Override
    public <T> long count(Query query, Class<T> entityClass) {
        return jdbcAggregateTemplate.count(query, entityClass);
    }

    @Override
    public <T> boolean exists(Query query, Class<T> entityClass) {
        return jdbcAggregateTemplate.exists(query, entityClass);
    }


    @Override
    public int deleteById(Object id, Class<?> entityClass) {
        ExtendSQLGeneratorSource.Generator<?> generator = extendSQLGeneratorSource.simpleSqlGenerator(entityClass);
        return namedParameterJdbcTemplate.getJdbcTemplate().update(generator.deleteById(), id);
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public int deleteAllById(Iterable<?> ids, Class<?> entityClass) {
        ExtendSQLGeneratorSource.Generator<?> generator = extendSQLGeneratorSource.simpleSqlGenerator(entityClass);

        Collection collection;
        if (ids instanceof Collection c) {
            collection = c;
        }else {
            collection = new ArrayList<>();
            ids.forEach(collection::add);
        }
        String sql = generator.deleteByIds(collection.size());
        return namedParameterJdbcTemplate.getJdbcTemplate().update(sql, collection.toArray());
    }

    @Override
    public long deleteAll(Class<?> entityClass) {
        ExtendSQLGeneratorSource.Generator<?> generator = extendSQLGeneratorSource.simpleSqlGenerator(entityClass);
        return namedParameterJdbcTemplate.getJdbcTemplate().update(generator.deleteAll());
    }

    @Override
    public int deleteAllById(Object[] ids, Class<?> entityClass) {
        ExtendSQLGeneratorSource.Generator<?> generator = extendSQLGeneratorSource.simpleSqlGenerator(entityClass);
        return namedParameterJdbcTemplate.getJdbcTemplate().update(generator.deleteByIds(ids.length), ids);
    }

    @Override
    public <T> Optional<T> findById(Object id, Class<T> entityClass, String table) {
        ExtendSQLGeneratorSource.Generator<T> generator = extendSQLGeneratorSource.simpleSqlGenerator(entityClass);
        String sql = generator.findByIdTable(table);
        return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(sql, Map.of("id", id), generator.getEntityRowMapper()));
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public <T> List<T> findAllById(Iterable<?> ids, Class<T> entityClass, String table) {
        ExtendSQLGeneratorSource.Generator<T> generator = extendSQLGeneratorSource.simpleSqlGenerator(entityClass);
        Collection collection;
        if (ids instanceof Collection c) {
            collection = c;
        }else {
            collection = new ArrayList<>();
            ids.forEach(collection::add);
        }
        String sql = generator.findAllByIdTable(table, collection.size());
        return namedParameterJdbcTemplate.getJdbcTemplate().query(sql, generator.getEntityRowMapper(),collection.toArray());
    }

    @Override
    public <T> List<T> findAll(Query query, Class<T> entityClass, String table) {
        ExtendSQLGeneratorSource.Generator<T> generator = extendSQLGeneratorSource.simpleSqlGenerator(entityClass);
        Pair<String, MapSqlParameterSource> pair = generator.findAllTable(query, table);
        return namedParameterJdbcTemplate.query(pair.getFirst(), pair.getSecond(), generator.getEntityRowMapper());
    }

    @Override
    public <T> Page<T> findAll(Query query, Pageable pageable, Class<T> entityClass, String table) {
        ExtendSQLGeneratorSource.Generator<T> generator = extendSQLGeneratorSource.simpleSqlGenerator(entityClass);
        Pair<String, MapSqlParameterSource> pair = generator.findPageTable(query, pageable, table);
        return PageableExecutionUtils.getPage(namedParameterJdbcTemplate.query(pair.getFirst(), pair.getSecond(), generator.getEntityRowMapper()), pageable, () -> count(query, entityClass, table));
    }

    @Override
    public <T> long count(Query query, Class<T> entityClass, String table) {
        ExtendSQLGeneratorSource.Generator<T> generator = extendSQLGeneratorSource.simpleSqlGenerator(entityClass);
        Pair<String, MapSqlParameterSource> pair = generator.count(query, table);
        return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(pair.getFirst(), pair.getSecond(), Long.class))
                .orElse(0L);
    }

    @Override
    public <T> boolean exists(Query query, Class<T> entityClass, String table) {
        ExtendSQLGeneratorSource.Generator<T> generator = extendSQLGeneratorSource.simpleSqlGenerator(entityClass);
        Pair<String, MapSqlParameterSource> pair = generator.exists(query, table);
        return Boolean.TRUE.equals(namedParameterJdbcTemplate.queryForObject(pair.getFirst(), pair.getSecond(), Boolean.class));
    }

    @Override
    public <T> boolean existsById(Object id, Class<T> entityClass, String table) {
        ExtendSQLGeneratorSource.Generator<T> generator = extendSQLGeneratorSource.simpleSqlGenerator(entityClass);
        String sql = generator.existsById(table);
        return Boolean.TRUE.equals(namedParameterJdbcTemplate.queryForObject(sql, Map.of("id", id), Boolean.class));
    }
}
