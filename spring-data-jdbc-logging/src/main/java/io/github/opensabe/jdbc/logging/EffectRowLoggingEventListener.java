package io.github.opensabe.jdbc.logging;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.common.P6LogQuery;
import com.p6spy.engine.common.PreparedStatementInformation;
import com.p6spy.engine.common.StatementInformation;
import com.p6spy.engine.logging.Category;
import com.p6spy.engine.logging.LoggingEventListener;

import java.sql.SQLException;

/**
 * @author heng.ma
 */
public class EffectRowLoggingEventListener extends LoggingEventListener {


    @Override
    public void onAfterCommit(ConnectionInformation connectionInformation, long timeElapsedNanos, SQLException e) {

    }

    @Override
    public void onAfterExecuteUpdate(PreparedStatementInformation statementInformation, long timeElapsedNanos, int rowCount, SQLException e) {
        super.onAfterExecuteUpdate(statementInformation, timeElapsedNanos, rowCount, e);
        P6LogQuery.log(Category.COMMIT,  "", "SQL update affected "+rowCount+" rows");
    }

    @Override
    public void onAfterExecuteUpdate(StatementInformation statementInformation, long timeElapsedNanos, String sql, int rowCount, SQLException e) {
        super.onAfterExecuteUpdate(statementInformation, timeElapsedNanos, sql, rowCount, e);
        P6LogQuery.log(Category.COMMIT,  "", "SQL update affected "+rowCount+" rows");
    }

    @Override
    public void onAfterExecuteBatch(StatementInformation statementInformation, long timeElapsedNanos, int[] updateCounts, SQLException e) {
        super.onAfterExecuteBatch(statementInformation, timeElapsedNanos, updateCounts, e);
        P6LogQuery.log(Category.COMMIT, "", "SQL update affected "+updateCounts.length+" rows");
    }


}
