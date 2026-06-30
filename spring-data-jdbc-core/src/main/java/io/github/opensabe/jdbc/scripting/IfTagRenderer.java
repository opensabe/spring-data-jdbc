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
import org.springframework.data.spel.ExpressionDependencies;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.Assert;

/**
 * Renders MyBatis-style {@code <if test="...">} fragments using SpEL for condition evaluation.
 * <p>
 * Only {@code <if>} tags are supported. Fragment bodies may contain nested {@code <if>} tags.
 * SpEL expressions in {@code test} use the same {@link QueryMethodEvaluationContextProvider}
 * as Spring Data {@code @Query} ({@code :#{...}}) bindings.
 */
public final class IfTagRenderer {

    private static final SpelExpressionParser PARSER = new SpelExpressionParser();

    private IfTagRenderer() {
    }

    public static String render(String sql, Parameters<?, ?> parameters, Object[] values,
            QueryMethodEvaluationContextProvider evaluationContextProvider) {

        Assert.notNull(sql, "SQL must not be null");
        Assert.notNull(parameters, "Parameters must not be null");
        Assert.notNull(values, "Values must not be null");
        Assert.notNull(evaluationContextProvider, "QueryMethodEvaluationContextProvider must not be null");

        if (!DynamicSqlDetector.isDynamic(sql)) {
            return sql;
        }

        return renderInternal(sql, parameters, values, evaluationContextProvider);
    }

    private static String renderInternal(String sql, Parameters<?, ?> parameters, Object[] values,
            QueryMethodEvaluationContextProvider evaluationContextProvider) {

        StringBuilder result = new StringBuilder(sql.length());
        int index = 0;

        while (index < sql.length()) {
            if (isQuoted(sql, index)) {
                char quote = sql.charAt(index);
                int end = findQuoteEnd(sql, index + 1, quote);
                result.append(sql, index, end);
                index = end;
                continue;
            }

            int ifStart = findIfTagStart(sql, index);
            if (ifStart < 0) {
                result.append(sql.substring(index));
                break;
            }

            result.append(sql, index, ifStart);

            int testValueStart = skipIfTagPrefix(sql, ifStart);
            char quote = sql.charAt(testValueStart);
            int testValueEnd = findQuoteEnd(sql, testValueStart + 1, quote);
            String testExpression = sql.substring(testValueStart + 1, testValueEnd - 1);

            int bodyStart = findTagBodyStart(sql, testValueEnd);
            int bodyEnd = findMatchingEndIf(sql, bodyStart);

            if (evaluateTest(testExpression, parameters, values, evaluationContextProvider)) {
                result.append(renderInternal(sql.substring(bodyStart, bodyEnd), parameters, values,
                        evaluationContextProvider));
            }

            index = bodyEnd + "</if>".length();
        }

        return result.toString();
    }

    private static boolean evaluateTest(String expression, Parameters<?, ?> parameters, Object[] values,
            QueryMethodEvaluationContextProvider evaluationContextProvider) {

        Expression spelExpression = PARSER.parseExpression(expression);
        EvaluationContext evaluationContext = evaluationContextProvider.getEvaluationContext(parameters, values,
                ExpressionDependencies.discover(spelExpression));
        Object value = spelExpression.getValue(evaluationContext);

        if (value instanceof Boolean bool) {
            return bool;
        }
        if (value instanceof Number number) {
            return number.doubleValue() != 0D;
        }
        return value != null;
    }

    private static int findIfTagStart(String sql, int from) {
        for (int i = from; i < sql.length(); i++) {
            if (isQuoted(sql, i)) {
                i = findQuoteEnd(sql, i + 1, sql.charAt(i)) - 1;
                continue;
            }
            if (matchesIgnoreCase(sql, i, "<if")) {
                int next = i + 3;
                if (next >= sql.length()) {
                    return i;
                }
                char c = sql.charAt(next);
                if (Character.isWhitespace(c) || c == '>') {
                    return i;
                }
            }
        }
        return -1;
    }

    private static int skipIfTagPrefix(String sql, int ifStart) {
        int index = ifStart + 3;
        while (index < sql.length() && Character.isWhitespace(sql.charAt(index))) {
            index++;
        }
        if (!matchesIgnoreCase(sql, index, "test")) {
            throw new IllegalArgumentException("Expected test attribute in <if> tag near index " + ifStart);
        }
        index += 4;
        while (index < sql.length() && Character.isWhitespace(sql.charAt(index))) {
            index++;
        }
        if (index >= sql.length() || sql.charAt(index) != '=') {
            throw new IllegalArgumentException("Expected '=' after test attribute near index " + ifStart);
        }
        index++;
        while (index < sql.length() && Character.isWhitespace(sql.charAt(index))) {
            index++;
        }
        if (index >= sql.length()) {
            throw new IllegalArgumentException("Unterminated test attribute near index " + ifStart);
        }
        char quote = sql.charAt(index);
        if (quote != '"' && quote != '\'') {
            throw new IllegalArgumentException("test attribute value must be quoted near index " + ifStart);
        }
        return index;
    }

    private static int findTagBodyStart(String sql, int from) {
        int index = from;
        while (index < sql.length() && Character.isWhitespace(sql.charAt(index))) {
            index++;
        }
        if (index >= sql.length() || sql.charAt(index) != '>') {
            throw new IllegalArgumentException("Expected '>' to close <if> opening tag near index " + from);
        }
        return index + 1;
    }

    private static int findMatchingEndIf(String sql, int bodyStart) {
        int depth = 1;
        int index = bodyStart;

        while (index < sql.length()) {
            if (isQuoted(sql, index)) {
                index = findQuoteEnd(sql, index + 1, sql.charAt(index));
                continue;
            }

            if (matchesIgnoreCase(sql, index, "</if>")) {
                depth--;
                if (depth == 0) {
                    return index;
                }
                index += "</if>".length();
                continue;
            }

            if (matchesIgnoreCase(sql, index, "<if")) {
                int next = index + 3;
                if (next < sql.length()) {
                    char c = sql.charAt(next);
                    if (Character.isWhitespace(c) || c == '>') {
                        depth++;
                    }
                }
            }

            index++;
        }

        throw new IllegalArgumentException("Unclosed <if> tag starting near index " + bodyStart);
    }

    private static boolean isQuoted(String sql, int index) {
        char c = sql.charAt(index);
        return c == '\'' || c == '"';
    }

    private static int findQuoteEnd(String sql, int from, char quote) {
        for (int i = from; i < sql.length(); i++) {
            char c = sql.charAt(i);
            if (c == quote) {
                if (i > from && sql.charAt(i - 1) == '\\') {
                    continue;
                }
                return i + 1;
            }
        }
        throw new IllegalArgumentException("Unterminated string literal in SQL near index " + from);
    }

    private static boolean matchesIgnoreCase(String sql, int index, String token) {
        if (index + token.length() > sql.length()) {
            return false;
        }
        for (int i = 0; i < token.length(); i++) {
            if (Character.toLowerCase(sql.charAt(index + i)) != Character.toLowerCase(token.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
