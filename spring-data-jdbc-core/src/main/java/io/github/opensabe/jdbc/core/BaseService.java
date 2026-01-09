package io.github.opensabe.jdbc.core;

import io.github.opensabe.jdbc.core.lambda.Weekend;
import io.github.opensabe.jdbc.core.repository.BaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.data.util.Lazy;
import org.springframework.data.util.TypeInformation;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.util.*;

/**
 * @author heng.ma
 */
public abstract class BaseService<T, ID> implements IService<T, ID>, MagicQuery {

    private final Class<T> entityClass;

    private String tableName;

    private final Lazy<ArchiveService<T, ID>> archive;

    private BaseRepository<T, ID> repository;

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private BeanRowMapperFactory beanRowMapperFactory;

    @SuppressWarnings("unchecked")
    public BaseService() {
        this.entityClass = (Class<T>) TypeInformation
                .of(this.getClass())
                .getSuperTypeInformation(BaseService.class)
                .getTypeArguments().get(0).getType();

        this.archive = Lazy.of(() -> new ArchiveService<>(getRepository(), tableName+"_his"));
    }


    @Autowired
    public void setBeanRowMapperFactory(BeanRowMapperFactory beanRowMapperFactory) {
        this.beanRowMapperFactory = beanRowMapperFactory;
    }

    @Autowired
    @SuppressWarnings("unused")
    public void setRelationalMappingContext(RelationalMappingContext relationalMappingContext) {
        this.tableName = relationalMappingContext.getRequiredPersistentEntity(entityClass).getQualifiedTableName().getReference();
    }

    public ArchiveService<T, ID> archive () {
        return archive.get();
    }

    @Autowired
    public void setRepository(BaseRepository<T, ID> repository) {
        this.repository = repository;
    }

    @Autowired
    public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public BaseRepository<T, ID> getRepository() {
        return repository;
    }

    public Example<T> getExample (T entity) {
        return Example.of(entity, ExampleMatcher.matching().withIgnoreNullValues());
    }


    @Override
    public Optional<T> selectById(ID id) {
        return repository.findById(id);
    }

    @Override
    public List<T> selectByIds(List<ID> ids) {
        return repository.findAllById(ids);
    }

    @Override
    public Optional<T> selectOne(Sort sort) {
        return repository.findOne(sort);
    }

    @Override
    public Optional<T> selectOne(T entity, Sort sort) {
        return repository.findOne(getExample(entity), sort);
    }

    @Override
    public Optional<T> selectOne(Weekend<T> weekend, Sort sort) {
        return repository.findOne(weekend, sort);
    }

    @Override
    public List<T> select() {
        return repository.findAll();
    }

    @Override
    public List<T> select(Sort sort) {
        return repository.findAll(sort);
    }

    @Override
    public List<T> select(T entity, Sort sort) {
        return repository.findAll(getExample(entity), sort);
    }

    @Override
    public List<T> select(Weekend<T> weekend, Sort sort) {
        return repository.findAll(weekend, sort);
    }

    @Override
    public Page<T> select(T entity, Pageable pageable) {
        return repository.findAll(getExample(entity), pageable);
    }

