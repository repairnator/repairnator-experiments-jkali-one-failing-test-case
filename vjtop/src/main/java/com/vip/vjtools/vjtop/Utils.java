package com.vip.vjtools.vjtop;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Utils {

	public static int NANOS_TO_MILLS = 1000 * 1000;

	private static final int BYTE_SIZE = 1;
	private static final int KB_SIZE = BYTE_SIZE * 1024;
	private static final int MB_SIZE = KB_SIZE * 1024;
	private static final int GB_SIZE = MB_SIZE * 1024;

	/**
	 * Formats a long value containing "number of bytes" to its megabyte representation. If the value is negative, "n/a"
	 * will be returned.
	 */
	public static String toMB(long bytes) {
		if (bytes < 0) {
			return "n/a";
		}
		return Long.toString(bytes / 1024 / 1024) + "m";
	}

	public static String toSizeUnit(Long size) {
		if (size == null) {
			return "n/a";
		}
		if (size < 1024) {
			return String.format("%4d", size);
		}

		if (size / 1024 < 1024) {
			return String.format("%4dk", size / 1024);
		}

		if (size / (1024 * 1024) < 1024) {
			return String.format("%4dm", size / (1024 * 1024));
		}

		if (size / (1024 * 1024 * 1024) < 1024) {
			return String.format("%4dg", size / (1024 * 1024 * 1024));
		}

		return String.format("%4dt", size / (1024 * 1024 * 1024 * 1024));
	}

	public static String toTimeUnit(long millis) {
		long seconds = millis / 1000;
		if (seconds < 60) {
			return String.format("%02ds", seconds);
		}

		if (seconds < 3600) {
			return String.format("%02dm%02ds", seconds / 60, seconds % 60);
		}

		if (seconds < (24 * 3600)) {
			return String.format("%02dh%02dm", seconds / 3600, (seconds / 60) % 60);
		}

		return String.format("%dd%02dh", seconds / (3600 * 24), (seconds / 3600) % 24);
	}

	/**
	 * Returns a substring of the given string, representing the 'length' most-right characters
	 */
	public static String rightStr(String str, int length) {
		return str.substring(Math.max(0, str.length() - length));
	}

	/**
	 * Returns a substring of the given string, representing the 'length' most-left characters
	 */
	public static String leftStr(String str, int length) {
		return str.substring(0, Math.min(str.length(), length));
	}

	/**
	 * shortName("123456789", 8, 3) = "12...789"
	 * 
	 * shortName("123456789", 8, 2) = "123...89"
	 */
	public static String shortName(String str, int length, int rightLength) {
		if (str.length() > length) {
			int leftIndex = length - 3 - rightLength;
			str = str.substring(0, Math.max(0, leftIndex)) + "..."
					+ str.substring(Math.max(0, str.length() - rightLength), str.length());
		}
		return str;
	}

	/**
	 * Joins the given list of strings using the given delimiter delim
	 */
	public static String join(List<String> list, String delim) {

		StringBuilder sb = new StringBuilder();

		String loopDelim = "";

		for (String s : list) {

			sb.append(loopDelim);
			sb.append(s);

			loopDelim = delim;
		}

		return sb.toString();
	}

	/**
	 * calculates a "load", given on two deltas
	 */
	public static double calcLoad(double deltaUptime, double deltaTime, int factor) {
		if (deltaTime <= 0 || deltaUptime == 0) {
			return 0.0;
		}
		return Math.min(99.99, deltaTime / (deltaUptime * factor));
	}

	/**
	 * Sorts a Map by its values, using natural ordering.
	 */
	public static long[] sortAndFilterThreadIdsByValue(Map map, int threadLimit) {
		int max = Math.min(threadLimit, map.size());
		List<Map.Entry> list = new LinkedList(map.entrySet());
		Collections.sort(list, new Comparator() {
			@Override
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o2)).getValue()).compareTo(((Map.Entry) (o1)).getValue());
			}
		});

		long[] topTidArray = new long[max];
		int i = 0;
		for (Map.Entry entry : list) {
			topTidArray[i] = (Long) entry.getKey();
			if (++i >= max) {
				break;
			}
		}

		return topTidArray;
	}

	public static long parseFromSize(String str) {
		if (str == null || str.isEmpty()) {
			return -1;
		}

		str = str.toLowerCase();
		int fromScale = BYTE_SIZE;

		try {
			if (str.endsWith("kb")) {
				str = str.substring(0, str.length() - 2).trim();
				fromScale = KB_SIZE;
			}
			if (str.endsWith("k")) {
				str = str.substring(0, str.length() - 1).trim();
				fromScale = KB_SIZE;
			} else if (str.endsWith("mb")) {
				str = str.substring(0, str.length() - 2).trim();
				fromScale = MB_SIZE;
			} else if (str.endsWith("m")) {
				str = str.substring(0, str.length() - 1).trim();
				fromScale = MB_SIZE;
			} else if (str.endsWith("gb")) {
				str = str.substring(0, str.length() - 2).trim();
				fromScale = GB_SIZE;
			} else if (str.endsWith("g")) {
				str = str.substring(0, str.length() - 1).trim();
				fromScale = GB_SIZE;
			} else if (str.endsWith("bytes")) {
				str = str.substring(0, str.length() - "bytes".length()).trim();
				fromScale = BYTE_SIZE;
			}

			str = str.replace(",", "");

			long value = (long) Double.parseDouble(str);
			return value * fromScale;
		} catch (Throwable ex) {
			return -1;
		}
	}

	public static void sleep(long mills) {
		try {
			Thread.sleep(mills);
		} catch (InterruptedException e) {
		}
	}
}
