package nl.topicus.jdbc.resultset;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.mockito.internal.stubbing.answers.Returns;

import com.google.cloud.ByteArray;
import com.google.cloud.Date;
import com.google.cloud.Timestamp;
import com.google.cloud.spanner.ResultSet;
import com.google.cloud.spanner.Type;
import com.google.cloud.spanner.Type.StructField;

import nl.topicus.jdbc.CloudSpannerArray;
import nl.topicus.jdbc.CloudSpannerDataType;
import nl.topicus.jdbc.exception.CloudSpannerSQLException;
import nl.topicus.jdbc.statement.CloudSpannerStatement;
import nl.topicus.jdbc.test.category.UnitTest;

@Category(UnitTest.class)
public class CloudSpannerResultSetTest
{
	static final String UNKNOWN_COLUMN = "UNKNOWN_COLUMN";

	static final String STRING_COL_NULL = "STRING_COL_NULL";

	static final String STRING_COL_NOT_NULL = "STRING_COL_NOT_NULL";

	static final int STRING_COLINDEX_NULL = 1;

	static final int STRING_COLINDEX_NOTNULL = 2;

	static final String BOOLEAN_COL_NULL = "BOOLEAN_COL_NULL";

	static final String BOOLEAN_COL_NOT_NULL = "BOOLEAN_COL_NOT_NULL";

	static final int BOOLEAN_COLINDEX_NULL = 3;

	static final int BOOLEAN_COLINDEX_NOTNULL = 4;

	static final String DOUBLE_COL_NULL = "DOUBLE_COL_NULL";

	static final String DOUBLE_COL_NOT_NULL = "DOUBLE_COL_NOT_NULL";

	static final int DOUBLE_COLINDEX_NULL = 5;

	static final int DOUBLE_COLINDEX_NOTNULL = 6;

	static final String BYTES_COL_NULL = "BYTES_COL_NULL";

	static final String BYTES_COL_NOT_NULL = "BYTES_COL_NOT_NULL";

	static final int BYTES_COLINDEX_NULL = 7;

	static final int BYTES_COLINDEX_NOTNULL = 8;

	static final String LONG_COL_NULL = "LONG_COL_NULL";

	static final String LONG_COL_NOT_NULL = "LONG_COL_NOT_NULL";

	static final int LONG_COLINDEX_NULL = 9;

	static final int LONG_COLINDEX_NOTNULL = 10;

	static final String DATE_COL_NULL = "DATE_COL_NULL";

	static final String DATE_COL_NOT_NULL = "DATE_COL_NOT_NULL";

	static final int DATE_COLINDEX_NULL = 11;

	static final int DATE_COLINDEX_NOTNULL = 12;

	static final String TIMESTAMP_COL_NULL = "TIMESTAMP_COL_NULL";

	static final String TIMESTAMP_COL_NOT_NULL = "TIMESTAMP_COL_NOT_NULL";

	static final int TIMESTAMP_COLINDEX_NULL = 13;

	static final int TIMESTAMP_COLINDEX_NOTNULL = 14;

	static final String TIME_COL_NULL = "TIME_COL_NULL";

	static final String TIME_COL_NOT_NULL = "TIME_COL_NOT_NULL";

	static final int TIME_COLINDEX_NULL = 15;

	static final int TIME_COLINDEX_NOTNULL = 16;

	static final String ARRAY_COL_NULL = "ARRAY_COL_NULL";

	static final String ARRAY_COL_NOT_NULL = "ARRAY_COL_NOT_NULL";

	static final int ARRAY_COLINDEX_NULL = 17;

	static final int ARRAY_COLINDEX_NOTNULL = 18;

	static final String URL_COL_NULL = "URL_COL_NULL";

	static final String URL_COL_NOT_NULL = "URL_COL_NOT_NULL";

	static final int URL_COLINDEX_NULL = 19;

	static final int URL_COLINDEX_NOTNULL = 20;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private CloudSpannerResultSet subject;

