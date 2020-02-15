package de.tum.in.niedermr.ta.extensions.analysis.workflows.stackdistance;

import de.tum.in.niedermr.ta.extensions.analysis.workflows.stackdistance.steps.AnalysisInformationCollectorStep;
import de.tum.in.niedermr.ta.extensions.analysis.workflows.stackdistance.steps.AnalysisInstrumentationStep;
import de.tum.in.niedermr.ta.runner.analysis.workflow.AbstractWorkflow;
import de.tum.in.niedermr.ta.runner.analysis.workflow.common.CleanupStep;
import de.tum.in.niedermr.ta.runner.analysis.workflow.common.PrepareWorkingFolderStep;
import de.tum.in.niedermr.ta.runner.configuration.Configuration;
import de.tum.in.niedermr.ta.runner.configuration.extension.DynamicConfigurationKey;
import de.tum.in.niedermr.ta.runner.configuration.extension.DynamicConfigurationKeyNamespace;
import de.tum.in.niedermr.ta.runner.execution.ExecutionContext;
import de.tum.in.niedermr.ta.runner.execution.exceptions.ExecutionException;
import de.tum.in.niedermr.ta.runner.execution.infocollection.IInformationCollectionLogic;

/**
 * Computes the minimum and maximum distance on the call stack between test case
 * and method.
 */
public abstract class AbstractStackDistanceAnalysisWorkflow extends AbstractWorkflow {

	/**
	 * <code>extension.stackdistance.useMultipleOutputFiles</code>: Split the
	 * output into multiple files.
	 */
	public static final DynamicConfigurationKey CONFIGURATION_KEY_USE_MULTIPLE_OUTPUT_FILES = DynamicConfigurationKey
			.create(DynamicConfigurationKeyNamespace.EXTENSION, "stackdistance.useMultipleOutputFiles", false);

	/**
	 * <code>extension.stackdistance.includeFailingTests</code>: Whether to
	 * include the distance of failing test cases.
	 */
	public static final DynamicConfigurationKey CONFIGURATION_KEY_INCLUDE_FAILING_TESTS = DynamicConfigurationKey
			.create(DynamicConfigurationKeyNamespace.EXTENSION, "stackdistance.includeFailingTests", false);

	/** {@inheritDoc} */
	@Override
	protected void startInternal(ExecutionContext context, Configuration configuration) throws ExecutionException {
		PrepareWorkingFolderStep prepareStep = createAndInitializeExecutionStep(PrepareWorkingFolderStep.class);
		AnalysisInstrumentationStep analysisInstrumentationStep = createAnalysisInstrumentationStep();
		AnalysisInformationCollectorStep analysisInformationCollectorStep = createAnalysisInformationCollectorStep();
		CleanupStep cleanupStep = createAndInitializeExecutionStep(CleanupStep.class);

		analysisInformationCollectorStep.setUseMultiFileOutput(
				configuration.getDynamicValues().getBooleanValue(CONFIGURATION_KEY_USE_MULTIPLE_OUTPUT_FILES));

		analysisInformationCollectorStep.setIncludeFailingTestcases(
				configuration.getDynamicValues().getBooleanValue(CONFIGURATION_KEY_INCLUDE_FAILING_TESTS));

		prepareStep.start();
		analysisInstrumentationStep.start();
		analysisInformationCollectorStep.start();
		cleanupStep.start();
	}

	/**
	 * Create an appropriate instance of {@link AnalysisInstrumentationStep}.
	 */
	protected AnalysisInstrumentationStep createAnalysisInstrumentationStep() {
		AnalysisInstrumentationStep step = createAndInitializeExecutionStep(AnalysisInstrumentationStep.class);
		step.setStackLogRecorderClass(getStackLogRecorderClass());
		return step;
	}

	/**
	 * Create an appropriate instance of
	 * {@link AnalysisInformationCollectorStep}.
	 */
	protected AnalysisInformationCollectorStep createAnalysisInformationCollectorStep() {
		AnalysisInformationCollectorStep step = createAndInitializeExecutionStep(
				AnalysisInformationCollectorStep.class);
		step.setResultOutputFile(getResultOutputFile());
		step.setInformationCollectorLogicClass(getInformationCollectorLogicClass());
		return step;
	}

	protected abstract Class<?> getStackLogRecorderClass();

	protected abstract Class<? extends IInformationCollectionLogic> getInformationCollectorLogicClass();

	protected abstract String getResultOutputFile();
}
