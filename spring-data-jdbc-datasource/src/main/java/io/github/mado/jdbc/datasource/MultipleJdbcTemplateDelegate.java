package io.github.mado.jdbc.datasource;

import org.springframework.dao.DataAccessException;
import org.springframework.data.util.Lazy;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author heng.ma
 */
public class MultipleJdbcTemplateDelegate extends JdbcTemplateDelegate {
    private final DataSourceContextFactory dataSourceContextFactory;

    private final Lazy<JdbcTemplate> readOnly;

    public MultipleJdbcTemplateDelegate(JdbcTemplate delegate, DataSourceContextFactory dataSourceContextFactory) {
        super(delegate);
        this.dataSourceContextFactory = dataSourceContextFactory;
        this.readOnly = dataSourceContextFactory.getReadOnlyDataSource().map(JdbcTemplate::new);
    }


    @Override
    public <T> T execute(CallableStatementCreator csc, CallableStatementCallback<T> action) throws DataAccessException {
        if (readOnly(csc)) {
            return readOnly.get().execute(csc, action);
        }
        return super.execute(csc, action);
    }

    @Override
    public <T> T execute(String callString, CallableStatementCallback<T> action) throws DataAccessException {
        if (readOnly(callString)) {
            return readOnly.get().execute(callString, action);
        }
        return super.execute(callString, action);
    }

    @Override
    public <T> T execute(PreparedStatementCreator psc, PreparedStatementCallback<T> action) throws DataAccessException {
        if (readOnly(psc)) {
            return readOnly.get().execute(psc, action);
        }
        return super.execute(psc, action);
    }

    @Override
    public <T> T execute(String sql, PreparedStatementCallback<T> action) throws DataAccessException {
        if (readOnly(sql)) {
            return readOnly.get().execute(sql, action);
        }
        return super.execute(sql, action);
    }


    @Override
    public <T> T execute(StatementCallback<T> action) throws DataAccessException {
        if (readOnly(action)) {
            return readOnly.get().execute(action);
        }
        return super.execute(action);
    }

    @Override
    public void execute(String sql) throws DataAccessException {
        if (readOnly(sql)) {
            readOnly.get().execute(sql);
        }else {
            super.execute(sql);
        }
    }


    @Override
    public <T> T query(String sql, ResultSetExtractor<T> rse) throws DataAccessException {
        if (readOnly(sql)) {
            return readOnly.get().query(sql, rse);
        }
        return super.query(sql, rse);
    }

    @Override
    public void query(String sql, RowCallbackHandler rch) throws DataAccessException {
        if (readOnly(sql)) {
            readOnly.get().query(sql, rch);
        }
        super.query(sql, rch);
    }

    @Override
    public <T> List<T> query(String sql, RowMapper<T> rowMapper) throws DataAccessException {
        if (readOnly(sql)) {
            return readOnly.get().query(sql, rowMapper);
        }
        return super.query(sql, rowMapper);
    }

    @Override
    public <T> Stream<T> queryForStream(String sql, RowMapper<T> rowMapper) throws DataAccessException {
        if (readOnly(sql)) {
            return readOnly.get().queryForStream(sql, rowMapper);
        }
        return super.queryForStream(sql, rowMapper);
    }

    @Override
    public <T> T queryForObject(String sql, RowMapper<T> rowMapper) throws DataAccessException {
        if (readOnly(sql)) {
            return readOnly.get().queryForObject(sql, rowMapper);
        }
        return super.queryForObject(sql, rowMapper);
    }

    @Override
    public <T> T queryForObject(String sql, Class<T> requiredType) throws DataAccessException {
        if (readOnly(sql)) {
            return readOnly.get().queryForObject(sql, requiredType);
        }
        return super.queryForObject(sql, requiredType);
    }

    @Override
    public Map<String, Object> queryForMap(String sql) throws DataAccessException {
        if (readOnly(sql)) {
            return readOnly.get().queryForMap(sql, sql);
        }
        return super.queryForMap(sql);
    }

    @Override
    public <T> List<T> queryForList(String sql, Class<T> elementType) throws DataAccessException {
        if (readOnly(sql)) {
            return readOnly.get().queryForList(sql, elementType);
        }
        return super.queryForList(sql, elementType);
    }

    @Override
    public List<Map<String, Object>> queryForList(String sql) throws DataAccessException {
        if (readOnly(sql)) {
            return readOnly.get().queryForList(sql);
        }
        return super.queryForList(sql);
    }

    @Override
    public <T> T query(PreparedStatementCreator psc, ResultSetExtractor<T> rse) throws DataAccessException {
        if (readOnly(psc)) {
            return readOnly.get().query(psc, rse);
        }
        return super.query(psc, rse);
    }

