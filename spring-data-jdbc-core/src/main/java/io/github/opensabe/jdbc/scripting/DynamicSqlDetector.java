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

import java.util.regex.Pattern;

/**
 * Detects MyBatis-style {@code <if>} dynamic SQL fragments in a query string.
 */
public final class DynamicSqlDetector {

    private static final Pattern IF_TAG = Pattern.compile("<if\\s+test\\s*=", Pattern.CASE_INSENSITIVE);

    private DynamicSqlDetector() {
    }

    public static boolean isDynamic(String sql) {
        return sql != null && IF_TAG.matcher(sql).find();
    }
}
