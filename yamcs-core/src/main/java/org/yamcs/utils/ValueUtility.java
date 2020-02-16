package org.yamcs.utils;

import org.yamcs.protobuf.Yamcs.Value.Builder;
import org.yamcs.protobuf.Yamcs.Value.Type;

import java.util.Arrays;
import java.util.function.DoubleConsumer;
import java.util.function.LongConsumer;

import org.yamcs.parameter.AggregateValue;
import org.yamcs.parameter.ArrayValue;
import org.yamcs.parameter.BinaryValue;
import org.yamcs.parameter.BooleanValue;
import org.yamcs.parameter.DoubleValue;
import org.yamcs.parameter.FloatValue;
import org.yamcs.parameter.SInt32Value;
import org.yamcs.parameter.SInt64Value;
import org.yamcs.parameter.StringValue;
import org.yamcs.parameter.TimestampValue;
import org.yamcs.parameter.UInt32Value;
import org.yamcs.parameter.UInt64Value;
import org.yamcs.parameter.Value;
import org.yamcs.yarch.ColumnDefinition;
import org.yamcs.yarch.DataType;

import com.google.protobuf.ByteString;

public class ValueUtility {
    public static Value getUint32Value(int x) {
        return new UInt32Value(x); 
    }

    public static Value getSint32Value(int x) {
        return new SInt32Value(x); 
    }

    public static Value getUint64Value(long x) {
        return new UInt64Value(x); 
    }

    public static Value getSint64Value(long x) {
        return new SInt64Value(x); 
    }

    public static Value getStringValue(String x) {
        return new StringValue(x); 
    }

    public static Value getBinaryValue(byte[] x) {
        return new BinaryValue(x); 
    }

    public static Value getTimestampValue(long x) {
        return new TimestampValue(x); 
    }


    public static Value getBooleanValue(boolean b) {
        return new BooleanValue(b); 
    }


    public static Value getFloatValue(float f) {
        return new FloatValue(f); 
    }

    public static Value getDoubleValue(double d) {
        return new DoubleValue(d);
    }
    
    public static org.yamcs.protobuf.Yamcs.Value getDoubleGbpValue(double d) {
        return org.yamcs.protobuf.Yamcs.Value.newBuilder().setType(Type.DOUBLE).setDoubleValue(d).build();
    }
    
    public static org.yamcs.protobuf.Yamcs.Value getStringGbpValue(String s) {
        return org.yamcs.protobuf.Yamcs.Value.newBuilder().setType(Type.STRING).setStringValue(s).build();
    }
    
    public static org.yamcs.protobuf.Yamcs.Value getUint32GbpValue(int x) {
        return org.yamcs.protobuf.Yamcs.Value.newBuilder().setType(Type.UINT32).setUint32Value(x).build();
    }
    
    public static Value getColumnValue(ColumnDefinition cd, Object v) {
        switch (cd.getType().val) {
        case INT:
            return getSint32Value((Integer)v);
        case SHORT:
            return getUint32Value((Short)v);
        case BYTE:
            return getUint32Value((Byte)v);
        case STRING:
        case ENUM:
            return getStringValue((String)v);
        case TIMESTAMP:
            return getTimestampValue((Long)v);
        case BINARY:
            return getBinaryValue((byte[])v);
        case BOOLEAN:
            return getBooleanValue((Boolean)v);
        case DOUBLE:
            return getDoubleValue((Double)v);
        case LIST:
        case PROTOBUF:
        case TUPLE:
        default:
            throw new IllegalArgumentException("cannot convert type to value "+cd.getType());
        }
    }


    public static Object getYarchValue(Value v) {
        switch(v.getType()) {
        case BINARY:
            return v.getBinaryValue();
        case SINT32:
            return v.getSint32Value();
        case UINT32:
            return v.getUint32Value();
        case DOUBLE:
            return v.getDoubleValue();
        case FLOAT:
            return (double)v.getFloatValue();
        case STRING:
            return v.getStringValue();
        case TIMESTAMP:
            return v.getTimestampValue();
        case BOOLEAN:
            return v.getBooleanValue();
        case SINT64:
            return v.getSint64Value();
        case UINT64:
            return v.getUint64Value();
        default: 
            throw new IllegalArgumentException("cannot values of type "+v.getType());
        }
        
    }

