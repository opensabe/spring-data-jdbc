package io.github.mado.jdbc.datasource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author heng.ma
 */
public class JdbcTemplateDelegate extends JdbcTemplate {

    private final JdbcTemplate delegate;

    public JdbcTemplateDelegate(JdbcTemplate delegate) {
        this.delegate = delegate;
    }

    @Override
    public <T> T execute(ConnectionCallback<T> action) throws DataAccessException {
        return delegate.execute(action);
    }

    @Override
    public <T> T execute(StatementCallback<T> action) throws DataAccessException {
        return delegate.execute(action);
    }

    @Override
    public void execute(String sql) throws DataAccessException {
        delegate.execute(sql);
    }

    @Override
    public <T> T query(String sql, ResultSetExtractor<T> rse) throws DataAccessException {
        return delegate.query(sql, rse);
    }

    @Override
    public void query(String sql, RowCallbackHandler rch) throws DataAccessException {
        delegate.query(sql, rch);
    }

    @Override
    public <T> List<T> query(String sql, RowMapper<T> rowMapper) throws DataAccessException {
        return delegate.query(sql, rowMapper);
    }

    @Override
    public <T> Stream<T> queryForStream(String sql, RowMapper<T> rowMapper) throws DataAccessException {
        return delegate.queryForStream(sql, rowMapper);
    }

    @Override
    public <T> T queryForObject(String sql, RowMapper<T> rowMapper) throws DataAccessException {
        return delegate.queryForObject(sql, rowMapper);
    }

    @Override
    public <T> T queryForObject(String sql, Class<T> requiredType) throws DataAccessException {
        return delegate.queryForObject(sql, requiredType);
    }

    @Override
    public Map<String, Object> queryForMap(String sql) throws DataAccessException {
        return delegate.queryForMap(sql);
    }

    @Override
    public <T> List<T> queryForList(String sql, Class<T> elementType) throws DataAccessException {
        return delegate.queryForList(sql, elementType);
    }

    @Override
    public List<Map<String, Object>> queryForList(String sql) throws DataAccessException {
        return delegate.queryForList(sql);
    }

    @Override
    public SqlRowSet queryForRowSet(String sql) throws DataAccessException {
        return delegate.queryForRowSet(sql);
    }

    @Override
    public int update(String sql) throws DataAccessException {
        return delegate.update(sql);
    }

    @Override
    public int[] batchUpdate(String... sql) throws DataAccessException {
        return delegate.batchUpdate(sql);
    }

    @Override
    public <T> T execute(PreparedStatementCreator psc, PreparedStatementCallback<T> action) throws DataAccessException {
        return delegate.execute(psc, action);
    }

    @Override
    public <T> T execute(String sql, PreparedStatementCallback<T> action) throws DataAccessException {
        return delegate.execute(sql, action);
    }

    @Override
    public <T> T query(PreparedStatementCreator psc, ResultSetExtractor<T> rse) throws DataAccessException {
        return delegate.query(psc, rse);
    }

    @Override
    public <T> T query(String sql, PreparedStatementSetter pss, ResultSetExtractor<T> rse) throws DataAccessException {
        return delegate.query(sql, pss, rse);
    }

    @Override
    public <T> T query(String sql, Object[] args, int[] argTypes, ResultSetExtractor<T> rse) throws DataAccessException {
        return delegate.query(sql, args, argTypes, rse);
    }

    @Override
    public <T> T query(String sql, Object[] args, ResultSetExtractor<T> rse) throws DataAccessException {
        return delegate.query(sql, args, rse);
    }

    @Override
    public <T> T query(String sql, ResultSetExtractor<T> rse, Object... args) throws DataAccessException {
        return delegate.query(sql, rse, args);
    }

    @Override
    public void query(PreparedStatementCreator psc, RowCallbackHandler rch) throws DataAccessException {
        delegate.query(psc, rch);
    }

    @Override
    public void query(String sql, PreparedStatementSetter pss, RowCallbackHandler rch) throws DataAccessException {
        delegate.query(sql, pss, rch);
    }

    @Override
    public void query(String sql, Object[] args, int[] argTypes, RowCallbackHandler rch) throws DataAccessException {
        delegate.query(sql, args, argTypes, rch);
    }

    @Override
    public void query(String sql, Object[] args, RowCallbackHandler rch) throws DataAccessException {
        delegate.query(sql, args, rch);
    }

    @Override
    public void query(String sql, RowCallbackHandler rch, Object... args) throws DataAccessException {
        delegate.query(sql, rch, args);
    }

    @Override
    public <T> List<T> query(PreparedStatementCreator psc, RowMapper<T> rowMapper) throws DataAccessException {
        return delegate.query(psc, rowMapper);
    }

    @Override
    public <T> List<T> query(String sql, PreparedStatementSetter pss, RowMapper<T> rowMapper) throws DataAccessException {
        return delegate.query(sql, pss, rowMapper);
    }

    @Override
    public <T> List<T> query(String sql, Object[] args, int[] argTypes, RowMapper<T> rowMapper) throws DataAccessException {
        return delegate.query(sql, args, argTypes, rowMapper);
    }

    @Override
    public <T> List<T> query(String sql, Object[] args, RowMapper<T> rowMapper) throws DataAccessException {
        return delegate.query(sql, args, rowMapper);
    }

    @Override
    public <T> List<T> query(String sql, RowMapper<T> rowMapper, Object... args) throws DataAccessException {
        return delegate.query(sql, rowMapper, args);
    }