    @Override
    public Page<T> select(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Page<T> select(Weekend<T> weekend, Pageable pageable) {
        return repository.findAll(weekend, pageable);
    }

    @Override
    public List<T> selectByLimit(Weekend<T> weekend, int limit, Sort sort) {
        return repository.findLimit(weekend, limit, sort);
    }

    @Override
    public List<T> selectByLimit(T entity, int limit, Sort sort) {
        return repository.findLimit(getExample(entity), limit, sort);
    }

    @Override
    public List<T> selectByLimit(int limit, Sort sort) {
        return repository.findLimit(limit, sort);
    }

    @Override
    public boolean existsById(ID id) {
        return repository.existsById(id);
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public long count(T entity) {
        return repository.count(getExample(entity));
    }

    @Override
    public long count(Weekend<T> weekend) {
        return repository.count(weekend);
    }

    @Override
    public boolean exists(T entity) {
        return repository.exists(getExample(entity));
    }

    @Override
    public boolean exists(Weekend<T> weekend) {
        return repository.exists(weekend);
    }

    @Override
    public int insertSelective(T entity) {
        return repository.insertSelective(entity);
    }

    @Override
    public long insertList(List<T> entities) {
        return repository.insertList(entities);
    }

    @Override
    public T updateById(T entity) {
        return repository.updateById(entity);
    }

    @Override
    public int updateByIdSelective(T entity) {
        return repository.updateByIdSelective(entity);
    }

    @Override
    public long updateSelective(T entity, T query) {
        return repository.updateSelective(entity, getExample(query));
    }

    @Override
    public long updateSelective(T entity, Weekend<T> weekend) {
        return repository.updateSelective(entity, weekend);
    }

    @Override
    public int deleteById(ID id) {
        return repository.deleteById(id);
    }

    @Override
    public int deleteAllById(Iterable<ID> ids) {
        return repository.deleteAllById(ids);
    }

    @Override
    @SafeVarargs
    public final int deleteAllById(ID... ids) {
        return repository.deleteAllById(ids);
    }

    @Override
    public long deleteAll(T entity) {
        return repository.deleteAll(getExample(entity));
    }

    @Override
    public long deleteAll(Weekend<T> weekend) {
        return repository.deleteAll(weekend);
    }


    @Override
    public <E> List<E> selectList(String sql, Map<String, Object> params, Class<E> elementType) {
        return namedParameterJdbcTemplate.queryForList(sql, params, elementType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> Page<E> selectPage(String sql, Map<String, Object> params, Pageable pageable, Class<E> elementType) {
        String countSql = resolveCountSql(sql);
        String selectSql = enhancePageQuery(sql);
        //to prevent params.put throw UnsupportedOperationException
        Map<String, Object> parameters = new HashMap<>(params == null? 2 : params.size()+2);
        if (params != null) {
            parameters.putAll(params);
        }
        parameters.put(PageableBeanPropertySqlParameterSource.LIMIT_PARAMETER_NAME, pageable.getPageSize());
        parameters.put(PageableBeanPropertySqlParameterSource.OFFSET_PARAMETER_NAME, pageable.getOffset());
        RowMapper<? extends E> rowMapper = beanRowMapperFactory.getRowMapper(elementType);
        return PageableExecutionUtils.getPage(namedParameterJdbcTemplate.query(selectSql, parameters, (RowMapper<E>)Objects.requireNonNull(rowMapper)),
                pageable,
                () -> namedParameterJdbcTemplate.queryForObject(countSql, params, Long.class));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> Optional<E> selectOne(String sql, Map<String, Object> params, Class<E> resultType) {
        RowMapper<? extends E> rowMapper = beanRowMapperFactory.getRowMapper(resultType);
        return Optional.ofNullable(namedParameterJdbcTemplate.query(sql, params, (RowMapper<E>) Objects.requireNonNull(rowMapper)))
                .stream()
                .flatMap(List::stream).findFirst();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E, P> List<E> selectList(String sql, P params, Class<E> elementType) {
        RowMapper<? extends E> rowMapper = beanRowMapperFactory.getRowMapper(elementType);
        return namedParameterJdbcTemplate.query(sql, new BeanPropertySqlParameterSource(params),(RowMapper<E>) Objects.requireNonNull(rowMapper));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E, P> Page<E> selectPage(String sql, P params, Pageable pageable, Class<E> elementType) {
        String countSql = resolveCountSql(sql);
        String selectSql = enhancePageQuery(sql);
        SqlParameterSource parameterSource = new PageableBeanPropertySqlParameterSource(params, pageable);
        RowMapper<? extends E> rowMapper = beanRowMapperFactory.getRowMapper(elementType);
        return PageableExecutionUtils.getPage(namedParameterJdbcTemplate.query(selectSql, parameterSource, (RowMapper<E>) Objects.requireNonNull(rowMapper)),
                pageable,
                () -> namedParameterJdbcTemplate.queryForObject(countSql, parameterSource, Long.class
                ));
    }

    @Override
    public <E, P> Optional<E> selectOne(String sql, P params, Class<E> resultType) {
        RowMapper<? extends E> rowMapper = beanRowMapperFactory.getRowMapper(resultType);
        if (Objects.isNull(rowMapper)) {
            return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(sql, new BeanPropertySqlParameterSource(params), resultType));
        }else {
            return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(sql, new BeanPropertySqlParameterSource(params), rowMapper));
        }
    }

    private String resolveCountSql(String sql) {
        return sql.replaceFirst("(?i)select .*? from", "select count(*) from")
                .replaceFirst("(?i) order by .*", "");
    }

    private String enhancePageQuery(String query) {
        String original = query.trim().replace(";", "");
        return String.format("%s limit :limit offset :offset", original);
    }
}