    public static Object getYarchValue(org.yamcs.protobuf.Yamcs.Value v) {
        switch(v.getType()) {
        case BINARY:
            return v.getBinaryValue().toByteArray();
        case SINT32:
            return v.getSint32Value();
        case UINT32:
            return v.getUint32Value();
        case DOUBLE:
            return v.getDoubleValue();
        case FLOAT:
            return (double)v.getFloatValue();
        case STRING:
            return v.getStringValue();
        case TIMESTAMP:
            return v.getTimestampValue();
        case BOOLEAN:
            return v.getBooleanValue();
        case SINT64:
            return v.getSint64Value();
        case UINT64:
            return v.getUint64Value();
        default: 
            throw new IllegalArgumentException("cannot values of type "+v.getType());
        }
        
    }

    public static DataType getYarchType(Type type) {
        switch(type) {
        case BINARY:
            return DataType.BINARY;
        case SINT32:
            return DataType.INT;
        case UINT32:
            return DataType.INT;
        case DOUBLE:
            return DataType.DOUBLE;
        case FLOAT:
            return DataType.DOUBLE;
        case STRING:
            return DataType.STRING;
        case TIMESTAMP:
            return DataType.TIMESTAMP;
        }
        throw new IllegalArgumentException("cannot values of type "+type);
    }
    
    public static boolean equals(Value a, Value b) {
        if (a == null ^ b == null)
            return false;
        if (a == null && b == null)
            return true;
        if (a.getType() != b.getType())
            return false;

        switch (a.getType()) {
        case BINARY:
            return Arrays.equals(a.getBinaryValue(), b.getBinaryValue());
        case BOOLEAN:
            return a.getBooleanValue() == b.getBooleanValue();
        case DOUBLE:
            return a.getDoubleValue() == b.getDoubleValue();
        case FLOAT:
            return a.getFloatValue() == b.getFloatValue();
        case SINT32:
            return a.getSint32Value() == b.getSint32Value();
        case SINT64:
            return a.getSint64Value() == b.getSint64Value();
        case STRING:
            return a.getStringValue().equals(b.getStringValue());
        case TIMESTAMP:
            return a.getTimestampValue() == b.getTimestampValue();
        case UINT32:
            return a.getUint32Value() == b.getUint32Value();
        case UINT64:
            return a.getUint64Value() == b.getUint64Value();
        default:
            throw new IllegalArgumentException("Unexpected type " + a.getType());
        }
    }

    // Not perfect. Should also compare compatible types
    public static int compare(Value a, Value b) {
        if (a == null ^ b == null)
            return (a == null) ? -1 : 1;
        if (a == null && b == null)
            return 0;
        if (a.getType() != b.getType())
            return a.getType().compareTo(b.getType());

        switch (a.getType()) {
        case BINARY:
            return String.valueOf(a).compareTo(String.valueOf(b)); // TODO ?
        case BOOLEAN:
            return Boolean.compare(a.getBooleanValue(), b.getBooleanValue());
        case DOUBLE:
            return Double.compare(a.getDoubleValue(), b.getDoubleValue());
        case FLOAT:
            return Float.compare(a.getFloatValue(), b.getFloatValue());
        case SINT32:
            return Integer.compare(a.getSint32Value(), b.getSint32Value());
        case SINT64:
            return Long.compare(a.getSint64Value(), b.getSint64Value());
        case STRING:
            return a.getStringValue().compareTo(b.getStringValue());
        case TIMESTAMP:
            return Long.compare(a.getTimestampValue(), b.getTimestampValue());
        case UINT32:
            return Integer.compareUnsigned(a.getUint32Value(), b.getUint32Value());
        case UINT64:
            return Long.compareUnsigned(a.getUint64Value(), b.getUint64Value());
        default:
            throw new IllegalArgumentException("Unexpected type " + a.getType());
        }
    }

