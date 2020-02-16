package org.yamcs.yarch.oldrocksdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yamcs.CrashHandler;
import org.yamcs.EventCrashHandler;
import org.yamcs.YamcsServer;
import org.yamcs.api.EventProducer;
import org.yamcs.api.EventProducerFactory;
import org.yamcs.utils.TimeEncoding;
import org.yamcs.yarch.ColumnDefinition;
import org.yamcs.yarch.DataType;
import org.yamcs.yarch.PartitioningSpec;
import org.yamcs.yarch.Stream;
import org.yamcs.yarch.TableDefinition;
import org.yamcs.yarch.Tuple;
import org.yamcs.yarch.TupleDefinition;
import org.yamcs.yarch.YarchDatabaseInstance;

/**
 * table writer that stores each partition by value in a different column
 * family.
 * 
 * It is also used when the table is not partitioned by value
 * 
 * @author nm
 *
 */
public class CfTableWriter extends AbstractTableWriter {
    private final RdbPartitionManager partitionManager;
    private final PartitioningSpec partitioningSpec;
    Logger log = LoggerFactory.getLogger(this.getClass().getName());
    RDBFactory rdbFactory;
    CrashHandler crashHandler;

    public CfTableWriter(YarchDatabaseInstance ydb, TableDefinition tableDefinition, InsertMode mode,
            RdbPartitionManager pm) throws IOException {
        super(ydb, tableDefinition, mode);
        this.partitioningSpec = tableDefinition.getPartitioningSpec();
        this.partitionManager = pm;
        rdbFactory = RDBFactory.getInstance(ydb.getName());
        crashHandler = YamcsServer.getCrashHandler(ydb.getName());
    }

    @Override
    public void onTuple(Stream stream, Tuple t) {
        try {
            RdbPartition partition = getDbPartition(t);
            YRDB db = rdbFactory.getRdb(tableDefinition.getDataDir() + "/" + partition.dir, false);

            boolean inserted = false;
            boolean updated = false;

            switch (mode) {
            case INSERT:
            case LOAD:
                inserted = insert(db, partition, t);
                break;
            case UPSERT:
                inserted = upsert(db, partition, t);
                updated = !inserted;
                break;
            case INSERT_APPEND:
                inserted = insertAppend(db, partition, t);
                break;
            case UPSERT_APPEND:
                inserted = upsertAppend(db, partition, t);
                updated = !inserted;
                break;
            }

            if (inserted && tableDefinition.hasHistogram()) {
                addHistogram(db, t);
            }

            if (updated && tableDefinition.hasHistogram()) {
                // TODO updateHistogram(t);
            }
            rdbFactory.dispose(db);
        } catch (IOException e) {
            log.error("failed to insert a record: ", e);
            e.printStackTrace();
            crashHandler.handleCrash("IO", "failed to insert a record: " + e.getMessage());
        } catch (RocksDBException e) {
            log.error("failed to insert a record: ", e);
            e.printStackTrace();
            crashHandler.handleCrash("RocksDb", "failed to insert a record: " + e.getMessage());
        }
    }

    private boolean insert(YRDB db, RdbPartition partition, Tuple t) throws RocksDBException {
        byte[] k = tableDefinition.serializeKey(t);
        byte[] v = tableDefinition.serializeValue(t);
        ColumnFamilyHandle cfh = db.getColumnFamilyHandle(partition.binaryValue);
        if (cfh == null) {
            cfh = db.createColumnFamily(partition.binaryValue);
        }
        if (db.get(cfh, k) == null) {
            db.put(cfh, k, v);
            return true;
        } else {
            return false;
        }
    }

    private boolean upsert(YRDB db, RdbPartition partition, Tuple t) throws RocksDBException {
        byte[] k = tableDefinition.serializeKey(t);
        byte[] v = tableDefinition.serializeValue(t);
        ColumnFamilyHandle cfh = db.getColumnFamilyHandle(partition.binaryValue);
        if (cfh == null) {
            cfh = db.createColumnFamily(partition.binaryValue);
        }
        if (db.get(cfh, k) == null) {
            db.put(cfh, k, v);
            return true;
        } else {
            db.put(cfh, k, v);
            return false;
        }
    }