    @Override
    public <T> T query(String sql, PreparedStatementSetter pss, ResultSetExtractor<T> rse) throws DataAccessException {
        if (readOnly(sql)) {
            return readOnly.get().query(sql, pss, rse);
        }
        return super.query(sql, pss, rse);
    }

    @Override
    public <T> T query(String sql, Object[] args, int[] argTypes, ResultSetExtractor<T> rse) throws DataAccessException {
        if (readOnly(sql)) {
            return readOnly.get().query(sql, args, argTypes, rse);
        }
        return super.query(sql, args, argTypes, rse);
    }

    @Override
    public <T> T query(String sql, Object[] args, ResultSetExtractor<T> rse) throws DataAccessException {
        if (readOnly(sql)) {
            return readOnly.get().query(sql, args, rse);
        }
        return super.query(sql, args, rse);
    }

    @Override
    public <T> T query(String sql, ResultSetExtractor<T> rse, Object... args) throws DataAccessException {
        if (readOnly(sql)) {
            return readOnly.get().query(sql, rse, args);
        }
        return super.query(sql, rse, args);
    }

    @Override
    public void query(PreparedStatementCreator psc, RowCallbackHandler rch) throws DataAccessException {
        if (readOnly(psc)) {
            readOnly.get().query(psc, rch);
        }
        super.query(psc, rch);
    }

    @Override
    public void query(String sql, PreparedStatementSetter pss, RowCallbackHandler rch) throws DataAccessException {
        if (readOnly(sql)) {
            readOnly.get().query(sql, pss, rch);
        }
        super.query(sql, pss, rch);
    }

    @Override
    public void query(String sql, Object[] args, int[] argTypes, RowCallbackHandler rch) throws DataAccessException {
        if (readOnly(sql)) {
            readOnly.get().query(sql, args, argTypes, rch);
        }
        super.query(sql, args, argTypes, rch);
    }

    @Override
    public void query(String sql, Object[] args, RowCallbackHandler rch) throws DataAccessException {
        if (readOnly(sql)) {
            readOnly.get().query(sql, args, rch);
        }
        super.query(sql, args, rch);
    }

    @Override
    public void query(String sql, RowCallbackHandler rch, Object... args) throws DataAccessException {
        if (readOnly(sql)) {
            readOnly.get().query(sql, rch, args);
        }
        super.query(sql, rch, args);
    }

    @Override
    public <T> List<T> query(PreparedStatementCreator psc, RowMapper<T> rowMapper) throws DataAccessException {
        if (readOnly(psc)) {
            return readOnly.get().query(psc, rowMapper);
        }
        return super.query(psc, rowMapper);
    }

    @Override
    public <T> List<T> query(String sql, PreparedStatementSetter pss, RowMapper<T> rowMapper) throws DataAccessException {
        if (readOnly(sql)) {
            return readOnly.get().query(sql, pss, rowMapper);
        }
        return super.query(sql, pss, rowMapper);
    }

    @Override
    public <T> List<T> query(String sql, Object[] args, int[] argTypes, RowMapper<T> rowMapper) throws DataAccessException {
        if (readOnly(sql)) {
            return readOnly.get().query(sql, args, argTypes, rowMapper);
        }
        return super.query(sql, args, argTypes, rowMapper);
    }

    @Override
    public <T> List<T> query(String sql, Object[] args, RowMapper<T> rowMapper) throws DataAccessException {
        if (readOnly(sql)) {
            return readOnly.get().query(sql, args, rowMapper);
        }
        return super.query(sql, args, rowMapper);
    }

    @Override
    public <T> List<T> query(String sql, RowMapper<T> rowMapper, Object... args) throws DataAccessException {
        if (readOnly(sql)) {
            return readOnly.get().query(sql, rowMapper, args);
        }
        return super.query(sql, rowMapper, args);
    }

    @Override
    public <T> Stream<T> queryForStream(PreparedStatementCreator psc, RowMapper<T> rowMapper) throws DataAccessException {
        if (readOnly(psc)) {
            return readOnly.get().queryForStream(psc, rowMapper);
        }
        return super.queryForStream(psc, rowMapper);
    }

    @Override
    public <T> Stream<T> queryForStream(String sql, PreparedStatementSetter pss, RowMapper<T> rowMapper) throws DataAccessException {
        if (readOnly(sql)) {
            return readOnly.get().queryForStream(sql, pss, rowMapper);
        }
        return super.queryForStream(sql, pss, rowMapper);
    }

    @Override
    public <T> Stream<T> queryForStream(String sql, RowMapper<T> rowMapper, Object... args) throws DataAccessException {
        if (readOnly(sql)) {
            return readOnly.get().queryForStream(sql, rowMapper, args);
        }
        return super.queryForStream(sql, rowMapper, args);
    }

