package io.github.opensabe.jdbc.core.lambda;

import org.springframework.data.relational.core.query.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class Weekend<T> {
    private final Class<T> entityClass;
    private WeekendCriteria<T,Object> weekendCriteria;

    private final List<String> columns = new ArrayList<>();

    private Weekend(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public static <T> Weekend<T> of(Class<T> entityClass) {
        return new Weekend<>(entityClass);
    }

    public WeekendCriteria<T, Object> weekendCriteria() {
        WeekendCriteria<T,Object> result = new WeekendCriteria<>();
        if (Objects.isNull(this.weekendCriteria)) {
            this.weekendCriteria = result;
        }
        return result;
    }


    @SafeVarargs
    public final Weekend<T> columns(Fn<T, Object>... fns) {
        Arrays.stream(fns).forEach(fn -> columns.add(Reflections.fnToFieldName(fn)));
        return this;
    }


    public void or(WeekendCriteria<T, Object> criteria) {
        this.weekendCriteria.or(criteria);
    }
    public void and(WeekendCriteria<T, Object> criteria) {
        this.weekendCriteria.and(criteria);
    }


    public Query toQuery () {
        Query query = Query.query(this.weekendCriteria);
        if (!columns.isEmpty()) {
            query = query.columns(columns);
        }
        return query;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }
}
