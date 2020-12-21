/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.cassandra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

/**
 * ID generator that relies on a "sequence" table with at least one entry keyed as "item".
 * 
 * @author wjohnson000
 *
 */
public class CassandraIdGenerator implements IdGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraIdGenerator.class);

    private static final int BLOCK_SIZE  = 250;
    private static final int RETRY_COUNT = 5;

    /** Two 'prepared statements' used to retrieve and update the pseudo-sequence value */
    private PreparedStatement getNextKey;
    private PreparedStatement updateKeyBlock;

    private CassandraOps cassandraOps = null;
    private Long nextId = null;
    private Long maxId  = null;

    public CassandraIdGenerator(Session session) {
        this.cassandraOps = new CassandraOps(session);
        createPreparedStatements(session);
    }

    @Override
    public synchronized long getNext() {
        if (nextId == null  ||  nextId.intValue() == maxId.intValue()) {
            boolean success = generateKey();
            if (! success) {
                LOGGER.error("Unable to generate a key ...");
                throw new IllegalArgumentException("Unable to generate a key ...");
            }
        }

        return nextId++;
    }

    /**
     * Make multiple attempts to get a block of keys
     * 
     * @return TRUE if a key block could be retrieved, FALSE otherwise
     */
    protected boolean generateKey() {
        boolean success = false;

        for (int i=0;  i<RETRY_COUNT && ! success;  i++) {
            success = generateKeyInternal();
        }

        return success;
    }

    /**
     * Get the next value from the sequence and update the value if no other process updated
     * it in the meantime.
     * 
     * @return TRUE if a key block could be retrieved, FALSE otherwise
     */
    protected boolean generateKeyInternal() {
        BoundStatement getItemId = getNextKey.bind();
        Row seqGet = cassandraOps.execute(getItemId, ConsistencyLevel.LOCAL_QUORUM).one();
        if (seqGet == null) {
            throw new IllegalArgumentException("Sequence 'sequence/item' doesn't exist.");
        }

        long currSeqValue = seqGet.getLong("nextid");
        long nextSeqValue = currSeqValue + BLOCK_SIZE;

        BoundStatement putItemId = updateKeyBlock.bind(nextSeqValue, currSeqValue);
        Row seqUpd = cassandraOps.execute(putItemId, ConsistencyLevel.LOCAL_SERIAL).one();
        if (seqUpd.getBool("[applied]")) {
            nextId = currSeqValue;
            maxId  = nextSeqValue;
            return true;
        } else {
            return false;
        }
    }

    protected void createPreparedStatements(Session session) {
        getNextKey = session.prepare("SELECT nextid FROM sequence WHERE name = 'item'");

        updateKeyBlock = session.prepare("UPDATE sequence SET nextid = ? WHERE name = 'item' IF nextid = ?");
    }
}
