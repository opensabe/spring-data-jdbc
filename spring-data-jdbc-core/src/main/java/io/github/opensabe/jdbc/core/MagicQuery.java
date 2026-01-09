package io.github.opensabe.jdbc.core;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MagicQuery {

    <E> List<E> selectList (String sql, Map<String, Object> params, Class<E> elementType);

    <E> Page<E> selectPage (String sql, Map<String, Object> params, Pageable pageable, Class<E> elementType);

    <E> Optional<E> selectOne (String sql, Map<String, Object> params, Class<E> resultType);

    <E, P> List<E> selectList (String sql, P params, Class<E> elementType);

    <E, P> Page<E> selectPage (String sql, P params, Pageable pageable, Class<E> elementType);

    <E, P> Optional<E> selectOne (String sql, P params, Class<E> resultType);
}
