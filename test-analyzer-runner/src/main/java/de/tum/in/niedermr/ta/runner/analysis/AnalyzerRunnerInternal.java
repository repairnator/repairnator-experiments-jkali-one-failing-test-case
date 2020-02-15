package de.tum.in.niedermr.ta.runner.analysis;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.tum.in.niedermr.ta.core.analysis.result.presentation.IResultPresentation;
import de.tum.in.niedermr.ta.core.common.constants.CommonConstants;
import de.tum.in.niedermr.ta.core.common.constants.FileSystemConstants;
import de.tum.in.niedermr.ta.core.common.io.TextFileUtility;
import de.tum.in.niedermr.ta.core.common.util.ClasspathUtility;
import de.tum.in.niedermr.ta.core.common.util.CommonUtility;
import de.tum.in.niedermr.ta.core.execution.id.IExecutionId;
import de.tum.in.niedermr.ta.runner.analysis.workflow.IWorkflow;
import de.tum.in.niedermr.ta.runner.analysis.workflow.common.PrepareWorkingFolderStep;
import de.tum.in.niedermr.ta.runner.configuration.Configuration;
import de.tum.in.niedermr.ta.runner.configuration.ConfigurationManager;
import de.tum.in.niedermr.ta.runner.configuration.exceptions.ConfigurationException;
import de.tum.in.niedermr.ta.runner.execution.ExecutionContext;
import de.tum.in.niedermr.ta.runner.execution.args.ProgramArgsKey;
import de.tum.in.niedermr.ta.runner.execution.args.ProgramArgsReader;
import de.tum.in.niedermr.ta.runner.execution.args.ProgramArgsWriter;
import de.tum.in.niedermr.ta.runner.execution.environment.Environment;
import de.tum.in.niedermr.ta.runner.execution.environment.EnvironmentConstants;
import de.tum.in.niedermr.ta.runner.execution.exceptions.ExecutionException;
import de.tum.in.niedermr.ta.runner.execution.id.ExecutionIdFactory;
import de.tum.in.niedermr.ta.runner.factory.FactoryUtil;
import de.tum.in.niedermr.ta.runner.factory.IFactory;
import de.tum.in.niedermr.ta.runner.logging.LoggingUtil;
import de.tum.in.niedermr.ta.runner.start.AnalyzerRunnerStart;

/**
 * Starts the workflows. <br/>
 * Further dependencies: jars to be processed and dependencies
 */
public class AnalyzerRunnerInternal {
	/** Logger. */
	private static final Logger LOGGER = LogManager.getLogger(AnalyzerRunnerInternal.class);

	/** Number of args. */
	private static final int ARGS_COUNT = 3;
	public static final ProgramArgsKey ARGS_EXECUTION_ID = new ProgramArgsKey(AnalyzerRunnerInternal.class, 0);
	public static final ProgramArgsKey ARGS_PROGRAM_PATH = new ProgramArgsKey(AnalyzerRunnerInternal.class, 1);
	public static final ProgramArgsKey ARGS_CONFIG_FILE = new ProgramArgsKey(AnalyzerRunnerInternal.class, 2);

	private static final String RELATIVE_WORKING_FOLDER = FileSystemConstants.CURRENT_FOLDER;

	/** Main method. */
	public static void main(String[] args) {
		if (args.length == 0) {
			LoggingUtil.printDontStartThisClass(AnalyzerRunnerInternal.class, AnalyzerRunnerStart.class);
			return;
		}

		ProgramArgsReader argsReader = new ProgramArgsReader(AnalyzerRunnerInternal.class, args);

		final IExecutionId executionId = ExecutionIdFactory
				.parseShortExecutionId(argsReader.getArgument(ARGS_EXECUTION_ID));
		final String programPath = argsReader.getArgument(ARGS_PROGRAM_PATH);
		final String configurationFileToUse = Environment.replaceWorkingFolder(argsReader.getArgument(ARGS_CONFIG_FILE),
				RELATIVE_WORKING_FOLDER);

		try {
			LOGGER.info("TEST ANALYZER START");
			LOGGER.info("Classpath: " + ClasspathUtility.getCurrentClasspath());

			Configuration configuration = loadAndValidateTheConfiguration(configurationFileToUse);

			LOGGER.info("Configuration is valid.");
			LOGGER.info("Configuration is:" + CommonConstants.NEW_LINE + configuration.toMultiLineString());

			prepareWorkingDirectory(executionId, configuration, programPath);
			// must be done before starting the workflows because the steps may
			// append information to the file
			writeExecutionInformationFile(executionId, configuration);

			IWorkflow[] workflows = configuration.getWorkflows().createInstances();

			for (IWorkflow workFlow : workflows) {
				executeWorkflow(executionId, programPath, configuration, workFlow);
			}

			LOGGER.info("TEST ANALYZER END");
		} catch (Throwable t) {
			t.printStackTrace();
			LOGGER.fatal("Execution failed", t);
			throw new ExecutionException(executionId, AnalyzerRunnerInternal.class.getName() + " was not successful.");
		}
	}

