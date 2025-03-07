package io.github.opensabe.jdbc.observation.jfr;

import jdk.jfr.Category;
import jdk.jfr.Event;
import jdk.jfr.Label;
import jdk.jfr.StackTrace;

/**
 * sql执行监控
 * @author maheng
 */

@Category({"observation","mybatis"})
@Label("SQL Execute Monitor")
@StackTrace(value = false)
public class SQLExecuteEvent extends Event {

    /**
     * 执行sql的方法（mybatis mapper）
     */
    @Label("SQL Executed Method")
    private final String method;

    @Label("Transaction Id")
    private final String transactionName;

    private String traceId;
    private String spanId;

    private final boolean success;


    public String getMethod() {
        return method;
    }

    public String getTransactionName() {
        return transactionName;
    }

    public String getTraceId() {
        return traceId;
    }

    public String getSpanId() {
        return spanId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public void setSpanId(String spanId) {
        this.spanId = spanId;
    }

    public SQLExecuteEvent(String method, String transactionName, boolean success) {
        this.method = method;
        this.transactionName = transactionName;
        this.success = success;
    }

}