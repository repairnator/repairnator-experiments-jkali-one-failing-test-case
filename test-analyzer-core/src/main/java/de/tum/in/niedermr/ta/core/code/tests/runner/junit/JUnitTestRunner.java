package de.tum.in.niedermr.ta.core.code.tests.runner.junit;

import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;

import de.tum.in.niedermr.ta.core.code.tests.detector.ITestClassDetector;
import de.tum.in.niedermr.ta.core.code.tests.detector.junit.JUnitTestClassDetector;
import de.tum.in.niedermr.ta.core.code.tests.runner.ITestRunner;

public class JUnitTestRunner implements ITestRunner {
	protected final JUnitCore m_jUnitCore;

	/** Constructor. */
	public JUnitTestRunner() {
		m_jUnitCore = new JUnitCore();
	}

	/** {@inheritDoc} */
	@Override
	public JUnitTestRunResult runTest(Class<?> testClass, String testcaseName) throws ReflectiveOperationException {
		// may cause the program to terminate if the test case or one of its invoked methods invokes System.exit
		Result result = m_jUnitCore.run(Request.method(testClass, testcaseName));

		return new JUnitTestRunResult(result);
	}

	/** {@inheritDoc} */
	@Override
	public ITestClassDetector createTestClassDetector(boolean acceptAbstractTestClasses, String[] testClassIncludes,
			String[] testClassExcludes) {
		return new JUnitTestClassDetector(acceptAbstractTestClasses, testClassIncludes, testClassExcludes);
	}
}
