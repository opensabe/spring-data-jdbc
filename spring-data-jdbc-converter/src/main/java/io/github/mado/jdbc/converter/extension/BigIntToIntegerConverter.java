package io.github.mado.jdbc.converter.extension;

import org.springframework.core.convert.converter.Converter;

import java.math.BigInteger;

/**
 * @author heng.ma
 */
public class BigIntToIntegerConverter implements Converter<BigInteger, Integer> {

    @Override
    public Integer convert(BigInteger source) {
        return source.intValue();
    }
}
