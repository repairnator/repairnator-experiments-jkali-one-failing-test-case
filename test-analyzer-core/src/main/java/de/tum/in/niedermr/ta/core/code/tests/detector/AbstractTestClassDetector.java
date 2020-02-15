package de.tum.in.niedermr.ta.core.code.tests.detector;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;
import java.util.regex.Pattern;

import org.objectweb.asm.tree.ClassNode;

import de.tum.in.niedermr.ta.core.code.util.BytecodeUtility;
import de.tum.in.niedermr.ta.core.code.util.Identification;
import de.tum.in.niedermr.ta.core.code.util.JavaUtility;
import de.tum.in.niedermr.ta.core.common.util.StringUtility;

public abstract class AbstractTestClassDetector implements ITestClassDetector {

	protected static final boolean IGNORE_IGNORED_TEST_CASES = true;

	private final boolean m_ignoreAbstractClasses;
	private final Collection<Pattern> m_testClassIncludePatterns;
	private final Collection<Pattern> m_testClassExcludePatterns;

	public AbstractTestClassDetector(boolean acceptAbstractTestClasses, String[] testClassIncludes,
			String[] testClassExcludes) {
		m_ignoreAbstractClasses = !(acceptAbstractTestClasses);
		m_testClassIncludePatterns = compilePatterns(testClassIncludes);
		m_testClassExcludePatterns = compilePatterns(testClassExcludes);
	}

	protected Collection<Pattern> compilePatterns(String[] patternStrings) {
		Collection<Pattern> compiledPatterns = new LinkedList<>();

		for (String item : patternStrings) {
			if (!StringUtility.isNullOrEmpty(item)) {
				compiledPatterns.add(Pattern.compile(item));
			}
		}

		return compiledPatterns;
	}

	/** {@inheritDoc} */
	@Override
	public final ClassType analyzeIsTestClass(ClassNode cn) {
		ClassType classType = isTestClassInternal(cn);

		if (classType.isTestClass()) {
			if (!isIncludedClass(cn.name)) {
				return ClassType.IGNORED_TEST_CLASS;
			}

			if (m_ignoreAbstractClasses && BytecodeUtility.isAbstractClass(cn)) {
				return ClassType.NON_EXECUTABLE_TEST_CLASS;
			}

			if (isExcludeNonPublicClasses() && !BytecodeUtility.isPublicClass(cn)) {
				return ClassType.NON_EXECUTABLE_TEST_CLASS;
			}

			if (isExcludeTestClassesWithNonDefaultConstructor() && !BytecodeUtility.hasPublicParameterlessConstructor(cn)) {
				return ClassType.NON_EXECUTABLE_TEST_CLASS;
			}
		}

		if (!classType.isTestClass() && isInnerClassInTestOrIgnoredClass(cn)) {
			return ClassType.INNER_CLASS_IN_TEST_OR_IGNORED_CLASS;
		}

		return classType;
	}

	protected abstract ClassType isTestClassInternal(ClassNode cn);

	private boolean isIncludedClass(String classPathOrName) {
		String className = Identification.asClassName(classPathOrName);

		for (Pattern p : m_testClassExcludePatterns) {
			if (p.matcher(className).find()) {
				// exclude pattern matched
				return false;
			}
		}

		if (m_testClassIncludePatterns.isEmpty()) {
			// no include pattern defined -> take all
			return true;
		}

		for (Pattern p : m_testClassIncludePatterns) {
			if (p.matcher(className).find()) {
				// include pattern matched
				return true;
			}
		}

		// include patterns are defined but no pattern matched
		return false;
	}

	protected boolean isExcludeTestClassesWithNonDefaultConstructor() {
		return false;
	}

	protected boolean isExcludeNonPublicClasses() {
		return true;
	}

	/**
	 * Check if the class node is an inner class and an outer class is a test class.
	 */
	private boolean isInnerClassInTestOrIgnoredClass(ClassNode cn) {
		Optional<Class<?>> outerClass = JavaUtility.getOuterClassNoEx(cn);

		if (!outerClass.isPresent()) {
			return false;
		}

		try {
			ClassNode outerClassNode = BytecodeUtility.getAcceptedClassNode(outerClass.get());

			ClassType classType = analyzeIsTestClass(outerClassNode);
			if (classType.isTestClass() || !classType.isSourceClass()) {
				return true;
			}
		} catch (IOException e) {
			// ignore
		}

		return false;
	}
}
