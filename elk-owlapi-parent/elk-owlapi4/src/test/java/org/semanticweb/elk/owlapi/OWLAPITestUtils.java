/*
 * #%L
 * ELK OWL API Binding
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
/**
 * 
 */
package org.semanticweb.elk.owlapi;

import org.semanticweb.elk.reasoner.FailingReasonerInterrupter;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.ReasonerInterrupter;
import org.semanticweb.elk.reasoner.TestReasonerUtils;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * A collection of utility methods to be used in OWL API related tests
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author Peter Skocovsky
 */
public class OWLAPITestUtils {

	static ElkReasoner createReasoner(final OWLOntology ontology,
			final boolean isBufferingMode,
			final ElkReasonerConfiguration elkConfig,
			final Reasoner internalReasoner) {
		return new ElkReasoner(ontology, isBufferingMode, elkConfig,
				internalReasoner);
	}

	public static ElkReasoner createReasoner(final OWLOntology ontology,
			final boolean isBufferingMode,
			final ReasonerInterrupter interrupter) {
		final ElkReasonerConfiguration elkConfig = new ElkReasonerConfiguration();
		final Reasoner internalReasoner = TestReasonerUtils.createTestReasoner(
				interrupter, elkConfig.getElkConfiguration());
		return createReasoner(ontology, isBufferingMode, elkConfig,
				internalReasoner);
	}

	/**
	 * Created a reasoner that fails on interrupt.
	 * 
	 * @param ontology
	 * @param isBufferingMode
	 * @return
	 */
	public static ElkReasoner createReasoner(final OWLOntology ontology,
			final boolean isBufferingMode) {
		return createReasoner(ontology, isBufferingMode,
				FailingReasonerInterrupter.INSTANCE);
	}

	/**
	 * Created a reasoner that fails on interrupt.
	 * 
	 * @param ontology
	 * @return
	 */
	public static ElkReasoner createReasoner(final OWLOntology ontology) {
		return createReasoner(ontology, false,
				FailingReasonerInterrupter.INSTANCE);
	}

	public static ElkProver createProver(ElkReasoner reasoner) {
		return new ElkProver(reasoner);
	}

	public static ElkProver createProver(OWLOntology ontology) {
		return createProver(createReasoner(ontology));
	}

}