    @Override
    public <T> Stream<T> queryForStream(PreparedStatementCreator psc, RowMapper<T> rowMapper) throws DataAccessException {
        return delegate.queryForStream(psc, rowMapper);
    }

    @Override
    public <T> Stream<T> queryForStream(String sql, PreparedStatementSetter pss, RowMapper<T> rowMapper) throws DataAccessException {
        return delegate.queryForStream(sql, pss, rowMapper);
    }

    @Override
    public <T> Stream<T> queryForStream(String sql, RowMapper<T> rowMapper, Object... args) throws DataAccessException {
        return delegate.queryForStream(sql, rowMapper, args);
    }

    @Override
    public <T> T queryForObject(String sql, Object[] args, int[] argTypes, RowMapper<T> rowMapper) throws DataAccessException {
        return delegate.queryForObject(sql, args, argTypes, rowMapper);
    }

    @Override
    public <T> T queryForObject(String sql, Object[] args, RowMapper<T> rowMapper) throws DataAccessException {
        return delegate.queryForObject(sql, args, rowMapper);
    }

    @Override
    public <T> T queryForObject(String sql, RowMapper<T> rowMapper, Object... args) throws DataAccessException {
        return delegate.queryForObject(sql, rowMapper, args);
    }

    @Override
    public <T> T queryForObject(String sql, Object[] args, int[] argTypes, Class<T> requiredType) throws DataAccessException {
        return delegate.queryForObject(sql, args, argTypes, requiredType);
    }

    @Override
    public <T> T queryForObject(String sql, Object[] args, Class<T> requiredType) throws DataAccessException {
        return delegate.queryForObject(sql, args, requiredType);
    }

    @Override
    public <T> T queryForObject(String sql, Class<T> requiredType, Object... args) throws DataAccessException {
        return delegate.queryForObject(sql, requiredType, args);
    }

    @Override
    public Map<String, Object> queryForMap(String sql, Object[] args, int[] argTypes) throws DataAccessException {
        return delegate.queryForMap(sql, args, argTypes);
    }

    @Override
    public Map<String, Object> queryForMap(String sql, Object... args) throws DataAccessException {
        return delegate.queryForMap(sql, args);
    }

    @Override
    public <T> List<T> queryForList(String sql, Object[] args, int[] argTypes, Class<T> elementType) throws DataAccessException {
        return delegate.queryForList(sql, args, argTypes, elementType);
    }

    @Override
    public <T> List<T> queryForList(String sql, Object[] args, Class<T> elementType) throws DataAccessException {
        return delegate.queryForList(sql, args, elementType);
    }

    @Override
    public <T> List<T> queryForList(String sql, Class<T> elementType, Object... args) throws DataAccessException {
        return delegate.queryForList(sql, elementType, args);
    }

    @Override
    public List<Map<String, Object>> queryForList(String sql, Object[] args, int[] argTypes) throws DataAccessException {
        return delegate.queryForList(sql, args, argTypes);
    }

    @Override
    public List<Map<String, Object>> queryForList(String sql, Object... args) throws DataAccessException {
        return delegate.queryForList(sql, args);
    }

    @Override
    public SqlRowSet queryForRowSet(String sql, Object[] args, int[] argTypes) throws DataAccessException {
        return delegate.queryForRowSet(sql, args, argTypes);
    }

    @Override
    public SqlRowSet queryForRowSet(String sql, Object... args) throws DataAccessException {
        return delegate.queryForRowSet(sql, args);
    }

    @Override
    public int update(PreparedStatementCreator psc) throws DataAccessException {
        return delegate.update(psc);
    }

    @Override
    public int update(PreparedStatementCreator psc, KeyHolder generatedKeyHolder) throws DataAccessException {
        return delegate.update(psc, generatedKeyHolder);
    }

    @Override
    public int update(String sql, PreparedStatementSetter pss) throws DataAccessException {
        return delegate.update(sql, pss);
    }

    @Override
    public int update(String sql, Object[] args, int[] argTypes) throws DataAccessException {
        return delegate.update(sql, args, argTypes);
    }

    @Override
    public int update(String sql, Object... args) throws DataAccessException {
        return delegate.update(sql, args);
    }

    @Override
    public int[] batchUpdate(String sql, BatchPreparedStatementSetter pss) throws DataAccessException {
        return delegate.batchUpdate(sql, pss);
    }

    @Override
    public int[] batchUpdate(String sql, List<Object[]> batchArgs) throws DataAccessException {
        return delegate.batchUpdate(sql, batchArgs);
    }

    @Override
    public int[] batchUpdate(String sql, List<Object[]> batchArgs, int[] argTypes) throws DataAccessException {
        return delegate.batchUpdate(sql, batchArgs, argTypes);
    }

    @Override
    public <T> int[][] batchUpdate(String sql, Collection<T> batchArgs, int batchSize, ParameterizedPreparedStatementSetter<T> pss) throws DataAccessException {
        return delegate.batchUpdate(sql, batchArgs, batchSize, pss);
    }

    @Override
    public <T> T execute(CallableStatementCreator csc, CallableStatementCallback<T> action) throws DataAccessException {
        return delegate.execute(csc, action);
    }

    @Override
    public <T> T execute(String callString, CallableStatementCallback<T> action) throws DataAccessException {
        return delegate.execute(callString, action);
    }

    @Override
    public Map<String, Object> call(CallableStatementCreator csc, List<SqlParameter> declaredParameters) throws DataAccessException {
        return delegate.call(csc, declaredParameters);
    }
}