    @Override
    public <T> T queryForObject(String sql, Object[] args, int[] argTypes, RowMapper<T> rowMapper) throws DataAccessException {
        if (readOnly(sql)) {
            return readOnly.get().queryForObject(sql, args, argTypes, rowMapper);
        }
        return super.queryForObject(sql, args, argTypes, rowMapper);
    }

    @Override
    public <T> T queryForObject(String sql, Object[] args, RowMapper<T> rowMapper) throws DataAccessException {
        if (readOnly(sql)) {
            return readOnly.get().queryForObject(sql, args, rowMapper);
        }
        return super.queryForObject(sql, args, rowMapper);
    }

    @Override
    public <T> T queryForObject(String sql, RowMapper<T> rowMapper, Object... args) throws DataAccessException {
        if (readOnly(sql)) {
            return readOnly.get().queryForObject(sql, rowMapper, args);
        }
        return super.queryForObject(sql, rowMapper, args);
    }

    @Override
    public <T> T queryForObject(String sql, Object[] args, int[] argTypes, Class<T> requiredType) throws DataAccessException {
        if (readOnly(sql)) {
            return readOnly.get().queryForObject(sql, args, argTypes, requiredType);
        }
        return super.queryForObject(sql, args, argTypes, requiredType);
    }

    @Override
    public <T> T queryForObject(String sql, Object[] args, Class<T> requiredType) throws DataAccessException {
        if (readOnly(sql)) {
            return readOnly.get().queryForObject(sql, args, requiredType);
        }
        return super.queryForObject(sql, args, requiredType);
    }

    @Override
    public <T> T queryForObject(String sql, Class<T> requiredType, Object... args) throws DataAccessException {
        if (readOnly(sql)) {
            return readOnly.get().queryForObject(sql, requiredType, args);
        }
        return super.queryForObject(sql, requiredType, args);
    }

    @Override
    public Map<String, Object> queryForMap(String sql, Object[] args, int[] argTypes) throws DataAccessException {
        if (readOnly(sql)) {
            return readOnly.get().queryForMap(sql, args, argTypes);
        }
        return super.queryForMap(sql, args, argTypes);
    }

    @Override
    public Map<String, Object> queryForMap(String sql, Object... args) throws DataAccessException {
        if (readOnly(sql)) {
            return readOnly.get().queryForMap(sql, args);
        }
        return super.queryForMap(sql, args);
    }

    @Override
    public <T> List<T> queryForList(String sql, Object[] args, int[] argTypes, Class<T> elementType) throws DataAccessException {
        if (readOnly(sql)) {
            return readOnly.get().queryForList(sql, args, argTypes, elementType);
        }
        return super.queryForList(sql, args, argTypes, elementType);
    }

    @Override
    public <T> List<T> queryForList(String sql, Object[] args, Class<T> elementType) throws DataAccessException {
        if (readOnly(sql)) {
            return readOnly.get().queryForList(sql, args, elementType);
        }
        return super.queryForList(sql, args, elementType);
    }

    @Override
    public <T> List<T> queryForList(String sql, Class<T> elementType, Object... args) throws DataAccessException {
        if (readOnly(sql)) {
            return readOnly.get().queryForList(sql, elementType, args);
        }
        return super.queryForList(sql, elementType, args);
    }

    @Override
    public List<Map<String, Object>> queryForList(String sql, Object[] args, int[] argTypes) throws DataAccessException {
        if (readOnly(sql)) {
            return readOnly.get().queryForList(sql, args, argTypes);
        }
        return super.queryForList(sql, args, argTypes);
    }

    @Override
    public List<Map<String, Object>> queryForList(String sql, Object... args) throws DataAccessException {
        if (readOnly(sql)) {
            return readOnly.get().queryForList(sql, args);
        }
        return super.queryForList(sql, args);
    }

    @Override
    public SqlRowSet queryForRowSet(String sql, Object[] args, int[] argTypes) throws DataAccessException {
        if (readOnly(sql)) {
            return readOnly.get().queryForRowSet(sql, args, argTypes);
        }
        return super.queryForRowSet(sql, args, argTypes);
    }

    @Override
    public SqlRowSet queryForRowSet(String sql, Object... args) throws DataAccessException {
        if (readOnly(sql)) {
            return readOnly.get().queryForRowSet(sql, args);
        }
        return super.queryForRowSet(sql, args);
    }

    @Override
    public SqlRowSet queryForRowSet(String sql) throws DataAccessException {
        if (readOnly(sql)) {
            return readOnly.get().queryForRowSet(sql);
        }
        return super.queryForRowSet(sql);
    }

    private boolean readOnly(Object obj) {
        String sql = (obj instanceof SqlProvider sqlProvider ? sqlProvider.getSql() : null);

        if (StringUtils.hasLength(sql)) {
            return  readOnly(sql);
        }
        return false;
    }
    private boolean readOnly(String sql) {
        return sql.contains("READONLY");
    }
}
