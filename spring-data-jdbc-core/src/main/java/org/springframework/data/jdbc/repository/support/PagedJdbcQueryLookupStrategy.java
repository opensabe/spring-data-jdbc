package org.springframework.data.jdbc.repository.support;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.jdbc.repository.QueryMappingConfiguration;
import org.springframework.data.jdbc.repository.query.JdbcQueryMethod;
import org.springframework.data.jdbc.repository.query.PagedSliceJdbcQuery;
import org.springframework.data.mapping.callback.EntityCallbacks;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.repository.query.ValueExpressionDelegate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import java.lang.reflect.Method;

/**
 * @author heng.ma
 */
public class PagedJdbcQueryLookupStrategy extends JdbcQueryLookupStrategy {


    public PagedJdbcQueryLookupStrategy(ApplicationEventPublisher publisher, EntityCallbacks callbacks, RelationalMappingContext context, JdbcConverter converter, Dialect dialect, QueryMappingConfiguration queryMappingConfiguration, NamedParameterJdbcOperations operations, ValueExpressionDelegate delegate) {
        super(publisher, callbacks, context, converter, dialect, queryMappingConfiguration, operations, delegate);
    }

    @Override
    public RepositoryQuery resolveQuery(Method method, RepositoryMetadata metadata, ProjectionFactory factory, NamedQueries namedQueries) {
        JdbcQueryMethod queryMethod = getJdbcQueryMethod(method, metadata, factory, namedQueries);
        return new PagedSliceJdbcQuery(queryMethod, getOperations(), this::createMapper, getConverter(), delegate);
    }
}
