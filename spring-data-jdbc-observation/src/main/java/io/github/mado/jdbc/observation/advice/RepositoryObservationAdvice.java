package io.github.mado.jdbc.observation.advice;

import io.github.mado.jdbc.observation.SQLExecuteContext;
import io.github.mado.jdbc.observation.SQLExecuteDocumentation;
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
public class RepositoryObservationAdvice implements MethodInterceptor {

    private final ObjectProvider<ObservationRegistry> registry;

    public RepositoryObservationAdvice(ObjectProvider<ObservationRegistry> registry) {
        this.registry = registry;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        SQLExecuteContext context = new SQLExecuteContext(method.getDeclaringClass().getName()+"#"+method.getName(), TransactionSynchronizationManager.getCurrentTransactionName());
        Observation observation = SQLExecuteDocumentation.SQL_EXECUTE_MAPPER.observation(registry.getIfAvailable());
        observation.start();
        try {
            return invocation.proceed();
        }catch (Throwable e) {
            context.setSuccess(false);
            observation.error(e);
            throw e;
        }finally {
            observation.stop();
        }
    }
}
