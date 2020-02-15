package com.zhongkexinli.micro.serv.common.pagination;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class SortInfoTest {

	@Test
	public void sortInfoTest() {
		SortInfo sortInfo = new SortInfo();
	}

	@Test
	public void sortInfoColumnNameAndSortTypeTest() {
		SortInfo sortInfo = new SortInfo("column","desc");
		assertEquals("column",sortInfo.getColumnName());
		assertEquals("desc",sortInfo.getSortOrder());
	}

	@Test
	public void setGetColumnNameTest() {
		SortInfo sortInfo = new SortInfo();
		sortInfo.setColumnName("columnName");
		assertEquals("columnName",sortInfo.getColumnName());
	}

	@Test
	public void setGetSortOrderTest() {
		SortInfo sortInfo = new SortInfo();
		sortInfo.setSortOrder("SortOrder");
		assertEquals("SortOrder",sortInfo.getSortOrder());
	}

	@Test
	public void parseSortColumnsTest() {
		 List<SortInfo> sortInfList = SortInfo.parseSortColumns("sortColumn1,sortColumn2,sortColumn3");
		 
		 assertTrue(sortInfList.size()==3);
		 assertEquals("sortColumn1",((SortInfo)sortInfList.get(0)).getColumnName());
		 assertEquals("sortColumn2",((SortInfo)sortInfList.get(1)).getColumnName());
		 assertEquals("sortColumn3",((SortInfo)sortInfList.get(2)).getColumnName());
	}
	
	@Test
	public void parseSortColumnsSortTest() {
		 List<SortInfo> sortInfList = SortInfo.parseSortColumns("sortColumn1 \\s+ desc,sortColumn2 \\s+ asc,sortColumn3");
		 
		 assertTrue(sortInfList.size()==3);
		 assertEquals("sortColumn1",((SortInfo)sortInfList.get(0)).getColumnName());
		 assertEquals("sortColumn2",((SortInfo)sortInfList.get(1)).getColumnName());
		 assertEquals("sortColumn3",((SortInfo)sortInfList.get(2)).getColumnName());

		 assertEquals(null,((SortInfo)sortInfList.get(0)).getSortOrder());
		 assertEquals(null,((SortInfo)sortInfList.get(1)).getSortOrder());
		 assertNull(((SortInfo)sortInfList.get(2)).getSortOrder());
	}
	
	@Test
	public void parseSortColumnsSortTest2() {
		 List<SortInfo> sortInfList = SortInfo.parseSortColumns("sortColumn1");
		 
		 assertTrue(sortInfList.size()==1);
		 assertEquals("sortColumn1",((SortInfo)sortInfList.get(0)).getColumnName());

		 assertEquals(null,((SortInfo)sortInfList.get(0)).getSortOrder());
		 assertNull(((SortInfo)sortInfList.get(0)).getSortOrder());
	}
	
	
	@Test
	public void parseSortColumnsNullTest() {
		 List<SortInfo> sortInfList = SortInfo.parseSortColumns(null);
		 assertTrue(sortInfList.size()==0);
	}

	@Test
	public void toStringTest() {
		SortInfo sortInfo = new SortInfo("column","desc");
		assertEquals("column desc",sortInfo.toString());
		
		SortInfo sortInfoNull = new SortInfo("column",null);
		assertEquals("column",sortInfoNull.toString());
	}

}
