package org.wikidata.wdtk.dumpfiles.wmf;

/*
 * #%L
 * Wikidata Toolkit Dump File Handling
 * %%
 * Copyright (C) 2014 Wikidata Toolkit Developers
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.dumpfiles.DumpContentType;
import org.wikidata.wdtk.testing.MockDirectoryManager;
import org.wikidata.wdtk.testing.MockWebResourceFetcher;
import org.wikidata.wdtk.util.CompressionType;

public class WmfOnlineDailyDumpFileTest {

	MockWebResourceFetcher wrf;
	MockDirectoryManager dm;

	@Before
	public void setUp() throws IOException {
		dm = new MockDirectoryManager(
				Paths.get(System.getProperty("user.dir")), true, false);

		wrf = new MockWebResourceFetcher();
	}

	@Test
	public void validDumpProperties() throws IOException {
		String dateStamp = "20140220";
		wrf.setWebResourceContents(
				"https://dumps.wikimedia.org/other/incr/wikidatawiki/"
						+ dateStamp + "/status.txt", "done");
		wrf.setWebResourceContents(
				"https://dumps.wikimedia.org/other/incr/wikidatawiki/"
						+ dateStamp + "/wikidatawiki-" + dateStamp
						+ "-pages-meta-hist-incr.xml.bz2", "Line1",
				CompressionType.BZ2);
		WmfOnlineDailyDumpFile dump = new WmfOnlineDailyDumpFile(dateStamp,
				"wikidatawiki", wrf, dm);

		BufferedReader br = dump.getDumpFileReader();

		assertEquals(br.readLine(), "Line1");
		assertEquals(br.readLine(), null);
		assertTrue(dump.isAvailable());
		assertTrue(dump.isAvailable()); // second time should use cached entry
		assertEquals(dateStamp, dump.getDateStamp());
		assertEquals("wikidatawiki", dump.getProjectName());
		assertEquals("wikidatawiki-daily-" + dateStamp, dump.toString());
		assertEquals(DumpContentType.DAILY, dump.getDumpContentType());
	}

	@Test
	public void missingDumpProperties() {
		String dateStamp = "20140220";
		WmfOnlineDailyDumpFile dump = new WmfOnlineDailyDumpFile(dateStamp,
				"wikidatawiki", wrf, dm);

		assertFalse(dump.isAvailable());
		assertEquals(dateStamp, dump.getDateStamp());
	}

	@Test
	public void emptyDumpProperties() throws IOException {
		String dateStamp = "20140220";
		wrf.setWebResourceContents(
				"http://dumps.wikimedia.org/other/incr/wikidatawiki/"
						+ dateStamp + "/status.txt", "");
		WmfOnlineDailyDumpFile dump = new WmfOnlineDailyDumpFile(dateStamp,
				"wikidatawiki", wrf, dm);

		assertFalse(dump.isAvailable());
		assertEquals(dateStamp, dump.getDateStamp());
	}

	@Test
	public void inaccessibleStatus() throws IOException {
		String dateStamp = "20140220";
		wrf.setWebResourceContents(
				"http://dumps.wikimedia.org/other/incr/wikidatawiki/"
						+ dateStamp + "/status.txt", "done");
		wrf.setReturnFailingReaders(true);
		WmfOnlineDailyDumpFile dump = new WmfOnlineDailyDumpFile(dateStamp,
				"wikidatawiki", wrf, dm);

		assertFalse(dump.isAvailable());
	}

	@Test(expected = IOException.class)
	public void downloadNoRevisionId() throws IOException {
		String dateStamp = "20140220";
		wrf.setWebResourceContents(
				"http://dumps.wikimedia.org/other/incr/wikidatawiki/"
						+ dateStamp + "/wikidatawiki-" + dateStamp
						+ "-pages-meta-hist-incr.xml.bz2", "Line1",
				CompressionType.BZ2);
		WmfOnlineDailyDumpFile dump = new WmfOnlineDailyDumpFile(dateStamp,
				"wikidatawiki", wrf, dm);
		dump.getDumpFileReader();
	}

	@Test(expected = IOException.class)
	public void downloadNoDumpFile() throws IOException {
		String dateStamp = "20140220";
		wrf.setWebResourceContents(
				"http://dumps.wikimedia.org/other/incr/wikidatawiki/"
						+ dateStamp + "/status.txt", "done");
		WmfOnlineDailyDumpFile dump = new WmfOnlineDailyDumpFile(dateStamp,
				"wikidatawiki", wrf, dm);
		dump.getDumpFileReader();
	}

}
