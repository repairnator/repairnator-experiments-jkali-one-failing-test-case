package org.yamcs.yarch.streamsql;

import org.yamcs.yarch.Stream;
import org.yamcs.yarch.YarchDatabase;
import org.yamcs.yarch.YarchDatabaseInstance;

import org.yamcs.yarch.streamsql.ExecutionContext;
import org.yamcs.yarch.streamsql.StreamSqlException;
import org.yamcs.yarch.streamsql.StreamSqlResult;
import org.yamcs.yarch.streamsql.StreamSqlStatement;

public class ShowStreamsStatement extends StreamSqlStatement{

    public ShowStreamsStatement() {

    }

    @Override
    public StreamSqlResult execute(ExecutionContext c) throws StreamSqlException {
        YarchDatabaseInstance dict = YarchDatabase.getInstance(c.getDbName());
        final StringBuffer sb=new StringBuffer();
        synchronized(dict) {
            for(Stream stream:dict.getStreams()) {
                sb.append(stream.toString()).append("\n");
            }
        }
        return new StreamSqlResult() {
            @Override
            public String toString() {
                return sb.toString();
            }
        };
    }

}