    public static org.yamcs.protobuf.Yamcs.Value toGbp(Value v) {
        org.yamcs.protobuf.Yamcs.Value.Builder b = org.yamcs.protobuf.Yamcs.Value.newBuilder();
        b.setType(v.getType());
        switch (v.getType()) {
        case BINARY:
            return b.setBinaryValue(ByteString.copyFrom(v.getBinaryValue())).build();
        case BOOLEAN:
            return b.setBooleanValue(v.getBooleanValue()).build();
        case DOUBLE:
            return b.setDoubleValue(v.getDoubleValue()).build();
        case FLOAT:
            return b.setFloatValue(v.getFloatValue()).build();
        case SINT32:
            return b.setSint32Value(v.getSint32Value()).build();
        case SINT64:
            return b.setSint64Value(v.getSint64Value()).build();
        case STRING:
            return b.setStringValue(v.getStringValue()).build();
        case TIMESTAMP:
            return b.setTimestampValue(v.getTimestampValue())
                    .setStringValue(TimeEncoding.toString(v.getTimestampValue()))
                    .build();
        case UINT32:
            return b.setUint32Value(v.getUint32Value()).build();
        case UINT64:
            return b.setUint64Value(v.getUint64Value()).build();
        case AGGREGATE:
            return b.setAggregateValue(toGbp((AggregateValue) v)).build();
        case ARRAY:
            fillInArray(b, (ArrayValue) v);
            return b.build();
        default:
            throw new IllegalArgumentException("Unexpected type " + v.getType());
        }
    }
    private static void fillInArray(Builder b, ArrayValue av) {
        int n = av.flatLength();
        for(int i = 0; i<n; i++) {
            b.addArrayValue(toGbp(av.getElementValue(i)));
        }
    }

    public static org.yamcs.protobuf.Yamcs.AggregateValue toGbp(AggregateValue v) {
        int n = v.numMembers();
        org.yamcs.protobuf.Yamcs.AggregateValue.Builder b = org.yamcs.protobuf.Yamcs.AggregateValue.newBuilder();
        for(int i = 0; i<n; i++) {
            b.addName(v.getMemberName(i));
            b.addValue(toGbp(v.getMemberValue(i)));
        }
        
        return b.build();
    }

    public static Value fromGpb(org.yamcs.protobuf.Yamcs.Value v) {
        switch (v.getType()) {
        case BINARY:
            return new BinaryValue(v.getBinaryValue().toByteArray());
        case BOOLEAN:
            return new BooleanValue(v.getBooleanValue());
        case DOUBLE:
            return new DoubleValue(v.getDoubleValue());
        case FLOAT:
            return new FloatValue(v.getFloatValue());
        case SINT32:
            return new SInt32Value(v.getSint32Value());
        case SINT64:
            return new SInt64Value(v.getSint64Value());
        case STRING:
            return new StringValue(v.getStringValue());
        case TIMESTAMP:
            return new TimestampValue(v.getTimestampValue());
        case UINT32:
            return new UInt32Value(v.getUint32Value());
        case UINT64:
            return new UInt64Value(v.getUint64Value());
        default:
            throw new IllegalArgumentException("Unexpected type " + v.getType());
        }
    }

    /**
     * if the passed on value is INT32, SINT32, INT64 or SINT64, invoke the function on the long value and return true
     * if v is of other types return false
     * 
     * @param v
     * @param c
     * @return
     */
    public static boolean processAsLong(Value v, LongConsumer c) {
        switch (v.getType()) {
        case SINT32:
            c.accept(v.getSint32Value());
            return true;
        case SINT64:
            c.accept(v.getSint64Value());
            return true;
        case UINT32:
            c.accept(v.getUint32Value()&0xFFFFFFFFL);
            return true;
        case UINT64:
            c.accept(v.getUint64Value());
            return true;
        default: 
            return false;
        }
    }

    /**
     * if the passed on value is float, double or integer invoke the function on the double value and return true
     * if v is of other types return false
     * 
     * @param v
     * @param c
     * @return
     */
    public static boolean processAsDouble(Value v, DoubleConsumer c) {
        switch (v.getType()) {
        case DOUBLE:
            c.accept(v.getDoubleValue());
            return true;
        case FLOAT:
            c.accept(v.getFloatValue());
            return true;
        case SINT32:
            c.accept(v.getSint32Value());
            return true;
        case SINT64:
            c.accept(v.getSint64Value());
            return true;
        case UINT32:
            c.accept(v.getUint32Value()&0xFFFFFFFFL);
            return true;
        case UINT64:
            c.accept(UnsignedLong.toDouble(v.getUint64Value()));
            return true;
        default: 
            return false;
        }
    }
    
    /**
     * if the passed on value is FLOAT or DOUBLE, invoke the function on the double value and return true
     * if v is of other types return false
     * 
     * @param v
     * @param c
     * @return
     */
    public static boolean processAsDouble1(Value v, DoubleConsumer c) {
        switch (v.getType()) {
        case DOUBLE:
            c.accept(v.getDoubleValue());
            return true;
        case FLOAT:
            c.accept(v.getFloatValue());
            return true;
        default: 
            return false;
        }
    }
}