	/** Prepare the working directory before running workflows. */
	private static void prepareWorkingDirectory(IExecutionId executionId, Configuration configuration,
			String programPath) {
		ExecutionContext executionContext = createExecutionContext(executionId, configuration, programPath);
		PrepareWorkingFolderStep prepareWorkingFolderStep = new PrepareWorkingFolderStep();
		prepareWorkingFolderStep.initialize(executionContext);
		prepareWorkingFolderStep.start();
	}

	/** Write a file with execution information. */
	private static void writeExecutionInformationFile(IExecutionId executionId, Configuration configuration)
			throws ReflectiveOperationException, IOException {
		IResultPresentation resultPresentation = configuration.getResultPresentation().createInstance(executionId);

		String fileName = Environment.replaceWorkingFolder(EnvironmentConstants.FILE_OUTPUT_EXECUTION_INFORMATION,
				RELATIVE_WORKING_FOLDER);
		List<String> configurationLines = ConfigurationManager.toFileLines(configuration, false);
		List<String> formattedContent = Arrays
				.asList(resultPresentation.formatExecutionInformation(configurationLines));
		TextFileUtility.writeToFile(fileName, formattedContent);
	}

	/** Execute the given workflow. */
	private static void executeWorkflow(IExecutionId executionId, String programPath, Configuration configuration,
			IWorkflow workFlow) {
		LOGGER.info("WORKFLOW " + workFlow.getName() + " START (" + new Date() + ")");
		long startTime = System.nanoTime();

		IWorkflow workflow = initializeWorkflow(executionId, workFlow, configuration, programPath);
		workflow.start();

		LOGGER.info("Workflow execution id was: '" + executionId.get() + "'");
		LOGGER.info("Workflow duration was: " + CommonUtility.getDurationInSec(startTime, TimeUnit.NANOSECONDS)
				+ " seconds");
		LOGGER.info("WORKFLOW " + workFlow.getName() + " END (" + new Date() + ")");
	}

	public static ProgramArgsWriter createProgramArgsWriter() {
		return new ProgramArgsWriter(AnalyzerRunnerInternal.class, ARGS_COUNT);
	}

	private static Configuration loadAndValidateTheConfiguration(String configurationFileToUse)
			throws ConfigurationException, IOException {
		Configuration configuration = ConfigurationManager.loadConfigurationFromFile(configurationFileToUse);

		final String classpathBefore = configuration.getClasspath().getValue();

		configuration.validateAndAdjust();

		if (!configuration.getClasspath().getValue().equals(classpathBefore)) {
			LOGGER.warn("Fixed the classpath of the configuration: removed elements of "
					+ configuration.getCodePathToMutate().getName() + " from " + configuration.getClasspath().getName()
					+ "!");
		}

		return configuration;
	}

	/** Initialize a workflow. */
	private static IWorkflow initializeWorkflow(IExecutionId executionId, IWorkflow workflow,
			Configuration configuration, String programPath) {
		try {
			// create a new context instance for each workflow
			ExecutionContext executionContext = createExecutionContext(executionId, configuration, programPath);
			workflow.initWorkflow(executionContext);

			return workflow;
		} catch (Throwable t) {
			throw new ExecutionException(executionId, "Error when initializing the test workflow");
		}
	}

	/** Create the execution context for a workflow. */
	private static ExecutionContext createExecutionContext(IExecutionId executionId, Configuration configuration,
			String programPath) {
		IFactory factory = FactoryUtil.createFactory(configuration);
		return factory.createNewExecutionContext(executionId, configuration, programPath, RELATIVE_WORKING_FOLDER);
	}
}
