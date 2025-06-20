package io.github.opensabe.jdbc.core.executor;

import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.ParameterDisposer;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.SqlProvider;
import org.springframework.lang.NonNull;

import java.sql.*;

@SuppressWarnings("unused")
public class ArgumentPreparedStatementCreator extends ArgumentPreparedStatementSetter implements PreparedStatementCreator, ParameterDisposer, SqlProvider {

    private final String sql;
    private String[] keyColumnNames;
    private boolean autoincr = false;
    private int resultSetType = ResultSet.TYPE_FORWARD_ONLY;

    private boolean updatableResults = false;

    public ArgumentPreparedStatementCreator(String sql, Object[] args, String[] keyColumnNames) {
        super(args);
        this.sql = sql;
        this.keyColumnNames = keyColumnNames;
        this.autoincr = true;
    }

    public ArgumentPreparedStatementCreator(String sql, Object[] args) {
        super(args);
        this.sql = sql;
    }

    public void setResultSetType(int resultSetType) {
        this.resultSetType = resultSetType;
    }

    public void setUpdatableResults(boolean updatableResults) {
        this.updatableResults = updatableResults;
    }

    @Override
    @NonNull
    public PreparedStatement createPreparedStatement(@NonNull Connection con) throws SQLException {
        PreparedStatement ps;
        if (keyColumnNames != null || autoincr) {
            if (keyColumnNames !=null && keyColumnNames.length > 1) {
                ps = con.prepareStatement(this.sql, keyColumnNames);
            } else {
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
