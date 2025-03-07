package io.github.opensabe.jdbc.observation.advice;

import io.github.opensabe.jdbc.observation.SQLExecuteContext;
import io.github.opensabe.jdbc.observation.SQLExecuteDocumentation;
import io.github.opensabe.jdbc.observation.SQLExecuteObservationConvention;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Method;

/**
 * @author heng.ma
 */
public class TransactionObservationAdvice implements MethodInterceptor {

    private final ObjectProvider<ObservationRegistry> registry;

    public TransactionObservationAdvice(ObjectProvider<ObservationRegistry> registry) {
        this.registry = registry;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        SQLExecuteContext context = new SQLExecuteContext(method.toGenericString(), TransactionSynchronizationManager.getCurrentTransactionName());
        Observation observation = SQLExecuteDocumentation.SQL_EXECUTE_TRANSACTION
                .observation(null, SQLExecuteObservationConvention.DEFAULT, () -> context, registry.getIfAvailable());
        observation.start();
        try {
            return invocation.proceed();
        }catch (Throwable e) {
            observation.error(e);
            context.setSuccess(false);
            throw  e;
        }finally {
            observation.stop();
        }
    }
}
