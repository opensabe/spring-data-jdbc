package io.github.mado.jdbc.datasource.support;

import io.github.mado.jdbc.datasource.aop.ReadOnlyRepositoryAdvice;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @author heng.ma
 */
public class WriteReadDataSource extends AbstractRoutingDataSource {

    public WriteReadDataSource(DataSource writeable, DataSource readOnly) {
        setTargetDataSources(Map.of(false, writeable, true, readOnly));
        setDefaultTargetDataSource(writeable);
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return ReadOnlyRepositoryAdvice.isReadOnly();
    }
}
