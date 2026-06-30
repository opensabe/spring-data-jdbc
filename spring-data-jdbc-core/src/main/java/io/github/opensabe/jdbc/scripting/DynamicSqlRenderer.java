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
package io.github.opensabe.jdbc.scripting;


import org.springframework.data.repository.query.Parameters;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.util.Assert;

/**
 * Entry point for rendering dynamic SQL fragments before parameter binding.
 */
public final class DynamicSqlRenderer {

    private final QueryMethodEvaluationContextProvider evaluationContextProvider;

    public DynamicSqlRenderer(QueryMethodEvaluationContextProvider evaluationContextProvider) {
        Assert.notNull(evaluationContextProvider, "QueryMethodEvaluationContextProvider must not be null");
        this.evaluationContextProvider = evaluationContextProvider;
    }

    public String render(String sql, Parameters<?, ?> parameters, Object[] values) {
        return IfTagRenderer.render(sql, parameters, values, evaluationContextProvider);
    }

    public static boolean isDynamic(String sql) {
        return DynamicSqlDetector.isDynamic(sql);
    }


//    public static void main(String[] args) {
//        String sql = """
//            SELECT * FROM t_user WHERE 1=1
//            <if test="#name != null and #name != ''">
//              AND name = :name
//            </if>
//           """;
//        DynamicSqlRenderer renderer = new DynamicSqlRenderer(evaluationContextProvider);
//        String rendered = renderer.render(sql, queryMethod.getParameters(), methodArgs);
//
//    }
}