    /**
     * returns true if a new record has been inserted and false if an record was
     * already existing with this key (even if modified)
     * 
     * @param partition
     * @throws RocksDBException
     */
    private boolean insertAppend(YRDB db, RdbPartition partition, Tuple t) throws RocksDBException {
        byte[] k = tableDefinition.serializeKey(t);
        ColumnFamilyHandle cfh = db.getColumnFamilyHandle(partition.binaryValue);
        if (cfh == null) {
            cfh = db.createColumnFamily(partition.binaryValue);
        }
        byte[] v = db.get(cfh, k);
        boolean inserted = false;
        if (v != null) {// append to an existing row
            Tuple oldt = tableDefinition.deserialize(k, v);
            TupleDefinition tdef = t.getDefinition();
            TupleDefinition oldtdef = oldt.getDefinition();

            boolean changed = false;
            ArrayList<Object> cols = new ArrayList<Object>(oldt.getColumns().size() + t.getColumns().size());
            cols.addAll(oldt.getColumns());
            for (ColumnDefinition cd : tdef.getColumnDefinitions()) {
                if (!oldtdef.hasColumn(cd.getName())) {
                    oldtdef.addColumn(cd);
                    cols.add(t.getColumn(cd.getName()));
                    changed = true;
                }
            }
            if (changed) {
                oldt.setColumns(cols);
                v = tableDefinition.serializeValue(oldt);
                db.put(cfh, k, v);
            }
        } else {// new row
            inserted = true;
            v = tableDefinition.serializeValue(t);
            db.put(cfh, k, v);
        }
        return inserted;
    }

    private boolean upsertAppend(YRDB db, RdbPartition partition, Tuple t) throws RocksDBException {
        byte[] k = tableDefinition.serializeKey(t);
        ColumnFamilyHandle cfh = db.getColumnFamilyHandle(partition.binaryValue);
        if (cfh == null) {
            cfh = db.createColumnFamily(partition.binaryValue);
        }
        byte[] v = db.get(cfh, k);
        boolean inserted = false;
        if (v != null) {// append to an existing row
            Tuple oldt = tableDefinition.deserialize(k, v);
            TupleDefinition tdef = t.getDefinition();
            TupleDefinition oldtdef = oldt.getDefinition();

            boolean changed = false;
            ArrayList<Object> cols = new ArrayList<Object>(oldt.getColumns().size() + t.getColumns().size());
            cols.addAll(oldt.getColumns());
            for (ColumnDefinition cd : tdef.getColumnDefinitions()) {
                if (oldtdef.hasColumn(cd.getName())) {
                    // currently always says it changed. Not sure if it's worth
                    // checking if different
                    cols.set(oldt.getColumnIndex(cd.getName()), t.getColumn(cd.getName()));
                    changed = true;
                } else {
                    oldtdef.addColumn(cd);
                    cols.add(t.getColumn(cd.getName()));
                    changed = true;
                }
            }
            if (changed) {
                oldt.setColumns(cols);
                v = tableDefinition.serializeValue(oldt);
                db.put(cfh, k, v);
            }
        } else {// new row
            inserted = true;
            v = tableDefinition.serializeValue(t);
            db.put(cfh, k, v);
        }
        return inserted;
    }

    /**
     * get the filename where the tuple would fit (can be a partition)
     * 
     * @param t
     * @return the partition where the tuple fits
     * @throws IOException
     *             if there was an error while creating the directories where
     *             the file should be located
     */
    public RdbPartition getDbPartition(Tuple t) throws IOException {
        long time = TimeEncoding.INVALID_INSTANT;
        Object value = null;
        if (partitioningSpec.timeColumn != null) {
            time = (Long) t.getColumn(partitioningSpec.timeColumn);
        }
        if (partitioningSpec.valueColumn != null) {
            value = t.getColumn(partitioningSpec.valueColumn);
            ColumnDefinition cd = tableDefinition.getColumnDefinition(partitioningSpec.valueColumn);
            if (cd.getType() == DataType.ENUM) {
                value = tableDefinition.addAndGetEnumValue(partitioningSpec.valueColumn, (String) value);
            }
        }
        return (RdbPartition) partitionManager.createAndGetPartition(time, value);
    }

    @Override
    public void streamClosed(Stream stream) {
    }

    public void close() {
    }

}