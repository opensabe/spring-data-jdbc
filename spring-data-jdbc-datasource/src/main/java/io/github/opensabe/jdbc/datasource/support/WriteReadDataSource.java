package io.github.opensabe.jdbc.datasource.support;

import io.github.opensabe.jdbc.datasource.aop.ReadOnlyRepositoryAdvice;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @author heng.ma
 */
public class WriteReadDataSource extends AbstractRoutingDataSource {

    public WriteReadDataSource(DataSource writeable, DataSource readOnly) {
        setDefaultTargetDataSource(writeable);
        if (readOnly != null) {
            setTargetDataSources(Map.of(false, writeable, true, readOnly));
        }else {
            setTargetDataSources(Map.of(false, writeable));
        }
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return ReadOnlyRepositoryAdvice.isReadOnly();
    }
}