	static ResultSet getMockResultSet()
	{
		ResultSet res = mock(ResultSet.class);
		when(res.getString(STRING_COL_NULL)).thenReturn(null);
		when(res.isNull(STRING_COL_NULL)).thenReturn(true);
		when(res.getString(STRING_COL_NOT_NULL)).thenReturn("FOO");
		when(res.isNull(STRING_COL_NOT_NULL)).thenReturn(false);
		when(res.getString(STRING_COLINDEX_NULL - 1)).thenReturn(null);
		when(res.isNull(STRING_COLINDEX_NULL - 1)).thenReturn(true);
		when(res.getString(STRING_COLINDEX_NOTNULL - 1)).thenReturn("BAR");
		when(res.isNull(STRING_COLINDEX_NOTNULL - 1)).thenReturn(false);
		when(res.getColumnType(STRING_COL_NULL)).thenReturn(Type.string());
		when(res.getColumnType(STRING_COL_NOT_NULL)).thenReturn(Type.string());
		when(res.getColumnType(STRING_COLINDEX_NULL - 1)).thenReturn(Type.string());
		when(res.getColumnType(STRING_COLINDEX_NOTNULL - 1)).thenReturn(Type.string());

		when(res.getString(URL_COL_NULL)).thenReturn(null);
		when(res.isNull(URL_COL_NULL)).thenReturn(true);
		when(res.getString(URL_COL_NOT_NULL)).thenReturn("https://github.com/olavloite/spanner-jdbc");
		when(res.isNull(URL_COL_NOT_NULL)).thenReturn(false);
		when(res.getString(URL_COLINDEX_NULL - 1)).thenReturn(null);
		when(res.isNull(URL_COLINDEX_NULL - 1)).thenReturn(true);
		when(res.getString(URL_COLINDEX_NOTNULL - 1)).thenReturn("https://github.com/olavloite");
		when(res.isNull(URL_COLINDEX_NOTNULL - 1)).thenReturn(false);
		when(res.getColumnType(URL_COL_NULL)).thenReturn(Type.string());
		when(res.getColumnType(URL_COL_NOT_NULL)).thenReturn(Type.string());
		when(res.getColumnType(URL_COLINDEX_NULL - 1)).thenReturn(Type.string());
		when(res.getColumnType(URL_COLINDEX_NOTNULL - 1)).thenReturn(Type.string());

		when(res.getBoolean(BOOLEAN_COL_NULL)).thenReturn(false);
		when(res.isNull(BOOLEAN_COL_NULL)).thenReturn(true);
		when(res.getBoolean(BOOLEAN_COL_NOT_NULL)).thenReturn(true);
		when(res.isNull(BOOLEAN_COL_NOT_NULL)).thenReturn(false);
		when(res.getBoolean(BOOLEAN_COLINDEX_NULL - 1)).thenReturn(false);
		when(res.isNull(BOOLEAN_COLINDEX_NULL - 1)).thenReturn(true);
		when(res.getBoolean(BOOLEAN_COLINDEX_NOTNULL - 1)).thenReturn(false);
		when(res.isNull(BOOLEAN_COLINDEX_NOTNULL - 1)).thenReturn(false);
		when(res.getColumnType(BOOLEAN_COL_NULL)).thenReturn(Type.bool());
		when(res.getColumnType(BOOLEAN_COL_NOT_NULL)).thenReturn(Type.bool());
		when(res.getColumnType(BOOLEAN_COLINDEX_NULL - 1)).thenReturn(Type.bool());
		when(res.getColumnType(BOOLEAN_COLINDEX_NOTNULL - 1)).thenReturn(Type.bool());

		when(res.getDouble(DOUBLE_COL_NULL)).thenReturn(0d);
		when(res.isNull(DOUBLE_COL_NULL)).thenReturn(true);
		when(res.getDouble(DOUBLE_COL_NOT_NULL)).thenReturn(1.123456789d);
		when(res.isNull(DOUBLE_COL_NOT_NULL)).thenReturn(false);
		when(res.getDouble(DOUBLE_COLINDEX_NULL - 1)).thenReturn(0d);
		when(res.isNull(DOUBLE_COLINDEX_NULL - 1)).thenReturn(true);
		when(res.getDouble(DOUBLE_COLINDEX_NOTNULL - 1)).thenReturn(2.123456789d);
		when(res.isNull(DOUBLE_COLINDEX_NOTNULL - 1)).thenReturn(false);
		when(res.getColumnType(DOUBLE_COL_NULL)).thenReturn(Type.float64());
		when(res.getColumnType(DOUBLE_COL_NOT_NULL)).thenReturn(Type.float64());
		when(res.getColumnType(DOUBLE_COLINDEX_NULL - 1)).thenReturn(Type.float64());
		when(res.getColumnType(DOUBLE_COLINDEX_NOTNULL - 1)).thenReturn(Type.float64());

		when(res.getString(BYTES_COL_NULL)).thenReturn(null);
		when(res.isNull(BYTES_COL_NULL)).thenReturn(true);
		when(res.getBytes(BYTES_COL_NOT_NULL)).thenReturn(ByteArray.copyFrom("FOO"));
		when(res.isNull(BYTES_COL_NOT_NULL)).thenReturn(false);
		when(res.getBytes(BYTES_COLINDEX_NULL - 1)).thenReturn(null);
		when(res.isNull(BYTES_COLINDEX_NULL - 1)).thenReturn(true);
		when(res.getBytes(BYTES_COLINDEX_NOTNULL - 1)).thenReturn(ByteArray.copyFrom("BAR"));
		when(res.isNull(BYTES_COLINDEX_NOTNULL - 1)).thenReturn(false);
		when(res.getColumnType(BYTES_COL_NULL)).thenReturn(Type.bytes());
		when(res.getColumnType(BYTES_COL_NOT_NULL)).thenReturn(Type.bytes());
		when(res.getColumnType(BYTES_COLINDEX_NULL - 1)).thenReturn(Type.bytes());
		when(res.getColumnType(BYTES_COLINDEX_NOTNULL - 1)).thenReturn(Type.bytes());

		when(res.getLong(LONG_COL_NULL)).thenReturn(0l);
		when(res.isNull(LONG_COL_NULL)).thenReturn(true);
		when(res.getLong(LONG_COL_NOT_NULL)).thenReturn(1l);
		when(res.isNull(LONG_COL_NOT_NULL)).thenReturn(false);
		when(res.getLong(LONG_COLINDEX_NULL - 1)).thenReturn(0l);
		when(res.isNull(LONG_COLINDEX_NULL - 1)).thenReturn(true);
		when(res.getLong(LONG_COLINDEX_NOTNULL - 1)).thenReturn(2l);
		when(res.isNull(LONG_COLINDEX_NOTNULL - 1)).thenReturn(false);
		when(res.getColumnType(LONG_COL_NULL)).thenReturn(Type.int64());
		when(res.getColumnType(LONG_COL_NOT_NULL)).thenReturn(Type.int64());
		when(res.getColumnType(LONG_COLINDEX_NULL - 1)).thenReturn(Type.int64());
		when(res.getColumnType(LONG_COLINDEX_NOTNULL - 1)).thenReturn(Type.int64());

		when(res.getDate(DATE_COL_NULL)).thenAnswer(new Returns(null));
		when(res.isNull(DATE_COL_NULL)).thenAnswer(new Returns(true));
		when(res.getDate(DATE_COL_NOT_NULL)).thenAnswer(new Returns(Date.fromYearMonthDay(2017, 9, 10)));
		when(res.isNull(DATE_COL_NOT_NULL)).thenAnswer(new Returns(false));
		when(res.getDate(DATE_COLINDEX_NULL - 1)).thenAnswer(new Returns(null));
		when(res.isNull(DATE_COLINDEX_NULL - 1)).thenAnswer(new Returns(true));
		when(res.getDate(DATE_COLINDEX_NOTNULL - 1)).thenAnswer(new Returns(Date.fromYearMonthDay(2017, 9, 10)));
		when(res.isNull(DATE_COLINDEX_NOTNULL - 1)).thenAnswer(new Returns(false));
		when(res.getColumnType(DATE_COL_NULL)).thenAnswer(new Returns(Type.date()));
		when(res.getColumnType(DATE_COL_NOT_NULL)).thenAnswer(new Returns(Type.date()));
		when(res.getColumnType(DATE_COLINDEX_NULL - 1)).thenAnswer(new Returns(Type.date()));
		when(res.getColumnType(DATE_COLINDEX_NOTNULL - 1)).thenAnswer(new Returns(Type.date()));

		Calendar cal1 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal1.clear();
		cal1.set(2017, 8, 10, 8, 15, 59);
		Calendar cal2 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal2.clear();
		cal2.set(2017, 8, 11, 8, 15, 59);
		when(res.getTimestamp(TIMESTAMP_COL_NULL)).thenReturn(null);
		when(res.isNull(TIMESTAMP_COL_NULL)).thenReturn(true);
		when(res.getTimestamp(TIMESTAMP_COL_NOT_NULL)).thenReturn(Timestamp.of(cal1.getTime()));
		when(res.isNull(TIMESTAMP_COL_NOT_NULL)).thenReturn(false);
		when(res.getTimestamp(TIMESTAMP_COLINDEX_NULL - 1)).thenReturn(null);
		when(res.isNull(TIMESTAMP_COLINDEX_NULL - 1)).thenReturn(true);
		when(res.getTimestamp(TIMESTAMP_COLINDEX_NOTNULL - 1)).thenReturn(Timestamp.of(cal2.getTime()));
		when(res.isNull(TIMESTAMP_COLINDEX_NOTNULL - 1)).thenReturn(false);
		when(res.getColumnType(TIMESTAMP_COL_NULL)).thenReturn(Type.timestamp());
		when(res.getColumnType(TIMESTAMP_COL_NOT_NULL)).thenReturn(Type.timestamp());
		when(res.getColumnType(TIMESTAMP_COLINDEX_NULL - 1)).thenReturn(Type.timestamp());
		when(res.getColumnType(TIMESTAMP_COLINDEX_NOTNULL - 1)).thenReturn(Type.timestamp());

		Calendar cal3 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal3.clear();
		cal3.set(1970, 0, 1, 14, 6, 15);
		Calendar cal4 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal4.clear();
		cal4.set(1970, 0, 1, 14, 6, 15);
		when(res.getTimestamp(TIME_COL_NULL)).thenReturn(null);
		when(res.isNull(TIME_COL_NULL)).thenReturn(true);
		when(res.getTimestamp(TIME_COL_NOT_NULL)).thenReturn(Timestamp.of(cal3.getTime()));
		when(res.isNull(TIME_COL_NOT_NULL)).thenReturn(false);
		when(res.getTimestamp(TIME_COLINDEX_NULL - 1)).thenReturn(null);
		when(res.isNull(TIME_COLINDEX_NULL - 1)).thenReturn(true);
		when(res.getTimestamp(TIME_COLINDEX_NOTNULL - 1)).thenReturn(Timestamp.of(cal4.getTime()));
		when(res.isNull(TIME_COLINDEX_NOTNULL - 1)).thenReturn(false);
		when(res.getColumnType(TIME_COL_NULL)).thenReturn(Type.timestamp());
		when(res.getColumnType(TIME_COL_NOT_NULL)).thenReturn(Type.timestamp());
		when(res.getColumnType(TIME_COLINDEX_NULL - 1)).thenReturn(Type.timestamp());
		when(res.getColumnType(TIME_COLINDEX_NOTNULL - 1)).thenReturn(Type.timestamp());

		when(res.getLongList(ARRAY_COL_NULL)).thenAnswer(new Returns(null));
		when(res.isNull(ARRAY_COL_NULL)).thenAnswer(new Returns(true));
		when(res.getLongList(ARRAY_COL_NOT_NULL)).thenAnswer(new Returns(Arrays.asList(1L, 2L, 3L)));
		when(res.isNull(ARRAY_COL_NOT_NULL)).thenAnswer(new Returns(false));
		when(res.getLongList(ARRAY_COLINDEX_NULL - 1)).thenAnswer(new Returns(null));
		when(res.isNull(ARRAY_COLINDEX_NULL - 1)).thenAnswer(new Returns(true));
		when(res.getLongList(ARRAY_COLINDEX_NOTNULL - 1)).thenAnswer(new Returns(Arrays.asList(1L, 2L, 3L)));
		when(res.isNull(ARRAY_COLINDEX_NOTNULL - 1)).thenAnswer(new Returns(false));
		when(res.getColumnType(ARRAY_COL_NULL)).thenAnswer(new Returns(Type.array(Type.int64())));
		when(res.getColumnType(ARRAY_COL_NOT_NULL)).thenAnswer(new Returns(Type.array(Type.int64())));
		when(res.getColumnType(ARRAY_COLINDEX_NULL - 1)).thenAnswer(new Returns(Type.array(Type.int64())));
		when(res.getColumnType(ARRAY_COLINDEX_NOTNULL - 1)).thenAnswer(new Returns(Type.array(Type.int64())));

		when(res.getColumnIndex(STRING_COL_NOT_NULL)).thenAnswer(new Returns(1));
		when(res.getColumnIndex(UNKNOWN_COLUMN)).thenThrow(IllegalArgumentException.class);
		when(res.getColumnIndex(DATE_COL_NOT_NULL)).thenAnswer(new Returns(DATE_COLINDEX_NOTNULL - 1));
		when(res.getColumnIndex(ARRAY_COL_NOT_NULL)).thenAnswer(new Returns(ARRAY_COLINDEX_NOTNULL - 1));
		when(res.getColumnIndex(ARRAY_COL_NULL)).thenAnswer(new Returns(ARRAY_COLINDEX_NULL - 1));

		when(res.getType()).thenReturn(Type.struct(StructField.of(STRING_COL_NULL, Type.string()),
				StructField.of(STRING_COL_NOT_NULL, Type.string()), StructField.of(BOOLEAN_COL_NULL, Type.bool()),
				StructField.of(BOOLEAN_COL_NOT_NULL, Type.bool()), StructField.of(DOUBLE_COL_NULL, Type.float64()),
				StructField.of(DOUBLE_COL_NOT_NULL, Type.float64()), StructField.of(BYTES_COL_NULL, Type.bytes()),
				StructField.of(BYTES_COL_NOT_NULL, Type.bytes()), StructField.of(LONG_COL_NULL, Type.int64()),
				StructField.of(LONG_COL_NOT_NULL, Type.int64()), StructField.of(DATE_COL_NULL, Type.date()),
				StructField.of(DATE_COL_NOT_NULL, Type.date()), StructField.of(TIMESTAMP_COL_NULL, Type.timestamp()),
				StructField.of(TIMESTAMP_COL_NOT_NULL, Type.timestamp()),
				StructField.of(TIME_COL_NULL, Type.timestamp()), StructField.of(TIME_COL_NOT_NULL, Type.timestamp()),
				StructField.of(URL_COL_NULL, Type.string()), StructField.of(URL_COL_NOT_NULL, Type.string())

		));

		// Next behaviour.
		when(res.next()).thenReturn(true, true, true, true, false);

		return res;
	}

