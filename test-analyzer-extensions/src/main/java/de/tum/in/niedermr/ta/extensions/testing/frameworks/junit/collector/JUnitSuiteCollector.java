package de.tum.in.niedermr.ta.extensions.testing.frameworks.junit.collector;

import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;

import de.tum.in.niedermr.ta.core.code.tests.collector.TestCollector;
import de.tum.in.niedermr.ta.core.code.tests.detector.ClassType;
import de.tum.in.niedermr.ta.core.code.tests.detector.junit.JUnitClassTypeResult;
import de.tum.in.niedermr.ta.core.code.util.JavaUtility;
import de.tum.in.niedermr.ta.extensions.testing.frameworks.junit.detector.JUnitSuiteDetector;

public class JUnitSuiteCollector extends TestCollector {

	/** Logger. */
	private static final Logger LOGGER = LogManager.getLogger(JUnitSuiteCollector.class);

	public JUnitSuiteCollector(JUnitSuiteDetector suiteDetector) {
		super(suiteDetector);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isCollectTestcasesInNonAbstractSuperClasses() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isCollectTestcasesInAbstractSuperClasses() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	protected Set<String> collectTestcasesInThisClass(ClassNode cn, ClassType testClassType) {
		if (testClassType.equals(JUnitClassTypeResult.TEST_SUITE_JUNIT_3)) {
			return collectTestsInJUnit3Suite(cn);
		} else if (testClassType.equals(JUnitClassTypeResult.TEST_SUITE_JUNIT_4)) {
			throw new UnsupportedOperationException("JUnit 4 suites are not supported.");
		}

		return new HashSet<>();
	}

	private Set<String> collectTestsInJUnit3Suite(ClassNode cn) {
		Set<String> testcases = new HashSet<>();

		try {
			Class<?> cls = JavaUtility.loadClass(JavaUtility.toClassName(cn.name));

			List<junit.framework.Test> testsOfSuite = getTestsOfSuite(cls);

			int index = 0;

			for (junit.framework.Test test : testsOfSuite) {
				testcases.add("T_" + index + "_" + test.toString());
				index++;
			}
		} catch (ClassNotFoundException e) {
			LOGGER.error("ClassNotFoundException", e);
		}

		return testcases;
	}

	public List<junit.framework.Test> getTestsOfSuite(Class<?> cls) {
		try {
			Method suiteMethod = ((JUnitSuiteDetector) m_testClassDetector).getJUnit3SuiteMethod(cls);
			junit.framework.Test suiteTestcase = (junit.framework.Test) suiteMethod.invoke(null);

			return getTestsOfSuiteTestcase(suiteTestcase);
		} catch (ReflectiveOperationException e) {
			LOGGER.error("Exception in getTestsOfSuite", e);
		}

		return new LinkedList<>();
	}

	private List<junit.framework.Test> getTestsOfSuiteTestcase(junit.framework.Test suiteTestcase) {
		List<junit.framework.Test> testsOfSuite = new LinkedList<>();

		if (suiteTestcase instanceof junit.framework.TestSuite) {
			testsOfSuite.addAll(getTestsOfSuite((junit.framework.TestSuite) suiteTestcase));
		} else {
			testsOfSuite.add(suiteTestcase);
		}

		return testsOfSuite;
	}

	private List<junit.framework.Test> getTestsOfSuite(junit.framework.TestSuite suite) {
		List<junit.framework.Test> testsOfSuite = new LinkedList<>();

		Enumeration<junit.framework.Test> containedTests = suite.tests();

		while (containedTests.hasMoreElements()) {
			junit.framework.Test currentTest = containedTests.nextElement();

			testsOfSuite.addAll(getTestsOfSuiteTestcase(currentTest));
		}

		return testsOfSuite;
	}
}
