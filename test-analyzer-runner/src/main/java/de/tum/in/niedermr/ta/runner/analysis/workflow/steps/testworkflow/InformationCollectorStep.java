package de.tum.in.niedermr.ta.runner.analysis.workflow.steps.testworkflow;

import de.tum.in.niedermr.ta.runner.analysis.workflow.common.AbstractInformationCollectorStep;
import de.tum.in.niedermr.ta.runner.configuration.Configuration;
import de.tum.in.niedermr.ta.runner.execution.environment.Environment;
import de.tum.in.niedermr.ta.runner.execution.infocollection.IInformationCollectionLogic;
import de.tum.in.niedermr.ta.runner.execution.infocollection.InformationCollectionLogic;

/** Information collector step. */
public class InformationCollectorStep extends AbstractInformationCollectorStep {

	/** {@inheritDoc} */
	@Override
	protected String getSuffixForFullExecutionId() {
		return "INFCOL";
	}

	/** {@inheritDoc} */
	@Override
	protected String getDescription() {
		return "Loading information about the testcases and the methods they are invoking";
	}

	/** {@inheritDoc} */
	@Override
	protected String getFileWithResultsParameterValue() {
		return getFileInWorkingArea(FILE_OUTPUT_COLLECTED_INFORMATION);
	}

	/** {@inheritDoc} */
	@Override
	protected String getSourceInstrumentedJarFilesClasspath(Configuration configuration) {
		return Environment.getClasspathOfIndexedFiles(getFileInWorkingArea(FILE_TEMP_JAR_INSTRUMENTED_SOURCE_X), 0,
				configuration.getCodePathToMutate().countElements());
	}

	/** {@inheritDoc} */
	@Override
	protected String getTestInstrumentedJarFilesClasspath(Configuration configuration) {
		return Environment.getClasspathOfIndexedFiles(getFileInWorkingArea(FILE_TEMP_JAR_INSTRUMENTED_TEST_X), 0,
				configuration.getCodePathToTest().countElements());
	}

	/** {@inheritDoc} */
	@Override
	protected Class<? extends IInformationCollectionLogic> getInformationCollectorLogicClass() {
		return InformationCollectionLogic.class;
	}

	/** {@inheritDoc} */
	@Override
	protected boolean isIncludeFailingTestcases() {
		return false;
	}
}
