package io.github.mado.jdbc.core;

import org.springframework.beans.factory.BeanFactory;

public interface BeanFactoryCustomizer {

    BeanFactory transform (BeanFactory beanFactory);

}
