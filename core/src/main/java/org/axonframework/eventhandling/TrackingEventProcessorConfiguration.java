package org.axonframework.eventhandling;

import org.axonframework.common.Assert;
import org.axonframework.common.AxonThreadFactory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;

/**
 * Configuration object for the {@link TrackingEventProcessor}. The TrackingEventProcessorConfiguration provides access to the options to tweak
 * various settings. Instances are not thread-safe and should not be altered after they have been used to initialize
 * a TrackingEventProcessor.
 *
 * @author Christophe Bouhier
 * @author Allard Buijze
 */
public class TrackingEventProcessorConfiguration {

    private static final int DEFAULT_BATCH_SIZE = 1;
    private static final int DEFAULT_THREAD_COUNT = 1;

    private final int maxThreadCount;
    private int batchSize;
    private int initialSegmentCount;
    private Function<String, ThreadFactory> threadFactory;

    private TrackingEventProcessorConfiguration(int numberOfSegments) {
        this.batchSize = DEFAULT_BATCH_SIZE;
        this.initialSegmentCount = numberOfSegments;
        this.maxThreadCount = numberOfSegments;
        this.threadFactory = pn -> new AxonThreadFactory("EventProcessor[" + pn + "]");
    }

    /**
     * Initialize a configuration with single threaded processing.
     *
     * @return a Configuration prepared for single threaded processing
     */
    public static TrackingEventProcessorConfiguration forSingleThreadedProcessing() {
        return new TrackingEventProcessorConfiguration(DEFAULT_THREAD_COUNT);
    }

    /**
     * Initialize a configuration instance with the given {@code threadCount}. This is both the number of threads
     * that a processor will start for processing, as well as the initial number of segments that will be created when
     * the processor is first started.
     *
     * @param threadCount the number of segments to process in parallel
     * @return a newly created configuration
     */
    public static TrackingEventProcessorConfiguration forParallelProcessing(int threadCount) {
        return new TrackingEventProcessorConfiguration(threadCount);
    }

    /**
     * @param batchSize The maximum number of events to process in a single batch.
     * @return {@code this} for method chaining
     */
    public TrackingEventProcessorConfiguration andBatchSize(int batchSize) {
        Assert.isTrue(batchSize > 0, () -> "Batch size must be greater or equal to 1");
        this.batchSize = batchSize;
        return this;
    }

    /**
     * @param segmentsSize The number of segments requested for handling asynchronous processing of events.
     * @return {@code this} for method chaining
     */
    public TrackingEventProcessorConfiguration andInitialSegmentsCount(int segmentsSize) {
        this.initialSegmentCount = segmentsSize;
        return this;
    }

    /**
     * Sets the ThreadFactory to use to create the threads to process events on. Each Segment will be processed by a
     * separate thread.
     *
     * @param threadFactory The factory to create threads with
     * @return {@code this} for method chaining
     */
    public TrackingEventProcessorConfiguration andThreadFactory(Function<String, ThreadFactory> threadFactory) {
        this.threadFactory = threadFactory;
        return this;
    }

    /**
     * @return the maximum number of events to process in a single batch.
     */
    public int getBatchSize() {
        return batchSize;
    }

    /**
     * @return the number of segments requested for handling asynchronous processing of events.
     */
    public int getInitialSegmentsCount() {
        return initialSegmentCount;
    }

    /**
     * @return the pool size of core threads as per {@link ThreadPoolExecutor#getCorePoolSize()}
     */
    public int getMaxThreadCount() {
        return maxThreadCount;
    }

    /**
     * Provides the ThreadFactory to use to construct Threads for the processor with given {@code processorName}
     *
     * @param processorName The name of the processor for which to return the ThreadFactory
     * @return the thread factory configured
     */
    public ThreadFactory getThreadFactory(String processorName) {
        return threadFactory.apply(processorName);
    }
}
