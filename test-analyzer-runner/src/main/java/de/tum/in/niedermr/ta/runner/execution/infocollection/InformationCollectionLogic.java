package de.tum.in.niedermr.ta.runner.execution.infocollection;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.tum.in.niedermr.ta.core.analysis.instrumentation.InvocationLogger;
import de.tum.in.niedermr.ta.core.analysis.result.receiver.IResultReceiver;
import de.tum.in.niedermr.ta.core.analysis.result.receiver.ResultReceiverFactory;
import de.tum.in.niedermr.ta.core.code.identifier.MethodIdentifier;
import de.tum.in.niedermr.ta.core.code.identifier.TestcaseIdentifier;
import de.tum.in.niedermr.ta.core.code.tests.TestInformation;
import de.tum.in.niedermr.ta.core.code.tests.runner.ITestRunResult;
import de.tum.in.niedermr.ta.core.common.constants.FileSystemConstants;

/**
 * Logic to collect the information. <br/>
 * Parameterless constructor required.
 */
public class InformationCollectionLogic extends AbstractInformationCollectionLogic {

	/** Logger. */
	private static final Logger LOGGER = LogManager.getLogger(InformationCollectionLogic.class);

	protected final Map<MethodIdentifier, TestInformation> m_methodInformation;

	public InformationCollectionLogic() {
		this.m_methodInformation = new HashMap<>();
	}

	/** {@inheritDoc} */
	@Override
	protected void execBeforeExecutingTestcase(TestcaseIdentifier testCaseIdentifier) {
		resetInvocationLog();
	}

	/** {@inheritDoc} */
	@Override
	protected void execTestcaseExecutedSuccessfully(TestcaseIdentifier testCaseIdentifier) {
		super.execTestcaseExecutedSuccessfully(testCaseIdentifier);
		recordTestcaseExecutionData(testCaseIdentifier);
	}

	/** {@inheritDoc} */
	@Override
	protected void execTestcaseExecutedWithFailure(TestcaseIdentifier testCaseIdentifier, ITestRunResult testResult) {
		super.execTestcaseExecutedWithFailure(testCaseIdentifier, testResult);

		if (isIncludeFailingTests()) {
			recordTestcaseExecutionData(testCaseIdentifier);
		}
	}

	protected void recordTestcaseExecutionData(TestcaseIdentifier testCaseIdentifier) {
		Set<String> methodsUnderTest = getInvocationLogContent();

		for (String loggedMethodIdentifier : methodsUnderTest) {
			MethodIdentifier identifier = MethodIdentifier.parse(loggedMethodIdentifier);

			TestInformation testInformation = m_methodInformation.get(identifier);

			if (testInformation == null) {
				testInformation = new TestInformation(identifier);
				m_methodInformation.put(identifier, testInformation);
			}

			testInformation.addTestcase(testCaseIdentifier.resolveTestClassNoEx(),
					testCaseIdentifier.getTestcaseName());
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void execAllTestsExecuted(Map<Class<?>, Set<String>> testClassesWithTestcases) {
		Collection<TestInformation> result = m_methodInformation.values();

		LOGGER.info("Collected " + result.size() + " methods that are directly or indirectly invoked by testcases.");
		LOGGER.info("Collected " + countTestcases(result) + " successful testcases from "
				+ testClassesWithTestcases.size() + " test classes.");

		writeResultToFiles(result);
	}

	protected void writeResultToFiles(Collection<TestInformation> result) {
		IResultReceiver plainTextResultReceiver = ResultReceiverFactory
				.createFileResultReceiverWithDefaultSettings(isUseMultiFileOutput(), getOutputFile());
		IResultReceiver additionalResultResultReceiver = ResultReceiverFactory
				.createFileResultReceiverWithDefaultSettings(isUseMultiFileOutput(),
						getAdditionalResultOutputFile(getOutputFile()));

		CollectedInformationUtility.convertToParseableMethodTestcaseText(result, plainTextResultReceiver);
		CollectedInformationUtility.convertToMethodTestcaseMappingResult(result, getResultPresentation(),
				additionalResultResultReceiver);

		plainTextResultReceiver.markResultAsComplete();
		additionalResultResultReceiver.markResultAsComplete();
	}

	protected String getAdditionalResultOutputFile(String mainOutputFile) {
		String additionalResultOutputFile = mainOutputFile;

		if (mainOutputFile.endsWith(FileSystemConstants.FILE_EXTENSION_TXT)) {
			additionalResultOutputFile = additionalResultOutputFile.substring(0,
					additionalResultOutputFile.lastIndexOf(FileSystemConstants.FILE_EXTENSION_TXT));
		}

		additionalResultOutputFile += FileSystemConstants.FILE_EXTENSION_SQL_TXT;

		return additionalResultOutputFile;
	}

	protected void resetInvocationLog() {
		InvocationLogger.reset();
	}

	protected Set<String> getInvocationLogContent() {
		return InvocationLogger.getTestingLog();
	}

	protected int countTestcases(Collection<TestInformation> testInformationCollection) {
		Set<TestcaseIdentifier> testcaseIdentifiers = new HashSet<>();

		for (TestInformation t : testInformationCollection) {
			testcaseIdentifiers.addAll(t.getTestcases());
		}

		return testcaseIdentifiers.size();
	}
}
