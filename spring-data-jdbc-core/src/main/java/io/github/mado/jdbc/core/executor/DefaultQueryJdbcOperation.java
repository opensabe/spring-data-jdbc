package io.github.mado.jdbc.core.executor;

import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.relational.core.conversion.IdValueSource;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.relational.core.dialect.IdGeneration;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.sql.IdentifierProcessing;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.util.*;

/**
 * @author heng.ma
 */
public class DefaultQueryJdbcOperation implements QueryJdbcOperation {

    public static DefaultQueryJdbcOperation INSTANCE;

    private final JdbcAggregateTemplate jdbcAggregateTemplate;

    private final GlobalSQLGeneratorSource sqlGeneratorSource;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final IdGeneration idGeneration;

    private final IdentifierProcessing identifierProcessing;


    public DefaultQueryJdbcOperation(JdbcAggregateTemplate jdbcAggregateTemplate, GlobalSQLGeneratorSource sqlGeneratorSource, NamedParameterJdbcTemplate namedParameterJdbcTemplate, Dialect dialect) {
        this.jdbcAggregateTemplate = jdbcAggregateTemplate;
        this.sqlGeneratorSource = sqlGeneratorSource;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.idGeneration = dialect.getIdGeneration();
         identifierProcessing = dialect.getIdentifierProcessing();
        INSTANCE = this;
    }


    @Override
    public <T> int insertSelective(T entity, Class<T> entityClass) {
        var generator = sqlGeneratorSource.simpleSqlGenerator(entityClass);
        Triple<String, Object[], PersistentPropertyAccessor<T>> triple = generator.insertSelective(entity);
        int i;
        if (IdValueSource.GENERATED.equals(generator.getIdValueSource())) {
            Object key;
            KeyHolder keyHolder = new GeneratedKeyHolder();
            RelationalPersistentProperty id = generator.getId();
            String[] keyNames = null;
            if (idGeneration.driverRequiresKeyColumnNames()) {
                 keyNames = new String[]{id.getColumnName().getReference(identifierProcessing)};
            }
            i = namedParameterJdbcTemplate.getJdbcTemplate().update(new ArgumentPreparedStatementCreator(triple.first(), triple.second(), keyHolder, keyNames));
            try {
                key  = keyHolder.getKeyAs(id.getType());
            }catch (DataRetrievalFailureException | InvalidDataAccessApiUsageException e) {
                key = Optional.ofNullable(keyHolder.getKeys()).map(m -> m.get(id.getColumnName().toSql(identifierProcessing)));
            }
            triple.third().setProperty(id, key);

        }else {
            i = namedParameterJdbcTemplate.getJdbcTemplate().update(triple.first(), triple.second());
        }
        return i;
    }


    @Override
    public <T> long insertList(Iterable<T> entities, Class<T> entityClass) {
        var generator = sqlGeneratorSource.simpleSqlGenerator(entityClass);
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
            String reference = id.getColumnName().getReference(identifierProcessing);
            Map<T, PersistentPropertyAccessor<T>> accessors = triple.third();
            if (idGeneration.driverRequiresKeyColumnNames()) {
                i = namedParameterJdbcTemplate.getJdbcTemplate().update(new ArgumentPreparedStatementCreator(triple.first(), triple.second(), keyHolder, new String[]{reference}));
            }else {
                i = namedParameterJdbcTemplate.getJdbcTemplate().update(new ArgumentPreparedStatementCreator(triple.first(), triple.second(), keyHolder));
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
    public <T> long updateByIdSelective(T entity, Class<T> entityClass) {
        GlobalSQLGeneratorSource.Generator<T> generator = sqlGeneratorSource.simpleSqlGenerator(entityClass);
        Pair<String, MapSqlParameterSource> pair = generator.updateByIdSelective(entity);
        return namedParameterJdbcTemplate.update(pair.getFirst(), pair.getSecond());
    }

    @Override
    public <T> long updateSelective(T updater, Query query, Class<T> entityClass) {
        GlobalSQLGeneratorSource.Generator<T> generator = sqlGeneratorSource.simpleSqlGenerator(entityClass);
        Pair<String, MapSqlParameterSource> pair = generator.update(updater, query);
        return namedParameterJdbcTemplate.update(pair.getFirst(), pair.getSecond());
    }

    @Override
    public <T> long deleteAll(Query query, Class<T> entityClass) {
        var generator = sqlGeneratorSource.simpleSqlGenerator(entityClass);
        Pair<String, MapSqlParameterSource> pair = generator.deleteAll(query);
        return namedParameterJdbcTemplate.update(pair.getFirst(), pair.getSecond());
    }

    @Override
    public <T> Optional<T> findOne(Query query, Class<T> entityClass) {
        return jdbcAggregateTemplate.findOne(query.limit(1), entityClass);
    }

    @Override
    public <T> Iterable<T> findAll(Query query, Class<T> entityClass) {
        return jdbcAggregateTemplate.findAll(query, entityClass);
    }

    @Override
    public <T> Page<T> findAll(Query query, Pageable pageable, Class<T> entityClass) {
        return jdbcAggregateTemplate.findAll(query, entityClass, pageable);
    }

    @Override
    public <T> long count(Query query, Class<T> entityClass) {
        return jdbcAggregateTemplate.count(query, entityClass);
    }


}
