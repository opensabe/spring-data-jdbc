package io.github.mado.jdbc.observation.advice;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.transaction.interceptor.BeanFactoryTransactionAttributeSourceAdvisor;

/**
 * @author heng.ma
 */
public class TransactionObservationAdvisor extends AbstractPointcutAdvisor {

    private final TransactionObservationAdvice advice;
    private final BeanFactoryTransactionAttributeSourceAdvisor attributeSourceAdvisor;

    public TransactionObservationAdvisor(TransactionObservationAdvice advice, BeanFactoryTransactionAttributeSourceAdvisor attributeSourceAdvisor) {
        this.advice = advice;
        this.attributeSourceAdvisor = attributeSourceAdvisor;
    }

    @Override
    public Pointcut getPointcut() {
        return attributeSourceAdvisor.getPointcut();
    }

    @Override
    public Advice getAdvice() {
        return advice;
    }

    @Override
    public int getOrder() {
        return attributeSourceAdvisor.getOrder() -1;
    }
}
