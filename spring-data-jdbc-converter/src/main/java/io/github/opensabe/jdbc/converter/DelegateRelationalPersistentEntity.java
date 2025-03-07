package io.github.opensabe.jdbc.converter;

import org.springframework.core.env.Environment;
import org.springframework.data.mapping.*;
import org.springframework.data.mapping.model.PersistentPropertyAccessorFactory;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.data.spel.EvaluationContextProvider;
import org.springframework.data.util.TypeInformation;

import java.lang.annotation.Annotation;
import java.util.Iterator;

/**
 * RelationalPersistentEntity代理类
 * @param <T>   表映射类型
 * @author heng.ma
 */
public class DelegateRelationalPersistentEntity<T> implements RelationalPersistentEntity<T>  {

    private final RelationalPersistentEntity<T> delegate;

    public DelegateRelationalPersistentEntity(RelationalPersistentEntity<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public SqlIdentifier getTableName() {
        return delegate.getTableName();
    }

    @Override
    public SqlIdentifier getIdColumn() {
        return delegate.getIdColumn();
    }

    @Override
    public void addPersistentProperty(RelationalPersistentProperty property) {
        delegate.addPersistentProperty(property);
    }

    @Override
    public void addAssociation(Association<RelationalPersistentProperty> association) {
        delegate.addAssociation(association);
    }

    @Override
    public void verify() throws MappingException {
        delegate.verify();
    }

    @Override
    public void setPersistentPropertyAccessorFactory(PersistentPropertyAccessorFactory factory) {
        delegate.setPersistentPropertyAccessorFactory(factory);
    }

    @Override
    public void setEvaluationContextProvider(EvaluationContextProvider provider) {
        delegate.setEvaluationContextProvider(provider);
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public PreferredConstructor<T, RelationalPersistentProperty> getPersistenceConstructor() {
        return delegate.getPersistenceConstructor();
    }

    @Override
    public InstanceCreatorMetadata<RelationalPersistentProperty> getInstanceCreatorMetadata() {
        return delegate.getInstanceCreatorMetadata();
    }

    @Override
    public boolean isCreatorArgument(PersistentProperty<?> property) {
        return delegate.isCreatorArgument(property);
    }

    @Override
    public boolean isIdProperty(PersistentProperty<?> property) {
        return delegate.isIdProperty(property);
    }

    @Override
    public boolean isVersionProperty(PersistentProperty<?> property) {
        return delegate.isVersionProperty(property);
    }

    @Override
    public RelationalPersistentProperty getIdProperty() {
        return delegate.getIdProperty();
    }

    @Override
    public RelationalPersistentProperty getVersionProperty() {
        return delegate.getVersionProperty();
    }

    @Override
    public RelationalPersistentProperty getPersistentProperty(String name) {
        return delegate.getPersistentProperty(name);
    }

    @Override
    public Iterable<RelationalPersistentProperty> getPersistentProperties(Class<? extends Annotation> annotationType) {
        return delegate.getPersistentProperties(annotationType);
    }

    @Override
    public boolean hasIdProperty() {
        return delegate.hasIdProperty();
    }

    @Override
    public boolean hasVersionProperty() {
        return delegate.hasVersionProperty();
    }

    @Override
    public Class<T> getType() {
        return delegate.getType();
    }

    @Override
    public Alias getTypeAlias() {
        return delegate.getTypeAlias();
    }

    @Override
    public TypeInformation<T> getTypeInformation() {
        return delegate.getTypeInformation();
    }

    @Override
    public void doWithProperties(PropertyHandler<RelationalPersistentProperty> handler) {
        delegate.doWithProperties(handler);
    }

    @Override
    public void doWithProperties(SimplePropertyHandler handler) {
        delegate.doWithProperties(handler);
    }

    @Override
    public void doWithAssociations(AssociationHandler<RelationalPersistentProperty> handler) {
        delegate.doWithAssociations(handler);
    }

    @Override
    public void doWithAssociations(SimpleAssociationHandler handler) {
        delegate.doWithAssociations(handler);
    }

    @Override
    public <A extends Annotation> A findAnnotation(Class<A> annotationType) {
        return delegate.findAnnotation(annotationType);
    }

    @Override
    public <A extends Annotation> boolean isAnnotationPresent(Class<A> annotationType) {
        return delegate.isAnnotationPresent(annotationType);
    }

    @Override
    public <B> PersistentPropertyAccessor<B> getPropertyAccessor(B bean) {
        return delegate.getPropertyAccessor(bean);
    }

    @Override
    public <B> PersistentPropertyPathAccessor<B> getPropertyPathAccessor(B bean) {
        return delegate.getPropertyPathAccessor(bean);
    }

    @Override
    public IdentifierAccessor getIdentifierAccessor(Object bean) {
        return delegate.getIdentifierAccessor(bean);
    }

    @Override
    public boolean isNew(Object bean) {
        return delegate.isNew(bean);
    }

    @Override
    public boolean isImmutable() {
        return delegate.isImmutable();
    }

    @Override
    public boolean requiresPropertyPopulation() {
        return delegate.requiresPropertyPopulation();
    }

    @Override
    public Iterator<RelationalPersistentProperty> iterator() {
        return delegate.iterator();
    }

    @Override
    public void setEnvironment(Environment environment) {
        delegate.setEnvironment(environment);
    }
}
