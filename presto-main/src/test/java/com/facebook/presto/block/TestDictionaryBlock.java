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
package com.facebook.presto.block;

import com.facebook.presto.spi.block.Block;
import com.facebook.presto.spi.block.DictionaryBlock;
import com.facebook.presto.spi.block.DictionaryId;
import com.facebook.presto.spi.block.SliceArrayBlock;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;
import io.airlift.slice.Slice;
import org.testng.annotations.Test;

import java.util.List;

import static io.airlift.slice.SizeOf.SIZE_OF_INT;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

public class TestDictionaryBlock
        extends AbstractTestBlock
{
    @Test
    public void testSizeInBytes()
            throws Exception
    {
        Slice[] expectedValues = createExpectedValues(10);
        DictionaryBlock dictionaryBlock = createDictionaryBlock(expectedValues, 100);
        assertEquals(dictionaryBlock.getSizeInBytes(), dictionaryBlock.getDictionary().getSizeInBytes() + (100 * SIZE_OF_INT));
    }

    @Test
    public void testCopyRegionCreatesCompactBlock()
            throws Exception
    {
        Slice[] expectedValues = createExpectedValues(10);
        DictionaryBlock dictionaryBlock = createDictionaryBlock(expectedValues, 100);

        DictionaryBlock copyRegionDictionaryBlock = (DictionaryBlock) dictionaryBlock.copyRegion(1, 3);
        assertTrue(copyRegionDictionaryBlock.isCompact());
    }

    @Test
    public void testCopyPositionsWithCompaction()
            throws Exception
    {
        Slice[] expectedValues = createExpectedValues(10);
        Slice firstExpectedValue = expectedValues[0];
        DictionaryBlock dictionaryBlock = createDictionaryBlock(expectedValues, 100);

        List<Integer> positionsToCopy = Ints.asList(0, 10, 20, 30, 40);
        DictionaryBlock copiedBlock = (DictionaryBlock) dictionaryBlock.copyPositions(positionsToCopy);

        assertEquals(copiedBlock.getDictionary().getPositionCount(), 1);
        assertEquals(copiedBlock.getPositionCount(), positionsToCopy.size());
        assertBlock(copiedBlock.getDictionary(), new Slice[] {firstExpectedValue});
        assertBlock(copiedBlock, new Slice[] {firstExpectedValue, firstExpectedValue, firstExpectedValue, firstExpectedValue, firstExpectedValue});
    }

    @Test
    public void testCopyPositionsWithCompactionsAndReorder()
            throws Exception
    {
        Slice[] expectedValues = createExpectedValues(10);
        DictionaryBlock dictionaryBlock = createDictionaryBlock(expectedValues, 100);
        List<Integer> positionsToCopy = Ints.asList(50, 55, 40, 45, 60);

        DictionaryBlock copiedBlock = (DictionaryBlock) dictionaryBlock.copyPositions(positionsToCopy);

        assertEquals(copiedBlock.getDictionary().getPositionCount(), 2);
        assertEquals(copiedBlock.getPositionCount(), positionsToCopy.size());

        assertBlock(copiedBlock.getDictionary(), new Slice[] {expectedValues[0], expectedValues[5]});
        assertDictionaryIds(copiedBlock, 0, 1, 0, 1, 0);
    }

    @Test
    public void testCopyPositionsSamePosition()
            throws Exception
    {
        Slice[] expectedValues = createExpectedValues(10);
        DictionaryBlock dictionaryBlock = createDictionaryBlock(expectedValues, 100);
        List<Integer> positionsToCopy = Ints.asList(52, 52, 52);

        DictionaryBlock copiedBlock = (DictionaryBlock) dictionaryBlock.copyPositions(positionsToCopy);

        assertEquals(copiedBlock.getDictionary().getPositionCount(), 1);
        assertEquals(copiedBlock.getPositionCount(), positionsToCopy.size());

        assertBlock(copiedBlock.getDictionary(), new Slice[] {expectedValues[2]});
        assertDictionaryIds(copiedBlock, 0, 0, 0);
    }

    @Test
    public void testCopyPositionsNoCompaction()
            throws Exception
    {
        Slice[] expectedValues = createExpectedValues(1);
        DictionaryBlock dictionaryBlock = createDictionaryBlock(expectedValues, 100);

        List<Integer> positionsToCopy = Ints.asList(0, 2, 4, 5);
        DictionaryBlock copiedBlock = (DictionaryBlock) dictionaryBlock.copyPositions(positionsToCopy);

        assertEquals(copiedBlock.getPositionCount(), positionsToCopy.size());
        assertBlock(copiedBlock.getDictionary(), expectedValues);
    }

    @Test
    public void testCompact()
            throws Exception
    {
        Slice[] expectedValues = createExpectedValues(5);
        DictionaryBlock dictionaryBlock = createDictionaryBlockWithUnreferencedKeys(expectedValues, 10);

        assertEquals(dictionaryBlock.isCompact(), false);
        DictionaryBlock compactBlock = dictionaryBlock.compact();
        assertNotEquals(dictionaryBlock.getDictionarySourceId(), compactBlock.getDictionarySourceId());

        assertEquals(compactBlock.getDictionary().getPositionCount(), (expectedValues.length / 2) + 1);
        assertBlock(compactBlock.getDictionary(), new Slice[] {expectedValues[0], expectedValues[1], expectedValues[3]});
        assertDictionaryIds(compactBlock, 0, 1, 1, 2, 2, 0, 1, 1, 2, 2);
        assertEquals(compactBlock.isCompact(), true);

        DictionaryBlock reCompactedBlock = compactBlock.compact();
        assertEquals(reCompactedBlock.getDictionarySourceId(), compactBlock.getDictionarySourceId());
    }

    @Test
    public void testCompactAllKeysReferenced()
            throws Exception
    {
        Slice[] expectedValues = createExpectedValues(5);
        DictionaryBlock dictionaryBlock = createDictionaryBlock(expectedValues, 10);
        DictionaryBlock compactBlock = dictionaryBlock.compact();

        // When there is nothing to compact, we return the same block
        assertEquals(compactBlock.getDictionary(), dictionaryBlock.getDictionary());
        assertEquals(compactBlock.getPositionCount(), dictionaryBlock.getPositionCount());
        for (int position = 0; position < compactBlock.getPositionCount(); position++) {
            assertEquals(compactBlock.getId(position), dictionaryBlock.getId(position));
        }
        assertEquals(compactBlock.isCompact(), true);
    }

    @Test
    public void testBasicGetPositions()
    {
        Slice[] expectedValues = createExpectedValues(10);
        Block dictionaryBlock = new DictionaryBlock(makeSliceArrayBlock(expectedValues), new int[] {0, 1, 2, 3, 4, 5});
        assertBlock(dictionaryBlock, new Slice[] {expectedValues[0], expectedValues[1], expectedValues[2], expectedValues[3], expectedValues[4], expectedValues[5]});
        DictionaryId dictionaryId = ((DictionaryBlock) dictionaryBlock).getDictionarySourceId();

        // first getPositions
        dictionaryBlock = dictionaryBlock.getPositions(new int[] {1, 2, 4, 5});
        assertBlock(dictionaryBlock, new Slice[] {expectedValues[1], expectedValues[2], expectedValues[4], expectedValues[5]});
        assertEquals(((DictionaryBlock) dictionaryBlock).getDictionarySourceId(), dictionaryId);

        // second getPositions
        dictionaryBlock = dictionaryBlock.getPositions(new int[] {0, 1, 3});
        assertBlock(dictionaryBlock, new Slice[] {expectedValues[1], expectedValues[2], expectedValues[5]});
        assertEquals(((DictionaryBlock) dictionaryBlock).getDictionarySourceId(), dictionaryId);

        // mixed getPositions
        dictionaryBlock = dictionaryBlock.getPositions(new int[] {0, 2, 2});
        assertBlock(dictionaryBlock, new Slice[] {expectedValues[1], expectedValues[5], expectedValues[5]});
        assertEquals(((DictionaryBlock) dictionaryBlock).getDictionarySourceId(), dictionaryId);

        // duplicated getPositions
        dictionaryBlock = dictionaryBlock.getPositions(new int[] {1, 1, 1, 1, 1});
        assertBlock(dictionaryBlock, new Slice[] {expectedValues[5], expectedValues[5], expectedValues[5], expectedValues[5], expectedValues[5]});
        assertEquals(((DictionaryBlock) dictionaryBlock).getDictionarySourceId(), dictionaryId);

        // out of range
        for (int position : ImmutableList.of(-1, 6)) {
            try {
                dictionaryBlock.getPositions(new int[] {position});
                fail("Expected to fail");
            }
            catch (IllegalArgumentException e) {
                assertTrue(e.getMessage().startsWith("Invalid position"));
            }
        }
    }

    @Test
    public void testCompactGetPositions()
    {
        DictionaryBlock block = new DictionaryBlock(makeSliceArrayBlock(createExpectedValues(10)), new int[] {0, 1, 2, 3, 4, 5}).compact();

        // 3, 3, 4, 5, 2, 0, 1, 1
        block = (DictionaryBlock) block.getPositions(new int[] {3, 3, 4, 5, 2, 0, 1, 1});
        assertTrue(block.isCompact());

        // 3, 3, 4, 5, 2, 0, 1, 1, 0, 2, 5, 4, 3
        block = (DictionaryBlock) block.getPositions(new int[] {0, 1, 2, 3, 4, 5, 6, 6, 5, 4, 3, 2, 1});
        assertTrue(block.isCompact());

        // 3, 4, 3, 4, 3
        block = (DictionaryBlock) block.getPositions(new int[] {0, 2, 0, 2, 0});
        assertFalse(block.isCompact());

        block = block.compact();
        // 3, 4, 4, 4
        block = (DictionaryBlock) block.getPositions(new int[] {0, 1, 1, 1});
        assertTrue(block.isCompact());

        // 4, 4, 4, 4
        block = (DictionaryBlock) block.getPositions(new int[] {1, 1, 1, 1});
        assertFalse(block.isCompact());

        block = block.compact();
        // 4
        block = (DictionaryBlock) block.getPositions(new int[] {0});
        assertTrue(block.isCompact());

        // empty
        block = (DictionaryBlock) block.getPositions(new int[] {});
        assertFalse(block.isCompact());

        block = block.compact();
        // empty
        block = (DictionaryBlock) block.getPositions(new int[] {});
        assertTrue(block.isCompact());
    }

    private static DictionaryBlock createDictionaryBlockWithUnreferencedKeys(Slice[] expectedValues, int positionCount)
    {
        // adds references to 0 and all odd indexes
        int dictionarySize = expectedValues.length;
        int[] ids = new int[positionCount];

        for (int i = 0; i < positionCount; i++) {
            int index = i % dictionarySize;
            if (index % 2 == 0 && index != 0) {
                index--;
            }
            ids[i] = index;
        }
        return new DictionaryBlock(new SliceArrayBlock(dictionarySize, expectedValues), ids);
    }

    private static DictionaryBlock createDictionaryBlock(Slice[] expectedValues, int positionCount)
    {
        int dictionarySize = expectedValues.length;
        int[] ids = new int[positionCount];

        for (int i = 0; i < positionCount; i++) {
            ids[i] = i % dictionarySize;
        }
        return new DictionaryBlock(makeSliceArrayBlock(expectedValues), ids);
    }

    private static Block makeSliceArrayBlock(Slice[] values)
    {
        return new SliceArrayBlock(values.length, values);
    }

    private static void assertDictionaryIds(DictionaryBlock dictionaryBlock, int... expected)
    {
        assertEquals(dictionaryBlock.getPositionCount(), expected.length);
        for (int position = 0; position < dictionaryBlock.getPositionCount(); position++) {
            assertEquals(dictionaryBlock.getId(position), expected[position]);
        }
    }
}
