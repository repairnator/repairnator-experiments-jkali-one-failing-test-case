package de.tum.in.niedermr.ta.core.analysis.instrumentation;

import java.util.HashSet;
import java.util.Set;

/**
 * Logger for method invocations.<br/>
 * <b>Do not move or rename this class. It will be invoked by generated code.</b>
 */
public class InvocationLogger {
	private static LoggingMode s_mode;
	private static Set<String> s_testingLog;
	private static Set<String> s_framingLog;

	static {
		s_testingLog = new HashSet<>();
		s_framingLog = new HashSet<>();

		reset();
	}

	/**
	 * Start the log. <br/>
	 * Invoked from generated code.
	 */
	public static synchronized void log(String methodIdentifier) {
		if (s_mode == LoggingMode.TESTING) {
			s_testingLog.add(methodIdentifier);
		} else if (s_mode == LoggingMode.FRAMING) {
			s_framingLog.add(methodIdentifier);
		} else {
			throw new IllegalStateException();
		}
	}

	public static synchronized Set<String> getFramingLog() {
		return s_framingLog;
	}

	public static synchronized Set<String> getTestingLog() {
		return s_testingLog;
	}

	public static synchronized void reset() {
		s_testingLog.clear();
		s_framingLog.clear();

		setMode(LoggingMode.FRAMING);
	}

	/**
	 * Set the current mode. <br/>
	 * Invoked from generated code.
	 */
	public static synchronized void setMode(LoggingMode newMode) {
		s_mode = newMode;
	}

	public enum LoggingMode {
		/**
		 * Framing: For the preparation before and the wrap-up after tests. (e. g. invocations from
		 * <code>@BeforeClass</code> / <code>@BeforeAll</code>, <code>@Before</code>, <code>@After</code>,
		 * <code>@AfterClass</code> / <code>@AfterAll</code>)
		 */
		FRAMING,
		/**
		 * Testing: For the actual test.
		 */
		TESTING;
	}
}