	public CloudSpannerResultSetTest() throws SQLException
	{
		subject = new CloudSpannerResultSet(mock(CloudSpannerStatement.class), getMockResultSet(), "SELECT * FROM FOO");
		subject.next();
	}

	@Test
	public void testWasNull() throws SQLException
	{
		String value = subject.getString(STRING_COL_NULL);
		boolean wasNull = subject.wasNull();
		assertTrue(wasNull);
		assertNull(value);
		String valueNotNull = subject.getString(STRING_COL_NOT_NULL);
		boolean wasNotNull = subject.wasNull();
		assertEquals(false, wasNotNull);
		assertNotNull(valueNotNull);
	}

	@Test
	public void testNext() throws SQLException
	{
		try (CloudSpannerResultSet rs = new CloudSpannerResultSet(mock(CloudSpannerStatement.class), getMockResultSet(),
				"SELECT * FROM FOO"))
		{
			assertTrue(rs.isBeforeFirst());
			assertEquals(false, rs.isAfterLast());
			int num = 0;
			while (rs.next())
			{
				num++;
			}
			assertTrue(num > 0);
			assertEquals(false, rs.isBeforeFirst());
			assertTrue(rs.isAfterLast());
		}
	}

	@Test
	public void testClose() throws SQLException
	{
		try (CloudSpannerResultSet rs = new CloudSpannerResultSet(mock(CloudSpannerStatement.class), getMockResultSet(),
				"SELECT * FROM FOO"))
		{
			assertEquals(false, rs.isClosed());
			rs.next();
			assertNotNull(rs.getString(STRING_COL_NOT_NULL));
			rs.close();
			assertTrue(rs.isClosed());
			boolean failed = false;
			try
			{
				// Should fail
				rs.getString(STRING_COL_NOT_NULL);
			}
			catch (SQLException e)
			{
				failed = true;
			}
			assertTrue(failed);
		}
	}

