package io.github.mado.jdbc.core.executor;

import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.ParameterDisposer;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.SqlProvider;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.*;

public class ArgumentPreparedStatementCreator extends ArgumentPreparedStatementSetter implements PreparedStatementCreator, ParameterDisposer, SqlProvider {

    private final String sql;
    private final KeyHolder keyHolder;
    private final String[] keyColumnNames;

    private int resultSetType = ResultSet.TYPE_FORWARD_ONLY;

    private boolean updatableResults = false;

    public ArgumentPreparedStatementCreator(String sql, Object[] args, KeyHolder keyHolder, String[] keyColumnNames) {
        super(args);
        this.sql = sql;
        this.keyHolder = keyHolder;
        this.keyColumnNames = keyColumnNames;
    }

    public ArgumentPreparedStatementCreator(String sql, Object[] args, KeyHolder keyHolder) {
        this(sql, args, keyHolder, null);
    }

    public ArgumentPreparedStatementCreator(String sql, Object[] args) {
        this (sql, args, null);
    }

    public void setResultSetType(int resultSetType) {
        this.resultSetType = resultSetType;
    }

    public void setUpdatableResults(boolean updatableResults) {
        this.updatableResults = updatableResults;
    }

    @Override
    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
        PreparedStatement ps;
        if (keyColumnNames != null || keyHolder != null) {
            if (keyColumnNames != null) {
                ps = con.prepareStatement(this.sql, keyColumnNames);
            }
            else {
                ps = con.prepareStatement(this.sql, Statement.RETURN_GENERATED_KEYS);
            }
        }else if (resultSetType == ResultSet.TYPE_FORWARD_ONLY && !updatableResults) {
            ps = con.prepareStatement(this.sql);
        } else {
            //noinspection MagicConstant
            ps = con.prepareStatement(this.sql, resultSetType, updatableResults ? ResultSet.CONCUR_UPDATABLE : ResultSet.CONCUR_READ_ONLY);
        }

        setValues(ps);
        return ps;
    }

    @Override
    public String getSql() {
        return sql;
    }
}
