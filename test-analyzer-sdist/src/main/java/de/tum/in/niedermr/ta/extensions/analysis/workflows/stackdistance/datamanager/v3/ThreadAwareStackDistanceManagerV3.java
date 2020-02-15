package de.tum.in.niedermr.ta.extensions.analysis.workflows.stackdistance.datamanager.v3;

import de.tum.in.niedermr.ta.core.code.identifier.TestcaseIdentifier;
import de.tum.in.niedermr.ta.extensions.analysis.workflows.stackdistance.datamanager.AbstractThreadAwareStackDistanceManager;
import de.tum.in.niedermr.ta.extensions.analysis.workflows.stackdistance.datamanager.threading.ThreadStackManager;
import de.tum.in.niedermr.ta.extensions.analysis.workflows.stackdistance.recording.v3.StackLogRecorderV3;

public class ThreadAwareStackDistanceManagerV3 extends AbstractThreadAwareStackDistanceManager {

	/** {@inheritDoc} */
	@Override
	protected void execSetThreadStackManagerAndVerify(ThreadStackManager stackManager) {
		StackLogRecorderV3.setThreadStackManagerAndVerify(stackManager);
	}

	/** {@inheritDoc} */
	@Override
	public void startStackLogger(TestcaseIdentifier testcaseIdentifier) {
		StackLogRecorderV3.startLog(testcaseIdentifier);
	}
}
