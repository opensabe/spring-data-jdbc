package io.github.mado.jdbc.core.lambda;

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
//    private Integer limit;
//    private Sort sort;
//    private Long offset;

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

//    public Weekend<T> with (Pageable pageable) {
//        this.offset = pageable.getOffset();
//        this.sort = pageable.getSort();
//        return this;
//    }

    @SafeVarargs
    public final Weekend<T> columns(Fn<T, Object>... fns) {
        Arrays.stream(fns).forEach(fn -> columns.add(Reflections.fnToFieldName(fn)));
        return this;
    }

//    public Weekend<T> limit (Integer limit) {
//        this.limit = limit;
//        return this;
//    }
//
//    public Weekend<T> sort (Sort sort) {
//        this.sort = sort;
//        return this;
//    }
//
//    public Weekend<T> offset (long offset) {
//        this.offset = offset;
//        return this;
//    }

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
//        if (Objects.nonNull(limit)) {
//            query = query.limit(limit);
//        }
//        if (Objects.nonNull(sort)) {
//            query = query.sort(sort);
//        }
//        if (Objects.nonNull(offset)) {
//            query = query.offset(offset);
//        }
        return query;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }
}
