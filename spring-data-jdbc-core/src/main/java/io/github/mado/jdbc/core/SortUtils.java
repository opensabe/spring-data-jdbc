package io.github.mado.jdbc.core;

import io.github.mado.jdbc.core.lambda.Fn;
import io.github.mado.jdbc.core.lambda.Reflections;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author heng.ma
 */
public class SortUtils {

    public static <T> Sort formMap (Map<Fn<T,Object>, Sort.Direction> sort) {
        if (sort == null) {
            return Sort.unsorted();
        }
        List<Sort.Order> list = sort.entrySet().stream()
                .map(e -> Sort.Order.by(Reflections.fnToFieldName(e.getKey())).with(e.getValue()))
                .toList();
        return Sort.by(list);
    }
    @SafeVarargs
    public static <T> Sort formArray (Sort.Direction order ,Fn<T, Object> ... properties) {
        return Sort.by(order, Arrays.stream(properties).map(Reflections::fnToFieldName).toArray(String[]::new));
    }
}
