package io.github.opensabe.jdbc.logging;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;

/**
 * 自定义日志内容，这里只打印SQL，并且限制SQL长度为2000
 * @author heng.ma
 */
public class LimitableLineFormat implements MessageFormattingStrategy {
    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
        return sql.length() > 2000? sql.substring(0, 2000) +"..." : sql;
    }
}
