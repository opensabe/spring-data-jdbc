package io.github.opensabe.jdbc.core;

import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;

import java.sql.Types;

/**
 * 使用对象参数时，支持分页
 * @author maheng
 */
public class PageableBeanPropertySqlParameterSource extends BeanPropertySqlParameterSource {

    public static final String OFFSET_PARAMETER_NAME = "offset";
    public static final String LIMIT_PARAMETER_NAME = "limit";

    private final Pageable pageable;

    /**
     * Create a new BeanPropertySqlParameterSource for the given bean.
     *
     * @param object the bean instance to wrap
     */
    public PageableBeanPropertySqlParameterSource(Object object, Pageable pageable) {
        super(object);
        this.pageable = pageable;
    }

    @Override
    public boolean hasValue(String paramName) {
        if (OFFSET_PARAMETER_NAME.equals(paramName) || LIMIT_PARAMETER_NAME.equals(paramName)) {
            return pageable.isPaged();
        }
        return super.hasValue(paramName);
    }

    @Override
    public Object getValue(String paramName) throws IllegalArgumentException {
        if (OFFSET_PARAMETER_NAME.equals(paramName)) {
            return pageable.getOffset();
        }else if (LIMIT_PARAMETER_NAME.equals(paramName)) {
            return pageable.getPageSize();
        }
        return super.getValue(paramName);
    }

    @Override
    public int getSqlType(String paramName) {
        if (OFFSET_PARAMETER_NAME.equals(paramName) || LIMIT_PARAMETER_NAME.equals(paramName)) {
            return Types.NUMERIC;
        }
        return super.getSqlType(paramName);
    }

    @Override
    public String[] getParameterNames() {
        return enhancePropertyNames(super.getParameterNames());
    }

    @Override
    public String[] getReadablePropertyNames() {
        return enhancePropertyNames(super.getReadablePropertyNames());
    }

    private String[] enhancePropertyNames(String[] propertyNames) {
        if (propertyNames == null || propertyNames.length == 0) {
            return new String[] {OFFSET_PARAMETER_NAME, LIMIT_PARAMETER_NAME};
        }
        String[] array = new String[propertyNames.length + 2];
        System.arraycopy(propertyNames, 0, array, 2, propertyNames.length);
        array[0]  = OFFSET_PARAMETER_NAME;
        array[1]  = LIMIT_PARAMETER_NAME;
        return array;
    }
}
