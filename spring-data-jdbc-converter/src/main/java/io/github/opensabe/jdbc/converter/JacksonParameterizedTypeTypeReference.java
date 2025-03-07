package io.github.opensabe.jdbc.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.data.util.TypeInformation;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * jackson反序列化时，支持Map,List等带泛型的类型
 * @see ParameterizedType
 * @param <T>   要反序列化的类型
 */
public class JacksonParameterizedTypeTypeReference<T> extends TypeReference<T> {

    private final ParameterizedType type;


    public static <T> JacksonParameterizedTypeTypeReference<T> fromTypeInformation (TypeInformation<T> typeInformation) {
        return new JacksonParameterizedTypeTypeReference<>(typeInformation);
    }

    JacksonParameterizedTypeTypeReference(TypeInformation<T> information) {
        List<TypeInformation<?>> arguments = information.getTypeArguments();
        this.type = new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return arguments.stream().map(TypeInformation::getType).toArray(Type[]::new);
            }

            @Override
            public Type getRawType() {
                return information.getType();
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };
    }

    @Override
    public Type getType() {
        return type;
    }
}
