package org.yamcs.yarch.oldrocksdb;

import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.junit.Test;
import org.yamcs.yarch.ColumnDefinition;
import org.yamcs.yarch.DataType;
import org.yamcs.yarch.PartitioningSpec;
import org.yamcs.yarch.TableDefinition;
import org.yamcs.yarch.TableDefinition.PartitionStorage;
import org.yamcs.yarch.TableWriter;
import org.yamcs.yarch.TableWriter.InsertMode;
import org.yamcs.yarch.Tuple;
import org.yamcs.yarch.TupleDefinition;
import org.yamcs.yarch.YarchTestCase;

import com.google.common.io.Files;


public class RdbEngineTest extends YarchTestCase {

    @Test
    public void testCreateDrop() throws Exception {
        RdbStorageEngine rse = new RdbStorageEngine();

        TupleDefinition tdef=new TupleDefinition();
        tdef.addColumn(new ColumnDefinition("gentime", DataType.TIMESTAMP));
        tdef.addColumn(new ColumnDefinition("packetid", DataType.INT));	        	       
        TableDefinition tblDef = new TableDefinition("RdbEngineTest", tdef, Arrays.asList("gentime"));
        tblDef.setPartitionStorage(PartitionStorage.COLUMN_FAMILY);


        String tmpdir=Files.createTempDir().getAbsolutePath();
        tblDef.setDataDir(tmpdir);


        PartitioningSpec pspec=PartitioningSpec.timeAndValueSpec("gentime", "packetid");
        pspec.setValueColumnType(DataType.INT);

        tblDef.setPartitioningSpec(pspec);
        IllegalArgumentException iae=null;
        try {
            rse.newTableReaderStream(ydb, tblDef, true, true);
        } catch (IllegalArgumentException e) {
            iae=e;
        }
        assertNotNull(iae);

        rse.createTable(ydb, tblDef);
        TableWriter tw = rse.newTableWriter(ydb, tblDef, InsertMode.INSERT);
        Tuple t = new Tuple(tdef, new Object[]{1000L, 10});
        tw.onTuple(null, t);		 
        rse.dropTable(ydb, tblDef);

        iae=null;
        try {
            rse.newTableReaderStream(ydb, tblDef, true, true);
        } catch (IllegalArgumentException e) {
            iae=e;
        }
        assertNotNull(iae);		 		 		 
    }
}
