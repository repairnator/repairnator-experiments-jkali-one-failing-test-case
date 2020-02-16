package org.yamcs.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.BeforeClass;
import org.junit.Test;

public class TimeEncodingTest {
    static final long j1972 = (2L * 365 * 24 * 3600 + 10) * 1000 + 123;

    @BeforeClass
    public static void setUpBeforeClass() {
        TimeEncoding.setUp();
    }

    @Test
    public void testCurrentInstant() {

        TimeEncoding.getWallclockTime();
        long time = System.currentTimeMillis();
        long instant = TimeEncoding.getWallclockTime();
        long correction = Math.abs(instant - time) % 1000;

        assertTrue(correction < 3);
        time += correction;

        String sinstant = TimeEncoding.toString(instant);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String stime = sdf.format(new Date(time));
        assertEquals(sinstant, stime);

    }

    @Test
    public void testToStringLong() {
        assertEquals("1972-01-01T00:00:00.123Z", TimeEncoding.toString(j1972));
    }

    @Test
    public void testToOrdinalDateTimeLong() {
        assertEquals("1972-001T00:00:00.123", TimeEncoding.toOrdinalDateTime(j1972));
    }

    @Test
    public void testToCombinedFormatLong() {
        assertEquals("1972-01-01/001T00:00:00.123", TimeEncoding.toCombinedFormat(j1972));
    }

    @Test
    public void testFromGpsCcsds() {
        long instant = TimeEncoding.fromGpsCcsdsTime(0, (byte) 128);
        assertEquals("1980-01-06T00:00:00.500Z", TimeEncoding.toString(instant));
    }

    @Test
    public void testFromGpsYearSecMillis() {
        long instant = TimeEncoding.fromGpsYearSecMillis(2010, 15, 200);
        assertEquals("2010-01-01T00:00:00.200Z", TimeEncoding.toString(instant));
    }

    @Test
    public void getInstantFromUnix2() throws ParseException {
        long instant = TimeEncoding.fromUnixMillisec(123);
        assertEquals("1970-01-01T00:00:00.123Z", TimeEncoding.toString(instant));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        long inst1 = TimeEncoding.fromUnixMillisec(sdf.parse("2008-12-31T23:59:59.125Z").getTime());
        long inst2 = TimeEncoding.fromUnixMillisec(sdf.parse("2009-01-01T00:00:00.126Z").getTime());
        assertEquals("2008-12-31T23:59:59.125Z", TimeEncoding.toString(inst1));
        assertEquals("2009-01-01T00:00:00.126Z", TimeEncoding.toString(inst2));
        assertEquals(2001, (inst2 - inst1));
    }

    @Test
    public void getUnixFromInstant() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        long unix1 = sdf.parse("2008-12-31T23:59:59.125Z").getTime();
        long unix2 = sdf.parse("2009-01-01T00:00:00.126Z").getTime();

        long inst1 = TimeEncoding.fromUnixMillisec(unix1);
        long inst2 = TimeEncoding.fromUnixMillisec(unix2);

        assertEquals("2008-12-31T23:59:59.125Z", TimeEncoding.toString(inst1));
        assertEquals("2009-01-01T00:00:00.126Z", TimeEncoding.toString(inst2));
        assertEquals(1001, (unix2 - unix1));
        assertEquals(2001, (inst2 - inst1));
    }

    @Test
    public void testGetInstantFromUnix() {
        long instant = TimeEncoding.fromUnixTime(1266539888, 20000);
        assertEquals("2010-02-19T00:38:08.020Z", TimeEncoding.toString(instant));
    }

    @Test
    public void testGetInstantFromCal() throws ParseException {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        String utc = "2010-01-01T00:00:00.000Z";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date d = sdf.parse(utc);
        cal.setTime(d);
        long instant = TimeEncoding.fromCalendar(cal);
        String s = TimeEncoding.toString(instant);

        assertEquals(utc, s);

        utc = "2010-12-31T23:59:59.000Z";
        d = sdf.parse(utc);
        cal.setTime(d);
        instant = TimeEncoding.fromCalendar(cal);
        s = TimeEncoding.toString(instant);
        assertEquals(utc, s);
    }

    @Test
    public void testGetJavaGpsFromInstant() {
        String utc = "2010-01-01T00:00:00";
        String javagps = "2010-01-01T00:00:15.000";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        long instant = TimeEncoding.parse(utc);
        long jt = TimeEncoding.getJavaGpsFromInstant(instant);
        String s = sdf.format(new Date(jt));
        assertEquals(javagps, s);
    }

    @Test
    public void testParse() {
        assertEquals(1230768032001L, TimeEncoding.parse("2008-12-31T23:59:59.001"));
        assertEquals(1230768033000L, TimeEncoding.parse("2008-12-31T23:59:60"));
        assertEquals(1230768034000L, TimeEncoding.parse("2009-01-01T00:00:00"));

        assertEquals(1230768034000L, TimeEncoding.parse("2009/001T00:00:00"));
        assertEquals(1230768033000L, TimeEncoding.parse("2008/366T23:59:60"));
    }

    @Test
    public void testToString() {
        assertEquals("2008-12-31T23:59:59.000Z", TimeEncoding.toString(1230768032000L));
        assertEquals("2008-12-31T23:59:60.000Z", TimeEncoding.toString(1230768033000L));
        assertEquals("2009-01-01T00:00:00.000Z", TimeEncoding.toString(1230768034000L));
    }

    @Test
    public void testGetGpsTime1() {
        final int startCoarseTime = 981456898;

        for (int coarseTime = startCoarseTime; coarseTime < startCoarseTime + 5; ++coarseTime) {
            for (short fineTime = 0; fineTime < 256; ++fineTime) {
                GpsCcsdsTime time = TimeEncoding.toGpsTime(TimeEncoding.fromGpsCcsdsTime(coarseTime, (byte) fineTime));

                // System.out.println("in coarse: " + coarseTime + "\tin fine: " + (fineTime&0xFF));
                // System.out.println("out coarse: " + time.coarseTime + "\tout fine: " + (time.fineTime&0xFF));

                assertEquals(time.coarseTime, coarseTime);
                assertTrue(Math.abs((time.fineTime & 0xFF) - fineTime) <= 1);
            }
        }
    }

    @Test
    public void testGetGpsTime2() {
        long instant = 1293841234004L;
        GpsCcsdsTime time = TimeEncoding.toGpsTime(instant);
        assertEquals(977876415, time.coarseTime);
        assertEquals(1, time.fineTime);
    }
    
    @Test
    public void testTaiOffset() {
        long instant = TimeEncoding.fromTaiMillisec(0);
        assertEquals(TimeEncoding.parse("1958-01-01T00:00:00"), instant);
    }
    
    @Test
    public void testJ2000Offset() {
        long instant = TimeEncoding.fromJ2000Millisec(0);
        assertEquals(TimeEncoding.parse("2000-01-01T11:58:55.816"), instant);
    }
}
