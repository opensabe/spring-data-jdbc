package io.github.mado.jdbc.core.lambda;

import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.CriteriaDefinition;
import org.springframework.data.relational.core.sql.SqlIdentifier;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class WeekendCriteria<A,B> implements CriteriaDefinition {

    private Criteria criteria;

    WeekendCriteria () {
        this.criteria = Criteria.empty();
    }
    private WeekendCriteria (Criteria criteria) {
        this.criteria = criteria;
    }

    void or(WeekendCriteria<A,B> weekendCriteria) {
        criteria = criteria.or(weekendCriteria);
    }
    void and(WeekendCriteria<A,B> weekendCriteria) {
        criteria = criteria.and(weekendCriteria);
    }

    public WeekendCriteria<A, B> andIsNull(Fn<A, B> fn) {
        criteria = criteria.and(Reflections.fnToFieldName(fn)).isNull();
        return this;
    }

    public WeekendCriteria<A, B> andIsNotNull(Fn<A, B> fn) {
        criteria = criteria.and(Reflections.fnToFieldName(fn)).isNotNull();
        return this;
    }

    public WeekendCriteria<A, B> andEqualTo(Fn<A, B> fn, Object value) {
        criteria = criteria.and(Reflections.fnToFieldName(fn)).is(value);
        return this;
    }

    public WeekendCriteria<A, B> andNotEqualTo(Fn<A, B> fn, Object value) {
        criteria = criteria.and(Reflections.fnToFieldName(fn)).not(value);
        return this;
    }

    public WeekendCriteria<A, B> andGreaterThan(Fn<A, B> fn, Object value) {
        criteria = criteria.and(Reflections.fnToFieldName(fn)).greaterThan(value);
        return this;
    }

    public WeekendCriteria<A, B> andGreaterThanOrEqualTo(Fn<A, B> fn, Object value) {
        criteria = criteria.and(Reflections.fnToFieldName(fn)).greaterThanOrEquals(value);
        return this;
    }

    public WeekendCriteria<A, B> andLessThan(Fn<A, B> fn, Object value) {
        criteria = criteria.and(Reflections.fnToFieldName(fn)).lessThan(value);
        return this;
    }

    public WeekendCriteria<A, B> andLessThanOrEqualTo(Fn<A, B> fn, Object value) {
        criteria = criteria.and(Reflections.fnToFieldName(fn)).lessThanOrEquals(value);
        return this;
    }

    public WeekendCriteria<A, B> andIn(Fn<A, B> fn, Collection<?> values) {
        criteria = criteria.and(Reflections.fnToFieldName(fn)).in(values);
        return this;
    }
    public WeekendCriteria<A, B> andIn(Fn<A, B> fn, Object ... values) {
        criteria = criteria.and(Reflections.fnToFieldName(fn)).in(values);
        return this;
    }

    public WeekendCriteria<A, B> andNotIn(Fn<A, B> fn, Collection<?> values) {
        criteria = criteria.and(Reflections.fnToFieldName(fn)).notIn(values);
        return this;
    }
    public WeekendCriteria<A, B> andNotIn(Fn<A, B> fn, Object ... values) {
        criteria = criteria.and(Reflections.fnToFieldName(fn)).notIn(values);
        return this;
    }

    public WeekendCriteria<A, B> andBetween(Fn<A, B> fn, Object begin, Object end) {
        criteria = criteria.and(Reflections.fnToFieldName(fn)).between(begin,end);
        return this;
    }

    public WeekendCriteria<A, B> andNotBetween(Fn<A, B> fn, Object begin, Object end) {
        criteria = criteria.and(Reflections.fnToFieldName(fn)).notBetween(begin,end);
        return this;
    }

    public WeekendCriteria<A, B> andLike(Fn<A, B> fn, String value) {
        criteria = criteria.and(Reflections.fnToFieldName(fn)).like(value);
        return this;
    }

    public WeekendCriteria<A, B> andNotLike(Fn<A, B> fn, String value) {
        criteria = criteria.and(Reflections.fnToFieldName(fn)).notLike(value);
        return this;
    }

    public WeekendCriteria<A, B> orIsNull(Fn<A, B> fn) {
        criteria = criteria.or(Reflections.fnToFieldName(fn)).isNull();
        return this;
    }

    public WeekendCriteria<A, B> orIsNotNull(Fn<A, B> fn) {
        criteria = criteria.or(Reflections.fnToFieldName(fn)).isNotNull();
        return this;
    }

    public WeekendCriteria<A, B> orEqualTo(Fn<A, B> fn, Object value) {
        criteria = criteria.or(Reflections.fnToFieldName(fn)).is(value);
        return this;
    }

    public WeekendCriteria<A, B> orNotEqualTo(Fn<A, B> fn, Object value) {
        criteria = criteria.or(Reflections.fnToFieldName(fn)).not(value);
        return this;
    }

    public WeekendCriteria<A, B> orGreaterThan(Fn<A, B> fn, Object value) {
        criteria = criteria.or(Reflections.fnToFieldName(fn)).greaterThan(value);
        return this;
    }

    public WeekendCriteria<A, B> orGreaterThanOrEqualTo(Fn<A, B> fn, Object value) {
        criteria = criteria.or(Reflections.fnToFieldName(fn)).greaterThanOrEquals(value);
        return this;
    }

    public WeekendCriteria<A, B> orLessThan(Fn<A, B> fn, Object value) {
        criteria = criteria.or(Reflections.fnToFieldName(fn)).lessThan(value);
        return this;
    }

    public WeekendCriteria<A, B> orLessThanOrEqualTo(Fn<A, B> fn, Object value) {
        criteria = criteria.or(Reflections.fnToFieldName(fn)).lessThanOrEquals(value);
        return this;
    }

    public WeekendCriteria<A, B> orIn(Fn<A, B> fn, Collection<?> values) {
        criteria = criteria.or(Reflections.fnToFieldName(fn)).in(values);
        return this;
    }
    public WeekendCriteria<A, B> orIn(Fn<A, B> fn, Objects ... values) {
        criteria = criteria.or(Reflections.fnToFieldName(fn)).in((Object[]) values);
        return this;
    }

    public WeekendCriteria<A, B> orNotIn(Fn<A, B> fn, Collection<?> values) {
        criteria = criteria.or(Reflections.fnToFieldName(fn)).notIn(values);
        return this;
    }
    public WeekendCriteria<A, B> orNotIn(Fn<A, B> fn, Object ... values) {
        criteria = criteria.or(Reflections.fnToFieldName(fn)).notIn(values);
        return this;
    }

    public WeekendCriteria<A, B> orBetween(Fn<A, B> fn, Object begin, Object end) {
        criteria = criteria.or(Reflections.fnToFieldName(fn)).between(begin,end);
        return this;
    }

    public WeekendCriteria<A, B> orNotBetween(Fn<A, B> fn, Object begin, Object end) {
        criteria = criteria.or(Reflections.fnToFieldName(fn)).notBetween(begin,end);
        return this;
    }

    public WeekendCriteria<A, B> orLike(Fn<A, B> fn, String value) {
        criteria = criteria.or(Reflections.fnToFieldName(fn)).like(value);
        return this;
    }

    public WeekendCriteria<A, B> orNotLike(Fn<A, B> fn, String value) {
        criteria = criteria.or(Reflections.fnToFieldName(fn)).notLike(value);
        return this;
    }

    public Criteria getCriteria() {
        return criteria;
    }

















    @Override
    public boolean isGroup() {
        return criteria.isGroup();
    }

    @Override
    public List<CriteriaDefinition> getGroup() {
        return criteria.getGroup();
    }

    @Override
    public SqlIdentifier getColumn() {
        return criteria.getColumn();
    }

    @Override
    public Comparator getComparator() {
        return criteria.getComparator();
    }

    @Override
    public Object getValue() {
        return criteria.getValue();
    }

    @Override
    public boolean isIgnoreCase() {
        return criteria.isIgnoreCase();
    }

    @Override
    public CriteriaDefinition getPrevious() {
        return criteria.getPrevious();
    }

    @Override
    public boolean hasPrevious() {
        return criteria.hasPrevious();
    }

    @Override
    public boolean isEmpty() {
        return criteria.isEmpty();
    }

    @Override
    public Combinator getCombinator() {
        return criteria.getCombinator();
    }
}
