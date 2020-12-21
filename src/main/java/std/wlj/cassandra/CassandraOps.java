/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.cassandra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;

/**
 * @author wjohnson000
 *
 */
public class CassandraOps {

    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraOps.class);

    private Session session;

    public CassandraOps(Session session) {
        this.session = session;
    }

    /**
     * Execute the statement with Consistency Level One. If that fails, for example, if the particular
     * node is giving errors, then retry with Consistency Level Local Quorum.
     */
    protected ResultSet execute(Statement statement, ConsistencyLevel consistencyLevel) {
        try {
            return executeDirect(statement, consistencyLevel);
        } catch (RuntimeException e) {
            if (consistencyLevel == ConsistencyLevel.ONE) {
                LOGGER.warn("Exception executing DB statement with ConsistencyLevel.ONE, retrying with LOCAL_QUORUM exception=[{}]", e);
                return executeDirect(statement, ConsistencyLevel.LOCAL_QUORUM);
            }
            throw e;
        }
    }

    protected ResultSet executeDirect(Statement statement, ConsistencyLevel consistencyLevel) {
        if (consistencyLevel.equals(ConsistencyLevel.LOCAL_SERIAL)) {
            statement.setSerialConsistencyLevel(consistencyLevel);
        } else {
            statement.setConsistencyLevel(consistencyLevel);
        }

        return session.execute(statement);
    }

}
