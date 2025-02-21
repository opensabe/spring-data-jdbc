package io.github.mado.jdbc.observation;

import io.micrometer.observation.Observation;

/**
 * 监控connection
 * 不返回连接的最大存活时间，因为只有配置testWhileIdle = true（默认是false）时才有效
 * @author maheng
 */
public class ConnectionContext extends Observation.Context {

    /**
     * 最多等待获取连接的线程数，默认是-1，没有限制
     */
    private int maxWaitThread;

    /**
     * 获取连接最多等待毫秒数
     */
    private long maxWaitTime;

    /**
     * 最大连接数
     */
    private final int maxActive;

    /**
     * 等待锁的线程数量，由于maxWaitThread默认为-1，不限制，也不想每次都去计算等待线程大小，因此缓存该值1秒钟，最终返回的数量不一定准确
     */
    private Integer waitThread;

    /**
     * 已经创建的连接数量
     */
    private int activeCount;

    /**
     * 连接创建时间
     */
    private long connectedTime;

    private final String event;

    private boolean success;

    public boolean isConnect () {
        return "Connect".equals(event);
    }


    public int getMaxWaitThread() {
        return maxWaitThread;
    }

    public long getMaxWaitTime() {
        return maxWaitTime;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public Integer getWaitThread() {
        return waitThread;
    }

    public int getActiveCount() {
        return activeCount;
    }

    public long getConnectedTime() {
        return connectedTime;
    }

    public String getEvent() {
        return event;
    }

    public boolean isSuccess() {
        return success;
    }

    private ConnectionContext(int maxWaitThread, long maxWaitTime, int maxActive) {
        this.maxWaitThread = maxWaitThread;
        this.maxWaitTime = maxWaitTime;
        this.maxActive = maxActive;
        this.event = "Connect";
        this.success = true;
    }

    public ConnectionContext(int maxActive, int activeCount, long connectedTime) {
        this.maxActive = maxActive;
        this.activeCount = activeCount;
        this.connectedTime = connectedTime;
        this.event = "Close";
        this.success = true;
    }

    public static ConnectionContext connect(int maxWaitThread, long maxWaitTime, int maxActive) {
        return new ConnectionContext(maxWaitThread, maxWaitTime, maxActive);
    }
    public static ConnectionContext release(int maxActive, int activeCount, long connectedTime) {
        return new ConnectionContext(maxActive, activeCount, connectedTime);
    }
}
