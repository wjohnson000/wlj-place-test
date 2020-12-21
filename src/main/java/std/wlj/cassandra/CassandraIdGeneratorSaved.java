/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.cassandra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.BuiltStatement;
import com.datastax.driver.core.querybuilder.QueryBuilder;

/**
 * ID generator that relies on a "sequence" table with at least one entry keyed as "item".
 * 
 * @author wjohnson000
 *
 */
public class CassandraIdGeneratorSaved implements IdGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraIdGeneratorSaved.class);

    private static final int BLOCK_SIZE  = 250;
    private static final int RETRY_COUNT = 5;

    private CassandraOps cassandraOps = null;
    private Integer nextId = null;
    private Integer maxId  = null;

    public CassandraIdGeneratorSaved(Session session) {
        this.cassandraOps = new CassandraOps(session);
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
        BuiltStatement getItemId = QueryBuilder
                .select()
                .from("sequence")
                .where(QueryBuilder.eq("name", "item"));

        Row seqGet = cassandraOps.execute(getItemId, ConsistencyLevel.LOCAL_QUORUM).one();
        if (seqGet == null) {
            throw new IllegalArgumentException("Sequence 'sequence/item' doesn't exist.");
        }

        int currSeqValue = seqGet.getInt("nextid");
        int nextSeqValue = currSeqValue + BLOCK_SIZE;

        BuiltStatement putItemId = QueryBuilder
                .update("sequence").with(QueryBuilder.set("nextid", nextSeqValue))
                .where(QueryBuilder.eq("name", "item"))
                .onlyIf(QueryBuilder.eq("nextid", currSeqValue));

        Row seqUpd = cassandraOps.execute(putItemId, ConsistencyLevel.LOCAL_SERIAL).one();
        if (seqUpd.getBool("[applied]")) {
            nextId = currSeqValue;
            maxId  = nextSeqValue;
            return true;
        } else {
            return false;
        }
    }
}
