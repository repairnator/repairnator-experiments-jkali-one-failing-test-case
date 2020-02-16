/*
 * #%L
 * ELK OWL API Binding
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.owlapi.proofs;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.junit.runner.RunWith;
import org.semanticweb.elk.ElkTestUtils;
import org.semanticweb.elk.owlapi.ElkProver;
import org.semanticweb.elk.owlapi.EntailmentTestManifestCreator;
import org.semanticweb.elk.owlapi.OwlApiReasoningTestDelegate;
import org.semanticweb.elk.reasoner.query.BaseQueryTest;
import org.semanticweb.elk.reasoner.query.QueryTestInput;
import org.semanticweb.elk.reasoner.query.QueryTestManifest;
import org.semanticweb.elk.testing.ConfigurationUtils;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.TestUtils;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.reasoner.ReasonerInterruptedException;

@RunWith(PolySuite.class)
public class EntailmentProofTest extends BaseQueryTest<OWLAxiom, Boolean> {

	// @formatter:off
	static final String[] IGNORE_LIST = {
			ElkTestUtils.TEST_INPUT_LOCATION + "/query/entailment/AssertionRanges.owl",// Ranges not supported with assertions
			ElkTestUtils.TEST_INPUT_LOCATION + "/query/entailment/HasValueRanges.owl",// Ranges not supported with ObjectHasValue
		};
	// @formatter:on

	static {
		Arrays.sort(IGNORE_LIST);
	}

	@Override
	protected boolean ignore(final QueryTestInput<OWLAxiom> input) {
		return super.ignore(input) || TestUtils.ignore(input,
				ElkTestUtils.TEST_INPUT_LOCATION, IGNORE_LIST);
	}

	public EntailmentProofTest(
			final QueryTestManifest<OWLAxiom, Boolean> manifest) {
		super(manifest, new OwlApiReasoningTestDelegate<Boolean>(manifest) {

			@Override
			public Boolean getActualOutput() throws Exception {

				final ElkProver prover = getProver();

				final OWLAxiom axiom = manifest.getInput().getQuery();

				ProofTestUtils.provabilityTest(prover, axiom);

				return true;
			}

			@Override
			public Class<? extends Exception> getInterruptionExceptionClass() {
				return ReasonerInterruptedException.class;
			}

		});
	}

	@Config
	public static Configuration getConfig()
			throws IOException, URISyntaxException {

		return ConfigurationUtils.loadFileBasedTestConfiguration(
				ElkTestUtils.TEST_INPUT_LOCATION, BaseQueryTest.class,
				EntailmentTestManifestCreator.INSTANCE, "owl", "entailed");

	}

}
