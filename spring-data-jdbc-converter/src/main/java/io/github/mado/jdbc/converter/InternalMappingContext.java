package io.github.mado.jdbc.converter;

import org.springframework.data.jdbc.core.mapping.JdbcMappingContext;
import org.springframework.data.mapping.model.Property;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.relational.core.mapping.NamingStrategy;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;

/**
 * 创建Property时，创建自己的子类，复写 isMap 方法
 * @author heng.ma
 */
public class InternalMappingContext extends JdbcMappingContext {

    public InternalMappingContext(NamingStrategy namingStrategy) {
        super(namingStrategy);
    }



    @Override
    protected RelationalPersistentProperty createPersistentProperty(Property property, RelationalPersistentEntity<?> owner, SimpleTypeHolder simpleTypeHolder) {
        FixedIsMapPersistentProperty persistentProperty = new FixedIsMapPersistentProperty(property, owner, simpleTypeHolder,
                this.getNamingStrategy());
        persistentProperty.setForceQuote(isForceQuote());
        return persistentProperty;
    }
}