	@Test
	public void testGetStringIndex() throws SQLException
	{
		assertNotNull(subject.getString(STRING_COLINDEX_NOTNULL));
		assertEquals("BAR", subject.getString(STRING_COLINDEX_NOTNULL));
		assertEquals(false, subject.wasNull());
		assertNull(subject.getString(STRING_COLINDEX_NULL));
		assertTrue(subject.wasNull());
	}

	@Test
	public void testGetNStringIndex() throws SQLException
	{
		assertNotNull(subject.getNString(STRING_COLINDEX_NOTNULL));
		assertEquals("BAR", subject.getNString(STRING_COLINDEX_NOTNULL));
		assertEquals(false, subject.wasNull());
		assertNull(subject.getNString(STRING_COLINDEX_NULL));
		assertTrue(subject.wasNull());
	}

	@Test
	public void testGetURLIndex() throws SQLException, MalformedURLException
	{
		assertNotNull(subject.getURL(URL_COLINDEX_NOTNULL));
		assertEquals(new URL("https://github.com/olavloite"), subject.getURL(URL_COLINDEX_NOTNULL));
		assertEquals(false, subject.wasNull());
		assertNull(subject.getURL(URL_COLINDEX_NULL));
		assertTrue(subject.wasNull());
	}

	@Test
	public void testGetURLIndexInvalid() throws SQLException, MalformedURLException
	{
		thrown.expect(CloudSpannerSQLException.class);
		thrown.expectMessage("Invalid URL");
		assertNotNull(subject.getURL(STRING_COLINDEX_NOTNULL));
	}

	@Test
	public void testGetBooleanIndex() throws SQLException
	{
		assertNotNull(subject.getBoolean(BOOLEAN_COLINDEX_NOTNULL));
		assertEquals(false, subject.wasNull());
		assertEquals(false, subject.getBoolean(BOOLEAN_COLINDEX_NULL));
		assertTrue(subject.wasNull());
	}

	@Test
	public void testGetLongIndex() throws SQLException
	{
		assertNotNull(subject.getLong(LONG_COLINDEX_NOTNULL));
		assertEquals(2l, subject.getLong(LONG_COLINDEX_NOTNULL));
		assertEquals(false, subject.wasNull());
		assertEquals(0l, subject.getLong(LONG_COLINDEX_NULL));
		assertTrue(subject.wasNull());
	}

	@Test
	public void testGetDoubleIndex() throws SQLException
	{
		assertNotNull(subject.getDouble(DOUBLE_COLINDEX_NOTNULL));
		assertEquals(2.123456789d, subject.getDouble(DOUBLE_COLINDEX_NOTNULL), 0d);
		assertEquals(false, subject.wasNull());
		assertEquals(0d, subject.getDouble(DOUBLE_COLINDEX_NULL), 0d);
		assertTrue(subject.wasNull());
	}

	@Test
	public void testGetBigDecimalIndexAndScale() throws SQLException
	{
		assertNotNull(subject.getBigDecimal(DOUBLE_COLINDEX_NOTNULL, 2));
		assertEquals(BigDecimal.valueOf(2.12d), subject.getBigDecimal(DOUBLE_COLINDEX_NOTNULL, 2));
		assertEquals(false, subject.wasNull());
		assertNull(subject.getBigDecimal(DOUBLE_COLINDEX_NULL, 2));
		assertTrue(subject.wasNull());
	}

