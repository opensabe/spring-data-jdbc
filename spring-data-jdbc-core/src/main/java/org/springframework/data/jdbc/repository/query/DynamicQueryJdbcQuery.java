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
package org.springframework.data.jdbc.repository.query;

import io.github.opensabe.jdbc.scripting.DynamicSqlRenderer;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

public class DynamicQueryJdbcQuery extends PagedSliceJdbcQuery {

    private final DynamicSqlRenderer dynamicSqlRenderer;

    public DynamicQueryJdbcQuery(JdbcQueryMethod queryMethod, NamedParameterJdbcOperations operations, RowMapperFactory rowMapperFactory, JdbcConverter converter, QueryMethodEvaluationContextProvider evaluationContextProvider) {
        super(queryMethod, operations, rowMapperFactory, converter, evaluationContextProvider);
        String query = queryMethod.getDeclaredQuery();
        if (!DynamicSqlRenderer.isDynamic(query)) {
            throw new UnsupportedOperationException("Dynamic queries can only be applied to dynamic queries");
        }
        this.dynamicSqlRenderer = new DynamicSqlRenderer(evaluationContextProvider);
    }

    @Override
    protected String processSpelExpressions(Object[] objects, MapSqlParameterSource parameterMap, String query) {
        query = dynamicSqlRenderer.render(query, getQueryMethod().getParameters(), objects);
        return super.processSpelExpressions(objects, parameterMap, query);
    }
}
