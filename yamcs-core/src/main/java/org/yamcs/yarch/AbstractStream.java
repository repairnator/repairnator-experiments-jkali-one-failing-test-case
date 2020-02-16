package org.yamcs.yarch;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.yamcs.utils.LoggingUtils;

public abstract class AbstractStream implements Stream {
    protected String name;
    protected TupleDefinition outputDefinition;
    final protected Collection<StreamSubscriber> subscribers = new ConcurrentLinkedQueue<>();

    protected volatile int state = SETUP;

    protected Logger log;

    protected YarchDatabaseInstance ydb;
    private volatile AtomicLong emitedTuples = new AtomicLong();
    private volatile AtomicInteger subscriberCount = new AtomicInteger();
    private ExceptionHandler handler;
    
    protected AbstractStream(YarchDatabaseInstance ydb, String name, TupleDefinition definition) {
        this.name = name;
        this.outputDefinition = definition;
        this.ydb = ydb;
        log = LoggingUtils.getLogger(this.getClass(), ydb.getName(), this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.yamcs.yarch.Stream#start()
     */
    @Override
    public abstract void start();

    /*
     * (non-Javadoc)
     * 
     * @see org.yamcs.yarch.Stream#getDefinition()
     */
    @Override
    public TupleDefinition getDefinition() {
        return outputDefinition;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.yamcs.yarch.Stream#emitTuple(org.yamcs.yarch.Tuple)
     */
    @Override
    public void emitTuple(Tuple tuple) {
        emitedTuples.incrementAndGet();
        for (StreamSubscriber s : subscribers) {
            try {
                s.onTuple(this, tuple);
            } catch (Exception e) {
                if(handler!=null) {
                    handler.handle(tuple, s, e);
                } else {
                    log.warn("Exception received when emitting tuple to subscriber " + s + ": {}", e);
                    throw e;
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.yamcs.yarch.Stream#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.yamcs.yarch.Stream#setName(java.lang.String)
     */
    @Override
    public void setName(String streamName) {
        this.name = streamName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.yamcs.yarch.Stream#addSubscriber(org.yamcs.yarch.StreamSubscriber)
     */
    @Override
    public void addSubscriber(StreamSubscriber s) {
        subscribers.add(s);
        subscriberCount.incrementAndGet();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.yamcs.yarch.Stream#removeSubscriber(org.yamcs.yarch.StreamSubscriber)
     */
    @Override
    public void removeSubscriber(StreamSubscriber s) {
        subscribers.remove(s);
        subscriberCount.decrementAndGet();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.yamcs.yarch.Stream#getColumnDefinition(java.lang.String)
     */
    @Override
    public ColumnDefinition getColumnDefinition(String colName) {
        return outputDefinition.getColumn(colName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.yamcs.yarch.Stream#close()
     */
    @Override
    final public void close() {
        if (state == QUITTING)
            return;
        state = QUITTING;

        ydb.removeStream(name);
        log.debug("Closed stream {} num emitted tuples: {}", name, getNumEmittedTuples());
        doClose();
        for (StreamSubscriber s : subscribers) {
            s.streamClosed(this);
        }
    }

    protected abstract void doClose();

    @Override
    public String toString() {
        return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.yamcs.yarch.Stream#getState()
     */
    @Override
    public int getState() {
        return state;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.yamcs.yarch.Stream#getNumEmittedTuples()
     */
    @Override
    public long getNumEmittedTuples() {
        return emitedTuples.get();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.yamcs.yarch.Stream#getSubscriberCount()
     */
    @Override
    public int getSubscriberCount() {
        return subscriberCount.get();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.yamcs.yarch.Stream#getSubscribers()
     */
    @Override
    public Collection<StreamSubscriber> getSubscribers() {
        return Collections.unmodifiableCollection(subscribers);
    }
    
    public void exceptionHandler(ExceptionHandler h) {
        this.handler = h;
    }
   
}