	@Test
	public void testGetBytesIndex() throws SQLException
	{
		assertNotNull(subject.getBytes(BYTES_COLINDEX_NOTNULL));
		assertArrayEquals(ByteArray.copyFrom("BAR").toByteArray(), subject.getBytes(BYTES_COLINDEX_NOTNULL));
		assertEquals(false, subject.wasNull());
		assertNull(subject.getBytes(BYTES_COLINDEX_NULL));
		assertTrue(subject.wasNull());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testGetDateIndex() throws SQLException
	{
		assertNotNull(subject.getDate(DATE_COLINDEX_NOTNULL));
		assertEquals(new java.sql.Date(2017 - 1900, 8, 10), subject.getDate(DATE_COLINDEX_NOTNULL));
		assertEquals(false, subject.wasNull());
		assertNull(subject.getDate(DATE_COLINDEX_NULL));
		assertTrue(subject.wasNull());
	}

	@Test
	public void testGetTimeIndex() throws SQLException
	{
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.clear();
		cal.set(1970, 0, 1, 14, 6, 15);

		assertNotNull(subject.getTime(TIME_COLINDEX_NOTNULL));
		assertEquals(new Time(cal.getTimeInMillis()), subject.getTime(TIME_COLINDEX_NOTNULL));
		assertEquals(false, subject.wasNull());
		assertNull(subject.getTime(TIME_COLINDEX_NULL));
		assertTrue(subject.wasNull());
	}

	@Test
	public void testGetTimestampIndex() throws SQLException
	{
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.clear();
		cal.set(2017, 8, 11, 8, 15, 59);

		assertNotNull(subject.getTimestamp(TIMESTAMP_COLINDEX_NOTNULL));
		assertEquals(new java.sql.Timestamp(cal.getTimeInMillis()), subject.getTimestamp(TIMESTAMP_COLINDEX_NOTNULL));
		assertEquals(false, subject.wasNull());
		assertNull(subject.getTimestamp(TIMESTAMP_COLINDEX_NULL));
		assertTrue(subject.wasNull());
	}

	@Test
	public void testGetStringLabel() throws SQLException
	{
		assertNotNull(subject.getString(STRING_COL_NOT_NULL));
		assertEquals("FOO", subject.getString(STRING_COL_NOT_NULL));
		assertEquals(false, subject.wasNull());
		assertNull(subject.getString(STRING_COL_NULL));
		assertTrue(subject.wasNull());
	}

	@Test
	public void testGetNStringLabel() throws SQLException
	{
		assertNotNull(subject.getNString(STRING_COL_NOT_NULL));
		assertEquals("FOO", subject.getNString(STRING_COL_NOT_NULL));
		assertEquals(false, subject.wasNull());
		assertNull(subject.getNString(STRING_COL_NULL));
		assertTrue(subject.wasNull());
	}

	@Test
	public void testGetURLLabel() throws SQLException
	{
		assertNotNull(subject.getString(URL_COL_NOT_NULL));
		assertEquals("https://github.com/olavloite/spanner-jdbc", subject.getString(URL_COL_NOT_NULL));
		assertEquals(false, subject.wasNull());
		assertNull(subject.getString(URL_COL_NULL));
		assertTrue(subject.wasNull());
	}

	@Test
	public void testGetURLLabelInvalid() throws SQLException
	{
		thrown.expect(CloudSpannerSQLException.class);
		thrown.expectMessage("Invalid URL");
		assertNotNull(subject.getURL(STRING_COL_NOT_NULL));
	}

	@Test
	public void testGetBooleanLabel() throws SQLException
	{
		assertNotNull(subject.getBoolean(BOOLEAN_COL_NOT_NULL));
		assertEquals(false, subject.wasNull());
		assertEquals(false, subject.getBoolean(BOOLEAN_COL_NULL));
		assertTrue(subject.wasNull());
	}

	@Test
	public void testGetLongLabel() throws SQLException
	{
		assertNotNull(subject.getLong(LONG_COL_NOT_NULL));
		assertEquals(1l, subject.getLong(LONG_COL_NOT_NULL));
		assertEquals(false, subject.wasNull());
		assertEquals(0l, subject.getLong(LONG_COL_NULL));
		assertTrue(subject.wasNull());
	}

	@Test
	public void testGetDoubleLabel() throws SQLException
	{
		assertNotNull(subject.getDouble(DOUBLE_COL_NOT_NULL));
		assertEquals(1.123456789d, subject.getDouble(DOUBLE_COL_NOT_NULL), 0d);
		assertEquals(false, subject.wasNull());
		assertEquals(0d, subject.getDouble(DOUBLE_COL_NULL), 0d);
		assertTrue(subject.wasNull());
	}

	@Test
	public void testGetBigDecimalLabelAndScale() throws SQLException
	{
		assertNotNull(subject.getBigDecimal(DOUBLE_COL_NOT_NULL, 2));
		assertEquals(BigDecimal.valueOf(1.12d), subject.getBigDecimal(DOUBLE_COL_NOT_NULL, 2));
		assertEquals(false, subject.wasNull());
		assertNull(subject.getBigDecimal(DOUBLE_COL_NULL, 2));
		assertTrue(subject.wasNull());
	}

	@Test
	public void testGetBytesLabel() throws SQLException
	{
		assertNotNull(subject.getBytes(BYTES_COL_NOT_NULL));
		assertArrayEquals(ByteArray.copyFrom("FOO").toByteArray(), subject.getBytes(BYTES_COL_NOT_NULL));
		assertEquals(false, subject.wasNull());
		assertNull(subject.getBytes(BYTES_COL_NULL));
		assertTrue(subject.wasNull());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testGetDateLabel() throws SQLException
	{
		assertNotNull(subject.getDate(DATE_COL_NOT_NULL));
		assertEquals(new java.sql.Date(2017 - 1900, 8, 10), subject.getDate(DATE_COL_NOT_NULL));
		assertEquals(false, subject.wasNull());
		assertNull(subject.getDate(DATE_COL_NULL));
		assertTrue(subject.wasNull());
	}

	@Test
	public void testGetTimeLabel() throws SQLException
	{
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.clear();
		cal.set(1970, 0, 1, 14, 6, 15);

		assertNotNull(subject.getTime(TIME_COL_NOT_NULL));
		assertEquals(new Time(cal.getTimeInMillis()), subject.getTime(TIME_COL_NOT_NULL));
		assertEquals(false, subject.wasNull());
		assertNull(subject.getTime(TIME_COL_NULL));
		assertTrue(subject.wasNull());
	}

	@Test
	public void testGetTimestampLabel() throws SQLException
	{
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.clear();
		cal.set(2017, 8, 10, 8, 15, 59);

		assertNotNull(subject.getTime(TIMESTAMP_COL_NOT_NULL));
		assertEquals(new java.sql.Timestamp(cal.getTimeInMillis()), subject.getTimestamp(TIMESTAMP_COL_NOT_NULL));
		assertEquals(false, subject.wasNull());
		assertNull(subject.getTimestamp(TIMESTAMP_COL_NULL));
		assertTrue(subject.wasNull());
	}

	@Test
	public void testGetMetaData() throws SQLException
	{
		CloudSpannerResultSetMetaData metadata = subject.getMetaData();
		assertNotNull(metadata);
	}

	@Test
	public void testFindColumn() throws SQLException
	{
		assertEquals(2, subject.findColumn(STRING_COL_NOT_NULL));
	}

	@Test
	public void testGetBigDecimalIndex() throws SQLException
	{
		assertNotNull(subject.getBigDecimal(DOUBLE_COLINDEX_NOTNULL));
		assertEquals(BigDecimal.valueOf(2.123456789d), subject.getBigDecimal(DOUBLE_COLINDEX_NOTNULL));
		assertEquals(false, subject.wasNull());
		assertNull(subject.getBigDecimal(DOUBLE_COLINDEX_NULL));
		assertTrue(subject.wasNull());
	}

	@Test
	public void testGetBigDecimalLabel() throws SQLException
	{
		assertNotNull(subject.getBigDecimal(DOUBLE_COL_NOT_NULL));
		assertEquals(BigDecimal.valueOf(1.123456789d), subject.getBigDecimal(DOUBLE_COL_NOT_NULL));
		assertEquals(false, subject.wasNull());
		assertNull(subject.getBigDecimal(DOUBLE_COL_NULL));
		assertTrue(subject.wasNull());
	}

	@Test
	public void testGetStatement() throws SQLException
	{
		assertNotNull(subject.getStatement());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testGetDateIndexCalendar() throws SQLException
	{
		Calendar cal = Calendar.getInstance();
		assertNotNull(subject.getDate(DATE_COLINDEX_NOTNULL, cal));
		assertEquals(new java.sql.Date(2017 - 1900, 8, 10), subject.getDate(DATE_COLINDEX_NOTNULL, cal));
		assertEquals(false, subject.wasNull());
		assertNull(subject.getDate(DATE_COLINDEX_NULL, cal));
		assertTrue(subject.wasNull());

		Calendar calGMT = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		Calendar expected = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		expected.set(2017, 8, 10, 0, 0, 0);
		expected.clear(Calendar.MILLISECOND);
		assertEquals(new java.sql.Date(expected.getTimeInMillis()), subject.getDate(DATE_COLINDEX_NOTNULL, calGMT));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testGetDateLabelCalendar() throws SQLException
	{
		Calendar cal = Calendar.getInstance();
		assertNotNull(subject.getDate(DATE_COL_NOT_NULL, cal));
		assertEquals(new java.sql.Date(2017 - 1900, 8, 10), subject.getDate(DATE_COL_NOT_NULL, cal));
		assertEquals(false, subject.wasNull());
		assertNull(subject.getDate(DATE_COL_NULL, cal));
		assertTrue(subject.wasNull());

		Calendar calGMT = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		Calendar expected = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		expected.set(2017, 8, 10, 0, 0, 0);
		expected.clear(Calendar.MILLISECOND);
		assertEquals(new java.sql.Date(expected.getTimeInMillis()), subject.getDate(DATE_COL_NOT_NULL, calGMT));
	}

	@Test
	public void testGetTimeIndexCalendar() throws SQLException
	{
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		Calendar expected = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		expected.clear();
		expected.set(1970, 0, 1, 14, 6, 15);

		assertNotNull(subject.getTime(TIME_COLINDEX_NOTNULL, cal));
		assertEquals(new Time(expected.getTimeInMillis()), subject.getTime(TIME_COLINDEX_NOTNULL, cal));
		assertEquals(false, subject.wasNull());
		assertNull(subject.getTime(TIME_COLINDEX_NULL, cal));
		assertTrue(subject.wasNull());
	}

	@Test
	public void testGetTimeLabelCalendar() throws SQLException
	{
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		Calendar expected = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		expected.clear();
		expected.set(1970, 0, 1, 14, 6, 15);

		assertNotNull(subject.getTime(TIME_COL_NOT_NULL, cal));
		assertEquals(new Time(expected.getTimeInMillis()), subject.getTime(TIME_COL_NOT_NULL, cal));
		assertEquals(false, subject.wasNull());
		assertNull(subject.getTime(TIME_COL_NULL, cal));
		assertTrue(subject.wasNull());
	}

	@Test
	public void testGetTimestampIndexCalendar() throws SQLException
	{
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		Calendar expected = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		expected.clear();
		expected.set(2017, 8, 11, 8, 15, 59);

		assertNotNull(subject.getTimestamp(TIMESTAMP_COLINDEX_NOTNULL, cal));
		assertEquals(new java.sql.Timestamp(expected.getTimeInMillis()),
				subject.getTimestamp(TIMESTAMP_COLINDEX_NOTNULL, cal));
		assertEquals(false, subject.wasNull());
		assertNull(subject.getTimestamp(TIMESTAMP_COLINDEX_NULL, cal));
		assertTrue(subject.wasNull());
	}

	@Test
	public void testGetTimestampLabelCalendar() throws SQLException
	{
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		Calendar expected = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		expected.clear();
		expected.set(2017, 8, 10, 8, 15, 59);

		assertNotNull(subject.getTimestamp(TIMESTAMP_COL_NOT_NULL, cal));
		assertEquals(new java.sql.Timestamp(expected.getTimeInMillis()),
				subject.getTimestamp(TIMESTAMP_COL_NOT_NULL, cal));
		assertEquals(false, subject.wasNull());
		assertNull(subject.getTimestamp(TIMESTAMP_COL_NULL, cal));
		assertTrue(subject.wasNull());
	}

	@Test
	public void testIsClosed() throws SQLException
	{
		try (CloudSpannerResultSet rs = new CloudSpannerResultSet(mock(CloudSpannerStatement.class), getMockResultSet(),
				"SELECT * FROM FOO"))
		{
			assertFalse(rs.isClosed());
			rs.close();
			assertTrue(rs.isClosed());
		}
	}

	@Test
	public void testGetByteIndex() throws SQLException
	{
		assertNotNull(subject.getByte(LONG_COLINDEX_NOTNULL));
		assertEquals(2, subject.getByte(LONG_COLINDEX_NOTNULL));
		assertEquals(false, subject.wasNull());
		assertEquals(0, subject.getByte(LONG_COLINDEX_NULL));
		assertTrue(subject.wasNull());
	}

	@Test
	public void testGetShortIndex() throws SQLException
	{
		assertNotNull(subject.getShort(LONG_COLINDEX_NOTNULL));
		assertEquals(2, subject.getShort(LONG_COLINDEX_NOTNULL));
		assertEquals(false, subject.wasNull());
		assertEquals(0, subject.getShort(LONG_COLINDEX_NULL));
		assertTrue(subject.wasNull());
	}

	@Test
	public void testGetIntIndex() throws SQLException
	{
		assertNotNull(subject.getInt(LONG_COLINDEX_NOTNULL));
		assertEquals(2, subject.getInt(LONG_COLINDEX_NOTNULL));
		assertEquals(false, subject.wasNull());
		assertEquals(0, subject.getInt(LONG_COLINDEX_NULL));
		assertTrue(subject.wasNull());
	}

	@Test
	public void testGetFloatIndex() throws SQLException
	{
		assertNotNull(subject.getFloat(DOUBLE_COLINDEX_NOTNULL));
		assertEquals(2.123456789f, subject.getFloat(DOUBLE_COLINDEX_NOTNULL), 0f);
		assertEquals(false, subject.wasNull());
		assertEquals(0d, subject.getFloat(DOUBLE_COLINDEX_NULL), 0f);
		assertTrue(subject.wasNull());
	}

	@Test
	public void testGetByteLabel() throws SQLException
	{
		assertNotNull(subject.getByte(LONG_COL_NOT_NULL));
		assertEquals(1, subject.getByte(LONG_COL_NOT_NULL));
		assertEquals(false, subject.wasNull());
		assertEquals(0, subject.getByte(LONG_COL_NULL));
		assertTrue(subject.wasNull());
	}

	@Test
	public void testGetShortLabel() throws SQLException
	{
		assertNotNull(subject.getShort(LONG_COL_NOT_NULL));
		assertEquals(1, subject.getShort(LONG_COL_NOT_NULL));
		assertEquals(false, subject.wasNull());
		assertEquals(0, subject.getShort(LONG_COL_NULL));
		assertTrue(subject.wasNull());
	}

	@Test
	public void testGetIntLabel() throws SQLException
	{
		assertNotNull(subject.getInt(LONG_COL_NOT_NULL));
		assertEquals(1, subject.getInt(LONG_COL_NOT_NULL));
		assertEquals(false, subject.wasNull());
		assertEquals(0, subject.getInt(LONG_COL_NULL));
		assertTrue(subject.wasNull());
	}

	@Test
	public void testGetFloatLabel() throws SQLException
	{
		assertNotNull(subject.getFloat(DOUBLE_COL_NOT_NULL));
		assertEquals(1.123456789f, subject.getFloat(DOUBLE_COL_NOT_NULL), 0f);
		assertEquals(false, subject.wasNull());
		assertEquals(0f, subject.getFloat(DOUBLE_COL_NULL), 0f);
		assertTrue(subject.wasNull());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testGetObjectLabel() throws SQLException
	{
		assertNotNull(subject.getObject(DATE_COL_NOT_NULL));
		assertEquals(new java.sql.Date(2017 - 1900, 8, 10), subject.getObject(DATE_COL_NOT_NULL));
		assertEquals(false, subject.wasNull());
		assertNull(subject.getObject(DATE_COL_NULL));
		assertTrue(subject.wasNull());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testGetObjectIndex() throws SQLException
	{
		assertNotNull(subject.getObject(DATE_COLINDEX_NOTNULL));
		assertEquals(new java.sql.Date(2017 - 1900, 8, 10), subject.getObject(DATE_COLINDEX_NOTNULL));
		assertEquals(false, subject.wasNull());
		assertNull(subject.getObject(DATE_COLINDEX_NULL));
		assertTrue(subject.wasNull());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testGetObjectLabelMap() throws SQLException
	{
		Map<String, Class<?>> map = new HashMap<>();
		assertNotNull(subject.getObject(DATE_COL_NOT_NULL, map));
		assertEquals(new java.sql.Date(2017 - 1900, 8, 10), subject.getObject(DATE_COL_NOT_NULL, map));
		assertEquals(false, subject.wasNull());
		assertNull(subject.getObject(DATE_COL_NULL, map));
		assertTrue(subject.wasNull());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testGetObjectIndexMap() throws SQLException
	{
		Map<String, Class<?>> map = Collections.emptyMap();
		assertNotNull(subject.getObject(DATE_COLINDEX_NOTNULL, map));
		assertEquals(new java.sql.Date(2017 - 1900, 8, 10), subject.getObject(DATE_COLINDEX_NOTNULL, map));
		assertEquals(false, subject.wasNull());
		assertNull(subject.getObject(DATE_COLINDEX_NULL, map));
		assertTrue(subject.wasNull());
	}

	@Test
	public void testGetArrayLabel() throws SQLException
	{
		assertNotNull(subject.getArray(ARRAY_COL_NOT_NULL));
		assertEquals(CloudSpannerArray.createArray(CloudSpannerDataType.INT64, Arrays.asList(1L, 2L, 3L)),
				subject.getArray(ARRAY_COL_NOT_NULL));
		assertEquals(false, subject.wasNull());
		assertNull(subject.getArray(ARRAY_COL_NULL));
		assertTrue(subject.wasNull());
	}

	@Test
	public void testGetArrayIndex() throws SQLException
	{
		assertNotNull(subject.getArray(ARRAY_COLINDEX_NOTNULL));
		assertEquals(CloudSpannerArray.createArray(CloudSpannerDataType.INT64, Arrays.asList(1L, 2L, 3L)),
				subject.getArray(ARRAY_COLINDEX_NOTNULL));
		assertEquals(false, subject.wasNull());
		assertNull(subject.getArray(ARRAY_COLINDEX_NULL));
		assertTrue(subject.wasNull());
	}

	@Test
	public void testGetWarnings() throws SQLException
	{
		assertNull(subject.getWarnings());
	}

	@Test
	public void testClearWarnings() throws SQLException
	{
		subject.clearWarnings();
	}

	@Test
	public void testIsBeforeFirst() throws SQLException
	{
		try (CloudSpannerResultSet rs = new CloudSpannerResultSet(mock(CloudSpannerStatement.class), getMockResultSet(),
				"SELECT * FROM FOO"))
		{
			assertTrue(rs.isBeforeFirst());
			rs.next();
			assertFalse(rs.isBeforeFirst());
		}
	}

	@Test
	public void testIsAfterLast() throws SQLException
	{
		try (CloudSpannerResultSet rs = new CloudSpannerResultSet(mock(CloudSpannerStatement.class), getMockResultSet(),
				"SELECT * FROM FOO"))
		{
			assertFalse(rs.isAfterLast());
			while (rs.next())
			{
				// do nothing
			}
			assertTrue(rs.isAfterLast());
		}
	}

	@Test
	public void testGetCharacterStreamIndex() throws SQLException, IOException
	{
		assertNotNull(subject.getCharacterStream(STRING_COLINDEX_NOTNULL));
		Reader actual = subject.getCharacterStream(STRING_COLINDEX_NOTNULL);
		char[] cbuf = new char[10];
		int len = actual.read(cbuf, 0, cbuf.length);
		assertEquals("BAR", new String(cbuf, 0, len));
		assertEquals(3, len);
		assertEquals(false, subject.wasNull());
		assertNull(subject.getCharacterStream(STRING_COLINDEX_NULL));
		assertTrue(subject.wasNull());
	}

	@Test
	public void testGetCharacterStreamLabel() throws SQLException, IOException
	{
		assertNotNull(subject.getCharacterStream(STRING_COL_NOT_NULL));
		Reader actual = subject.getCharacterStream(STRING_COL_NOT_NULL);
		char[] cbuf = new char[10];
		int len = actual.read(cbuf, 0, cbuf.length);
		assertEquals("FOO", new String(cbuf, 0, len));
		assertEquals(3, len);
		assertEquals(false, subject.wasNull());
		assertNull(subject.getCharacterStream(STRING_COL_NULL));
		assertTrue(subject.wasNull());
	}

	@Test
	public void testGetNCharacterStreamIndex() throws SQLException, IOException
	{
		assertNotNull(subject.getNCharacterStream(STRING_COLINDEX_NOTNULL));
		Reader actual = subject.getNCharacterStream(STRING_COLINDEX_NOTNULL);
		char[] cbuf = new char[10];
		int len = actual.read(cbuf, 0, cbuf.length);
		assertEquals("BAR", new String(cbuf, 0, len));
		assertEquals(3, len);
		assertEquals(false, subject.wasNull());
		assertNull(subject.getNCharacterStream(STRING_COLINDEX_NULL));
		assertTrue(subject.wasNull());
	}

	@Test
	public void testGetNCharacterStreamLabel() throws SQLException, IOException
	{
		assertNotNull(subject.getNCharacterStream(STRING_COL_NOT_NULL));
		Reader actual = subject.getNCharacterStream(STRING_COL_NOT_NULL);
		char[] cbuf = new char[10];
		int len = actual.read(cbuf, 0, cbuf.length);
		assertEquals("FOO", new String(cbuf, 0, len));
		assertEquals(3, len);
		assertEquals(false, subject.wasNull());
		assertNull(subject.getNCharacterStream(STRING_COL_NULL));
		assertTrue(subject.wasNull());
	}

	@Test
	public void testGetAsciiStreamIndex() throws SQLException, IOException
	{
		assertNotNull(subject.getAsciiStream(STRING_COLINDEX_NOTNULL));
		InputStream actual = subject.getAsciiStream(STRING_COLINDEX_NOTNULL);
		byte[] cbuf = new byte[10];
		int len = actual.read(cbuf, 0, cbuf.length);
		assertEquals("BAR", new String(cbuf, 0, len, StandardCharsets.US_ASCII));
		assertEquals(3, len);
		assertEquals(false, subject.wasNull());
		assertNull(subject.getAsciiStream(STRING_COLINDEX_NULL));
		assertTrue(subject.wasNull());
	}

	@Test
	public void testGetUnicodeStreamIndex() throws SQLException, IOException
	{
		assertNotNull(subject.getUnicodeStream(STRING_COLINDEX_NOTNULL));
		InputStream actual = subject.getUnicodeStream(STRING_COLINDEX_NOTNULL);
		byte[] cbuf = new byte[10];
		int len = actual.read(cbuf, 0, cbuf.length);
		assertEquals("BAR", new String(cbuf, 0, len, StandardCharsets.UTF_16LE));
		assertEquals(6, len);
		assertEquals(false, subject.wasNull());
		assertNull(subject.getUnicodeStream(STRING_COLINDEX_NULL));
		assertTrue(subject.wasNull());
	}

	@Test
	public void testGetBinaryStreamIndex() throws SQLException, IOException
	{
		assertNotNull(subject.getBinaryStream(BYTES_COLINDEX_NOTNULL));
		InputStream actual = subject.getBinaryStream(BYTES_COLINDEX_NOTNULL);
		byte[] cbuf = new byte[3];
		int len = actual.read(cbuf, 0, cbuf.length);
		assertArrayEquals(ByteArray.copyFrom("BAR").toByteArray(), cbuf);
		assertEquals(3, len);
		assertEquals(false, subject.wasNull());
		assertNull(subject.getUnicodeStream(BYTES_COLINDEX_NULL));
		assertTrue(subject.wasNull());
	}

	@Test
	public void testGetAsciiStreamLabel() throws SQLException, IOException
	{
		assertNotNull(subject.getAsciiStream(STRING_COL_NOT_NULL));
		InputStream actual = subject.getAsciiStream(STRING_COL_NOT_NULL);
		byte[] cbuf = new byte[10];
		int len = actual.read(cbuf, 0, cbuf.length);
		assertEquals("FOO", new String(cbuf, 0, len, StandardCharsets.US_ASCII));
		assertEquals(3, len);
		assertEquals(false, subject.wasNull());
		assertNull(subject.getAsciiStream(STRING_COL_NULL));
		assertTrue(subject.wasNull());
	}

	@Test
	public void testGetUnicodeStreamLabel() throws SQLException, IOException
	{
		assertNotNull(subject.getUnicodeStream(STRING_COL_NOT_NULL));
		InputStream actual = subject.getUnicodeStream(STRING_COL_NOT_NULL);
		byte[] cbuf = new byte[10];
		int len = actual.read(cbuf, 0, cbuf.length);
		assertEquals("FOO", new String(cbuf, 0, len, StandardCharsets.UTF_16LE));
		assertEquals(6, len);
		assertEquals(false, subject.wasNull());
		assertNull(subject.getUnicodeStream(STRING_COL_NULL));
		assertTrue(subject.wasNull());
	}

	@Test
	public void testGetBinaryStreamLabel() throws SQLException, IOException
	{
		assertNotNull(subject.getBinaryStream(BYTES_COL_NOT_NULL));
		InputStream actual = subject.getBinaryStream(BYTES_COL_NOT_NULL);
		byte[] cbuf = new byte[3];
		int len = actual.read(cbuf, 0, cbuf.length);
		assertArrayEquals(ByteArray.copyFrom("FOO").toByteArray(), cbuf);
		assertEquals(3, len);
		assertEquals(false, subject.wasNull());
		assertNull(subject.getUnicodeStream(BYTES_COL_NULL));
		assertTrue(subject.wasNull());
	}

	@Test
	public void testGetBeforeNext() throws SQLException
	{
		try (CloudSpannerResultSet rs = new CloudSpannerResultSet(mock(CloudSpannerStatement.class), getMockResultSet(),
				"SELECT * FROM FOO"))
		{
			thrown.expect(SQLException.class);
			thrown.expectMessage("Before first record");
			rs.getBigDecimal(LONG_COLINDEX_NOTNULL);
		}
	}

	@Test
	public void testGetAfterLast() throws SQLException
	{
		try (CloudSpannerResultSet rs = new CloudSpannerResultSet(mock(CloudSpannerStatement.class), getMockResultSet(),
				"SELECT * FROM FOO"))
		{
			while (rs.next())
			{
				// do nothing
			}
			thrown.expect(SQLException.class);
			thrown.expectMessage("After last record");
			rs.getBigDecimal(LONG_COLINDEX_NOTNULL);
		}
	}

	@Test
	public void testFindIllegalColumnName() throws SQLException
	{
		thrown.expect(SQLException.class);
		thrown.expectMessage("Column not found");
		int index = subject.findColumn(UNKNOWN_COLUMN);
		assertEquals(0, index);
	}

	@Test
	public void testGetRowAndIsFirst() throws SQLException
	{
		try (CloudSpannerResultSet rs = new CloudSpannerResultSet(mock(CloudSpannerStatement.class), getMockResultSet(),
				"SELECT * FROM FOO"))
		{
			int row = 0;
			while (rs.next())
			{
				row++;
				assertEquals(row, rs.getRow());
				assertEquals(row == 1, rs.isFirst());
			}
		}
	}

	@Test
	public void testGetHoldability() throws SQLException
	{
		assertEquals(java.sql.ResultSet.HOLD_CURSORS_OVER_COMMIT, subject.getHoldability());
	}

}
