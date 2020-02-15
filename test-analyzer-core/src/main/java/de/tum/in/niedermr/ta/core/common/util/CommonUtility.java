package de.tum.in.niedermr.ta.core.common.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class CommonUtility {

	protected static final DateTimeFormatter DATE_TIME_FOR_FILE_PATTERN_BASE = DateTimeFormatter
			.ofPattern("yyyyMMdd-HHmmss");
	protected static final DateTimeFormatter DATE_TIME_FOR_FILE_WITH_MS_PATTERN_BASE = DateTimeFormatter
			.ofPattern("yyyyMMdd-HHmmssSSS");

	public static String createRandomId(int length) {
		Random rd = new Random();

		StringBuilder sB = new StringBuilder();

		for (int i = 0; i < length; i++) {
			int x = rd.nextInt(36);

			if (x < 10) {
				sB.append(x);
			} else {
				sB.append((char) (65 - 10 + x));
			}
		}

		return sB.toString();
	}

	/** Get the duration in seconds. */
	public static long getDurationInSec(long startTime, TimeUnit inputTimeUnit) {
		long endTime = inputTimeUnit.convert(System.nanoTime(), TimeUnit.NANOSECONDS);

		if (endTime - startTime < 0) {
			throw new IllegalStateException("endTime < startTime");
		}

		return TimeUnit.SECONDS.convert(endTime - startTime, inputTimeUnit);
	}

	/** Check if the tests are running on Windows. */
	public static boolean isRunningOnWindows() {
		return System.getProperty("os.name").toLowerCase().contains("win");
	}

	public static String createDateTimeStringForFile() {
		return createDateTimeStringForFile(DATE_TIME_FOR_FILE_PATTERN_BASE, Instant.now(), ZoneId.systemDefault());
	}

	public static String createDateTimeWithMsStringForFile() {
		return createDateTimeStringForFile(DATE_TIME_FOR_FILE_WITH_MS_PATTERN_BASE, Instant.now(),
				ZoneId.systemDefault());
	}

	protected static String createDateTimeStringForFile(DateTimeFormatter formatter, Instant instant, ZoneId zoneId) {
		DateTimeFormatter formatterPattern = formatter.withZone(zoneId);
		return formatterPattern.format(instant);
	}
}
