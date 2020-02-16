package org.yamcs.artemis;

import java.security.InvalidParameterException;
import java.util.ArrayList;

import org.apache.activemq.artemis.api.core.client.ClientMessage;
import org.yamcs.tctm.ParameterDataLinkInitialiser;
import org.yamcs.api.YamcsApiException;
import org.yamcs.api.artemis.Protocol;
import org.yamcs.protobuf.Pvalue.ParameterData;
import org.yamcs.protobuf.Pvalue.ParameterData.Builder;
import org.yamcs.protobuf.Yamcs.NamedObjectId;
import org.yamcs.protobuf.Pvalue.ParameterValue;
import org.yamcs.yarch.DataType;
import org.yamcs.yarch.Tuple;
import org.yamcs.yarch.TupleDefinition;

/**
 * Translates between Processed Parameter tuples and ActiveMQ messages.
 * @author atu
 */
public class PpTupleTranslator implements TupleTranslator {

    @Override
    public ClientMessage buildMessage( ClientMessage msg, Tuple tuple ) {
        long genTime = (Long)tuple.getColumn(ParameterDataLinkInitialiser.PARAMETER_TUPLE_COL_GENTIME);
        msg.putLongProperty( ParameterDataLinkInitialiser.PARAMETER_TUPLE_COL_GENTIME, genTime);

        String ppGroup =(String)tuple.getColumn(ParameterDataLinkInitialiser.PARAMETER_TUPLE_COL_GROUP); 
        msg.putStringProperty( ParameterDataLinkInitialiser.PARAMETER_TUPLE_COL_GROUP,  ppGroup);


        msg.putIntProperty( ParameterDataLinkInitialiser.PARAMETER_TUPLE_COL_SEQ_NUM, (Integer)tuple.getColumn(ParameterDataLinkInitialiser.PARAMETER_TUPLE_COL_SEQ_NUM) );
        msg.putLongProperty( ParameterDataLinkInitialiser.PARAMETER_TUPLE_COL_RECTIME, (Long)tuple.getColumn(ParameterDataLinkInitialiser.PARAMETER_TUPLE_COL_RECTIME) );

        Builder b = ParameterData.newBuilder();
        b.setGenerationTime(genTime);
        b.setGroup(ppGroup);
        for( int i = 4; i < tuple.size(); i++ ) {
            // PP name is part of the instance
            org.yamcs.parameter.ParameterValue ppValue = (org.yamcs.parameter.ParameterValue)tuple.getColumn(i);
            b.addParameter(ppValue.toGpb(NamedObjectId.newBuilder().setName(ppValue.getParameterQualifiedNamed()).build()));
        }

        Protocol.encode(msg, b.build());
        return msg;
    }

    @Override
    public Tuple buildTuple(ClientMessage message ) {
        Tuple t = null;
        try {
            ParameterData pd = (ParameterData)Protocol.decode(message, ParameterData.newBuilder());
            TupleDefinition tupleDef = ParameterDataLinkInitialiser.PARAMETER_TUPLE_DEFINITION.copy();

            ArrayList<Object> columns = new ArrayList<Object>( 4 + pd.getParameterCount() );
            columns.add(message.getLongProperty( ParameterDataLinkInitialiser.PARAMETER_TUPLE_COL_GENTIME ));
            columns.add(message.getStringProperty( ParameterDataLinkInitialiser.PARAMETER_TUPLE_COL_GROUP ));
            columns.add(message.getIntProperty( ParameterDataLinkInitialiser.PARAMETER_TUPLE_COL_SEQ_NUM ));
            columns.add(message.getLongProperty( ParameterDataLinkInitialiser.PARAMETER_TUPLE_COL_RECTIME ));

            for( ParameterValue pv : pd.getParameterList() ) {
                String processedParameterName = pv.getId().getName();
                if( processedParameterName == null || "".equals( processedParameterName ) ) {
                    throw new InvalidParameterException( "Processed Parameter must have a name." );
                }
                tupleDef.addColumn( processedParameterName, DataType.PARAMETER_VALUE);
                columns.add( org.yamcs.parameter.ParameterValue.fromGpb(processedParameterName, pv));
            }

            t = new Tuple(tupleDef, columns);
        } catch( YamcsApiException e ) {
            throw new IllegalArgumentException(e.toString());
        }
        return t;
    }

}
