package de.tum.in.niedermr.ta.runner.analysis.workflow.steps.testworkflow;

import de.tum.in.niedermr.ta.core.code.tests.runner.ITestRunner;
import de.tum.in.niedermr.ta.core.execution.id.IFullExecutionId;
import de.tum.in.niedermr.ta.runner.analysis.instrumentation.source.SourceInstrumentation;
import de.tum.in.niedermr.ta.runner.analysis.instrumentation.test.TestInstrumentation;
import de.tum.in.niedermr.ta.runner.analysis.workflow.steps.AbstractExecutionStep;
import de.tum.in.niedermr.ta.runner.configuration.Configuration;
import de.tum.in.niedermr.ta.runner.execution.ProcessExecution;
import de.tum.in.niedermr.ta.runner.execution.exceptions.ExecutionException;

/**
 * Step to instrument the methods of the jars to be mutated in order to find the
 * methods under test to be mutated.
 */
public class InstrumentationStep extends AbstractExecutionStep {

	/** {@inheritDoc} */
	@Override
	protected String getSuffixForFullExecutionId() {
		return "INSTRU";
	}

	/** {@inheritDoc} */
	@Override
	public void runInternal(Configuration configuration, ProcessExecution processExecution)
			throws ExecutionException, ReflectiveOperationException {
		IFullExecutionId executionId = createFullExecutionId();
		final boolean operateFaultTolerant = configuration.getOperateFaultTolerant().getValue();
		ITestRunner testRunner = configuration.getTestRunner().createInstance();

		SourceInstrumentation sourceInstrumentation = new SourceInstrumentation(executionId, operateFaultTolerant);
		sourceInstrumentation.injectLoggingStatements(configuration.getCodePathToMutate().getElements(),
				getFileInWorkingArea(FILE_TEMP_JAR_INSTRUMENTED_SOURCE_X), testRunner,
				configuration.getTestClassIncludes().getElements(), configuration.getTestClassExcludes().getElements());

		TestInstrumentation testInstrumentation = new TestInstrumentation(executionId, operateFaultTolerant);
		testInstrumentation.injectTestingModeStatements(configuration.getCodePathToTest().getElements(),
				getFileInWorkingArea(FILE_TEMP_JAR_INSTRUMENTED_TEST_X), testRunner,
				configuration.getTestClassIncludes().getElements(), configuration.getTestClassExcludes().getElements());
	}

	/** {@inheritDoc} */
	@Override
	protected String getDescription() {
		return "Instrumenting jar file";
	}
}
