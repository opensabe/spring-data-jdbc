package io.github.mado.jdbc.core;

import org.springframework.beans.factory.BeanFactory;

public interface BeanFactoryCustomizers {

    BeanFactory transform (BeanFactory beanFactory);

}
