package io.github.mado.jdbc.observation;

import io.micrometer.observation.Observation;

import java.util.Optional;

/**
 * 统计事务或者SQL执行时间的content
 * @author maheng
 */

public class SQLExecuteContext extends Observation.Context {
    private final String method;

    private boolean success;

    private final String transactionName;

    public String getMethod() {
        return method;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getTransactionName() {
        return transactionName;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public SQLExecuteContext(String method, String transactionName) {
        this.method = method;
        this.transactionName = Optional.ofNullable(transactionName).orElse("");
        this.success = true;
    }
}
