package de.tum.in.niedermr.ta.runner.analysis;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.tum.in.niedermr.ta.core.analysis.result.presentation.IResultPresentation;
import de.tum.in.niedermr.ta.core.code.identifier.MethodIdentifier;
import de.tum.in.niedermr.ta.core.code.identifier.TestcaseIdentifier;
import de.tum.in.niedermr.ta.core.code.tests.runner.ITestRunResult;
import de.tum.in.niedermr.ta.core.code.tests.runner.ITestRunner;
import de.tum.in.niedermr.ta.core.code.util.JavaUtility;
import de.tum.in.niedermr.ta.core.common.io.TextFileUtility;
import de.tum.in.niedermr.ta.core.execution.id.IFullExecutionId;
import de.tum.in.niedermr.ta.runner.analysis.result.presentation.ResultPresentationUtil;
import de.tum.in.niedermr.ta.runner.execution.args.ProgramArgsKey;
import de.tum.in.niedermr.ta.runner.execution.args.ProgramArgsReader;
import de.tum.in.niedermr.ta.runner.execution.args.ProgramArgsWriter;
import de.tum.in.niedermr.ta.runner.execution.exceptions.ExecutionException;
import de.tum.in.niedermr.ta.runner.execution.id.ExecutionIdFactory;
import de.tum.in.niedermr.ta.runner.logging.LoggingConstants;
import de.tum.in.niedermr.ta.runner.logging.LoggingUtil;
import de.tum.in.niedermr.ta.runner.start.AnalyzerRunnerStart;

/**
 * <b>TSTRUN:</b> <b>Runs the specified testcases</b> and records the results.<br/>
 * <br/>
 * Dependencies: ASM, log4j, core.<br/>
 * Further classpath entries: jars to be processed and dependencies.
 *
 */
public class TestRun {
	/** Logger. */
	private static final Logger LOGGER = LogManager.getLogger(TestRun.class);

	/** Number of args. */
	private static final int ARGS_COUNT = 7;
	public static final ProgramArgsKey ARGS_EXECUTION_ID = new ProgramArgsKey(TestRun.class, 0);
	public static final ProgramArgsKey ARGS_FILE_WITH_TESTS_TO_RUN = new ProgramArgsKey(TestRun.class, 1);
	public static final ProgramArgsKey ARGS_FILE_WITH_RESULTS = new ProgramArgsKey(TestRun.class, 2);
	public static final ProgramArgsKey ARGS_MUTATED_METHOD_IDENTIFIER = new ProgramArgsKey(TestRun.class, 3);
	public static final ProgramArgsKey ARGS_TEST_RUNNER_CLASS = new ProgramArgsKey(TestRun.class, 4);
	public static final ProgramArgsKey ARGS_RETURN_VALUE_GENERATOR_CLASS = new ProgramArgsKey(TestRun.class, 5);
	/** Result presentation: 'TEXT', 'DB' or the name of a class implementing {@link IResultPresentation}. */
	public static final ProgramArgsKey ARGS_RESULT_PRESENTATION = new ProgramArgsKey(TestRun.class, 6);

	/**
	 * Main method. Note that the jar file of the class to be tested must be on the classpath!
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		if (args.length == 0) {
			LoggingUtil.printDontStartThisClass(TestRun.class, AnalyzerRunnerStart.class);
			return;
		}

		ProgramArgsReader argsReader = new ProgramArgsReader(TestRun.class, args);

		IFullExecutionId executionId = ExecutionIdFactory
				.parseFullExecutionId(argsReader.getArgument(ARGS_EXECUTION_ID));
		LOGGER.info(LoggingConstants.EXECUTION_ID_TEXT + executionId.get());
		LOGGER.info(LoggingUtil.getInputArgumentsF1(argsReader));

		try {
			final String fileWithTestsToRun = argsReader.getArgument(ARGS_FILE_WITH_TESTS_TO_RUN);
			final String fileWithResults = argsReader.getArgument(ARGS_FILE_WITH_RESULTS);
			final MethodIdentifier mutatedMethod = MethodIdentifier
					.parse(argsReader.getArgument(ARGS_MUTATED_METHOD_IDENTIFIER));
			final ITestRunner testRunner = JavaUtility.createInstance(argsReader.getArgument(ARGS_TEST_RUNNER_CLASS));
			final String usedReturnValueGenerator = argsReader.getArgument(ARGS_RETURN_VALUE_GENERATOR_CLASS);
			final String resultPresentationChoice = argsReader.getArgument(ARGS_RESULT_PRESENTATION);

			final IResultPresentation resultPresentation = ResultPresentationUtil
					.createResultPresentation(resultPresentationChoice, executionId);

			List<String> result = runTestsFromFile(executionId, testRunner, fileWithTestsToRun, resultPresentation,
					mutatedMethod, usedReturnValueGenerator);

			TextFileUtility.appendToFile(fileWithResults, result);

			System.exit(0);
		} catch (Throwable t) {
			LOGGER.error("Failed execution " + executionId.get(), t);
			throw new ExecutionException(executionId, t);
		}
	}

	public static ProgramArgsWriter createProgramArgsWriter() {
		return new ProgramArgsWriter(TestRun.class, ARGS_COUNT);
	}

	private static List<String> runTestsFromFile(IFullExecutionId executionId, ITestRunner testRunner,
			String fileWithTestsToRun, IResultPresentation resultPresentation, MethodIdentifier methodUnderTest,
			String usedReturnValueGenerator) throws IOException, ReflectiveOperationException {
		List<TestcaseIdentifier> allTestsToRun = parseTestcasesToRun(TextFileUtility.readFromFile(fileWithTestsToRun));
		List<String> result = new LinkedList<>();

		for (TestcaseIdentifier testcase : allTestsToRun) {
			ITestRunResult testResult = testRunner.runTest(testcase.resolveTestClass(), testcase.getTestcaseName());

			result.add(resultPresentation.formatTestResultEntry(testcase, testResult, methodUnderTest,
					usedReturnValueGenerator));
		}

		LOGGER.info(executionId.get() + ": " + allTestsToRun.size() + " testcases ran successfully.");

		return result;
	}

	private static List<TestcaseIdentifier> parseTestcasesToRun(List<String> testcasesFromFile) {
		List<TestcaseIdentifier> result = new LinkedList<>();

		for (String testcaseString : testcasesFromFile) {
			result.add(TestcaseIdentifier.parse(testcaseString));
		}

		return result;
	}
}
