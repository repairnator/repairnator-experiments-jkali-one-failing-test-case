package org.yamcs.yarch;

import java.util.List;

import org.junit.Test;
import org.yamcs.protobuf.Yamcs.Event;
import org.yamcs.protobuf.Yamcs.Event.EventSeverity;
import org.yamcs.utils.TimeEncoding;
import org.yamcs.yarch.AbstractStream;
import org.yamcs.yarch.DataType;
import org.yamcs.yarch.Tuple;
import org.yamcs.yarch.TupleDefinition;
import org.yamcs.yarch.YarchDatabaseInstance;
import org.yamcs.yarch.YarchException;
import org.yamcs.yarch.streamsql.GenericStreamSqlException;
import org.yamcs.yarch.streamsql.StreamSqlResult;

import static org.junit.Assert.*;

public class StreamSelectProtobufTest extends YarchTestCase {
    StreamSqlResult res;
    final int n = 20;

    public void createFeeder1() throws YarchException {
        AbstractStream s;
        YarchDatabaseInstance dict = YarchDatabase.getInstance(context.getDbName());
        final TupleDefinition tpdef = new TupleDefinition();
        tpdef.addColumn("event", DataType.protobuf(Event.class.getName()));
        tpdef.addColumn("y", DataType.INT);

        s = (new AbstractStream(dict, "stream_in", tpdef) {
            @Override
            public void start() {
                for (int i = 0; i < n; i++) {
                    Event event = Event.newBuilder().setSource("test"+i).setSeqNumber(i)
                            .setGenerationTime(TimeEncoding.getWallclockTime())
                            .setReceptionTime(TimeEncoding.getWallclockTime())
                            .setMessage("msg"+i)
                            .setSeverity(i==5?EventSeverity.INFO:EventSeverity.WARNING).build();
                    Integer y = i * 2;
                    Tuple t = new Tuple(tpdef, new Object[] { event, y });
                    emitTuple(t);
                    close();
                }
            }

            @Override
            protected void doClose() {
            }
        });
        dict.addStream(s);
    }

    @Test
    public void testSelectInvalidField() throws Exception {
        createFeeder1();
        GenericStreamSqlException ge = null;
        try {
            res = execute("create stream stream_out1 as select event from stream_in where event.invalidFieldName > 3");
        } catch (GenericStreamSqlException e) {
            ge = e;
        }
        
        assertNotNull(ge);
        assertTrue(ge.getMessage().contains("'event.invalidFieldName' is not an input column"));
    }
    
    @Test
    public void test1() throws Exception {
        createFeeder1();
        res = execute("create stream stream_out1 as select event from stream_in where event.seqNumber >= ?", 3);
        
        List<Tuple> tlist = fetchAll("stream_out1");
        
        assertEquals(n-3, tlist.size());
        Tuple t0 = tlist.get(0);
        assertEquals(3, ((Event) t0.getColumn("event")).getSeqNumber());
    }
    
    
    @Test
    public void test2() throws Exception {
        createFeeder1();
        res = execute("create stream stream_out1 as select event from stream_in where event.message like '%15%'");
        
        List<Tuple> tlist = fetchAll("stream_out1");
        
        assertEquals(1, tlist.size());
        Tuple t0 = tlist.get(0);
        assertEquals(15, ((Event) t0.getColumn("event")).getSeqNumber());
    }
    
    @Test
    public void test3() throws Exception {
        createFeeder1();
        res = execute("create stream stream_out1 as select event from stream_in where event.severity in ('INFO')");
        
        List<Tuple> tlist = fetchAll("stream_out1");
        
        assertEquals(1, tlist.size());
        Tuple t0 = tlist.get(0);
        assertEquals(5, ((Event) t0.getColumn("event")).getSeqNumber());
    }
    
    @Test
    public void test4() throws Exception {
        createFeeder1();
        res = execute("create stream stream_out1 as select event from stream_in where event.severity in (?) and event.message LIKE ? ", "WARNING", "%7%");
        
        List<Tuple> tlist = fetchAll("stream_out1");
        
        assertEquals(2, tlist.size());
        Tuple t0 = tlist.get(0);
        assertEquals(7, ((Event) t0.getColumn("event")).getSeqNumber());
        Tuple t1 = tlist.get(1);
        assertEquals(17, ((Event) t1.getColumn("event")).getSeqNumber());
    }
}
