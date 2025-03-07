package io.github.opensabe.jdbc.core.converter;

import org.springframework.core.convert.converter.Converter;

import java.util.Optional;

/**
 * 保存数据时，将java对象中的boolean类型转换为jdbc的int类型，如果数据库中tinyint长度大于1，不能自动转换
 * <p>
 * <b>这里跟mybatis保持一致，大于0为true，小于0为false</b>
 * </p>
 * @author heng.ma
 */
public class IntegerToBooleanConverter implements Converter<Integer, Boolean> {
    @Override
    public Boolean convert(Integer source) {
        return Optional.ofNullable(source).map(i -> i >0).orElse(null);
    }
}
