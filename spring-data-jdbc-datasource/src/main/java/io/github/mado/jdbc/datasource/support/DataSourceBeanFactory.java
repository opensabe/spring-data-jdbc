package io.github.mado.jdbc.datasource.support;

import io.github.mado.jdbc.core.BeanFactoryDelegate;
import org.springframework.beans.factory.BeanFactory;

/**
 * @author heng.ma
 */
public class DataSourceBeanFactory extends BeanFactoryDelegate {

    public DataSourceBeanFactory(BeanFactory delegate) {
        super(delegate);
    }
}
