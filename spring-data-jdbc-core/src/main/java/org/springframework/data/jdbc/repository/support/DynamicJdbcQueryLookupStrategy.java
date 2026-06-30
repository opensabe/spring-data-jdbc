/*
 * Copyright 2020-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.jdbc.repository.support;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.jdbc.repository.QueryMappingConfiguration;
import org.springframework.data.jdbc.repository.query.DynamicQueryJdbcQuery;
import org.springframework.data.jdbc.repository.query.JdbcQueryMethod;
import org.springframework.data.mapping.callback.EntityCallbacks;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.lang.Nullable;

import java.lang.reflect.Method;

public class DynamicJdbcQueryLookupStrategy extends JdbcQueryLookupStrategy {


    public DynamicJdbcQueryLookupStrategy(ApplicationEventPublisher publisher, @Nullable EntityCallbacks callbacks, RelationalMappingContext context, JdbcConverter converter, Dialect dialect, QueryMappingConfiguration queryMappingConfiguration, NamedParameterJdbcOperations operations, @Nullable BeanFactory beanfactory, QueryMethodEvaluationContextProvider evaluationContextProvider) {
        super(publisher, callbacks, context, converter, dialect, queryMappingConfiguration, operations, beanfactory, evaluationContextProvider);
    }

    @Override
    public RepositoryQuery resolveQuery(Method method, RepositoryMetadata metadata, ProjectionFactory factory, NamedQueries namedQueries) {
        JdbcQueryMethod jdbcQueryMethod = getJdbcQueryMethod(method, metadata, factory, namedQueries);
        return new DynamicQueryJdbcQuery(jdbcQueryMethod, getOperations(), this::createMapper, getConverter(), evaluationContextProvider);
    }
}
