/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.presto.memory;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import org.testng.annotations.Test;

import static com.facebook.presto.memory.MemoryReservationHandler.NOT_BLOCKED;
import static io.airlift.testing.Assertions.assertInstanceOf;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

public class TestMemoryContexts
{
    @Test
    public void testMemoryContexts()
    {
        TestMemoryReservationHandler reservationHandler = new TestMemoryReservationHandler(1_000);
        AggregatedMemoryContext aggregateContext = new AggregatedMemoryContext(reservationHandler);
        LocalMemoryContext localContext = aggregateContext.newLocalMemoryContext();

        assertEquals(localContext.addBytes(10), NOT_BLOCKED);
        assertEquals(localContext.getBytes(), 10);
        assertEquals(aggregateContext.getBytes(), 10);
        assertEquals(reservationHandler.getReservation(), aggregateContext.getBytes());

        LocalMemoryContext secondLocalContext = aggregateContext.newLocalMemoryContext();
        assertEquals(secondLocalContext.addBytes(20), NOT_BLOCKED);
        assertEquals(secondLocalContext.getBytes(), 20);
        assertEquals(aggregateContext.getBytes(), 30);
        assertEquals(localContext.getBytes(), 10);
        assertEquals(reservationHandler.getReservation(), aggregateContext.getBytes());

        aggregateContext.close();

        assertEquals(aggregateContext.getBytes(), 0);
        assertEquals(reservationHandler.getReservation(), 0);
    }

    @Test
    public void testTryReserve()
    {
        TestMemoryReservationHandler reservationHandler = new TestMemoryReservationHandler(1_000);
        AggregatedMemoryContext parentContext = new AggregatedMemoryContext(reservationHandler);
        AggregatedMemoryContext aggregateContext1 = parentContext.newAggregatedMemoryContext();
        AggregatedMemoryContext aggregateContext2 = parentContext.newAggregatedMemoryContext();
        LocalMemoryContext childContext1 = aggregateContext1.newLocalMemoryContext();

        assertTrue(childContext1.tryReserveMemory(500));
        assertTrue(childContext1.tryReserveMemory(500));
        assertFalse(childContext1.tryReserveMemory(1));
        assertEquals(reservationHandler.getReservation(), parentContext.getBytes());

        aggregateContext1.close();
        aggregateContext2.close();
        parentContext.close();

        assertEquals(aggregateContext1.getBytes(), 0);
        assertEquals(aggregateContext2.getBytes(), 0);
        assertEquals(parentContext.getBytes(), 0);
        assertEquals(reservationHandler.getReservation(), 0);
    }

    @Test
    public void testHieararchicalMemoryContexts()
    {
        TestMemoryReservationHandler reservationHandler = new TestMemoryReservationHandler(1_000);
        AggregatedMemoryContext parentContext = new AggregatedMemoryContext(reservationHandler);
        AggregatedMemoryContext aggregateContext1 = parentContext.newAggregatedMemoryContext();
        AggregatedMemoryContext aggregateContext2 = parentContext.newAggregatedMemoryContext();
        LocalMemoryContext childContext1 = aggregateContext1.newLocalMemoryContext();
        LocalMemoryContext childContext2 = aggregateContext2.newLocalMemoryContext();

        assertInstanceOf(parentContext.getReservationHandler(), TestMemoryReservationHandler.class);
        assertNull(aggregateContext1.getReservationHandler());
        assertNull(aggregateContext2.getReservationHandler());

        assertEquals(childContext1.setBytes(1), NOT_BLOCKED);
        assertEquals(childContext2.setBytes(1), NOT_BLOCKED);

        assertEquals(aggregateContext1.getBytes(), 1);
        assertEquals(aggregateContext2.getBytes(), 1);
        assertEquals(parentContext.getBytes(), aggregateContext1.getBytes() + aggregateContext2.getBytes());
        assertEquals(reservationHandler.getReservation(), parentContext.getBytes());

        // exhaust the max memory available
        childContext1.setBytes(999);
        assertFalse(childContext2.tryReserveMemory(1));
        assertNotEquals(childContext1.setBytes(1_000), NOT_BLOCKED);

        aggregateContext1.close();
        aggregateContext2.close();
        parentContext.close();

        assertEquals(aggregateContext1.getBytes(), 0);
        assertEquals(aggregateContext2.getBytes(), 0);
        assertEquals(parentContext.getBytes(), 0);
        assertEquals(reservationHandler.getReservation(), parentContext.getBytes());
    }

    @Test
    public void testTransferMemory()
    {
        TestMemoryReservationHandler reservationHandler = new TestMemoryReservationHandler(1_000);
        AggregatedMemoryContext taskContext = new AggregatedMemoryContext(reservationHandler);
        LocalMemoryContext taskLocalMemoryContext = taskContext.newLocalMemoryContext();
        AggregatedMemoryContext pipelineContext = taskContext.newAggregatedMemoryContext();
        AggregatedMemoryContext driverContext = pipelineContext.newAggregatedMemoryContext();
        AggregatedMemoryContext operatorContext = driverContext.newAggregatedMemoryContext();
        LocalMemoryContext operatorLocalMemoryContext = operatorContext.newLocalMemoryContext();

        operatorLocalMemoryContext.setBytes(10);
        assertEquals(taskContext.getBytes(), 10);
        assertEquals(pipelineContext.getBytes(), 10);
        assertEquals(driverContext.getBytes(), 10);
        assertEquals(operatorContext.getBytes(), 10);
        assertEquals(taskLocalMemoryContext.getBytes(), 0);
        assertEquals(reservationHandler.getReservation(), 10);

        operatorLocalMemoryContext.transferOwnership(taskLocalMemoryContext);
        assertEquals(operatorLocalMemoryContext.getBytes(), 0);
        assertEquals(operatorContext.getBytes(), 0);
        assertEquals(pipelineContext.getBytes(), 0);
        assertEquals(driverContext.getBytes(), 0);
        assertEquals(taskContext.getBytes(), 10);
        assertEquals(reservationHandler.getReservation(), 10);
    }

    private static class TestMemoryReservationHandler
            implements MemoryReservationHandler
    {
        private long reservation = 0L;
        private final long maxMemory;
        private SettableFuture<?> future;

        public TestMemoryReservationHandler(long maxMemory)
        {
            this.maxMemory = maxMemory;
        }

        public long getReservation()
        {
            return reservation;
        }

        @Override
        public ListenableFuture<?> reserveMemory(long delta)
        {
            reservation += delta;
            if (delta >= 0) {
                if (reservation >= maxMemory) {
                    future = SettableFuture.create();
                    return future;
                }
            }
            else {
                if (reservation < maxMemory) {
                    if (future != null) {
                        future.set(null);
                    }
                }
            }
            return NOT_BLOCKED;
        }

        @Override
        public boolean tryReserveMemory(long bytes)
        {
            if (reservation + bytes > maxMemory) {
                return false;
            }
            reservation += bytes;
            return true;
        }
    }
}
