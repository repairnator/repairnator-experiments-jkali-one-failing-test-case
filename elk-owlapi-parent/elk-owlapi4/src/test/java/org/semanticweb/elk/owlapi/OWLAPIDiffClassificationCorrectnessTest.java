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
package org.semanticweb.elk.owlapi;

import java.util.Arrays;

import org.junit.runner.RunWith;
import org.semanticweb.elk.ElkTestUtils;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.reasoner.BaseClassificationCorrectnessTest;
import org.semanticweb.elk.reasoner.ReasoningTestManifest;
import org.semanticweb.elk.reasoner.TaxonomyTestOutput;
import org.semanticweb.elk.reasoner.stages.ElkInterruptedException;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.TestUtils;
import org.semanticweb.elk.testing.UrlTestInput;

@RunWith(PolySuite.class)
public class OWLAPIDiffClassificationCorrectnessTest
		extends BaseClassificationCorrectnessTest {

	// @formatter:off
	static final String[] IGNORE_LIST = {
			ElkTestUtils.TEST_INPUT_LOCATION + "/classification/DisjointSelf.owl",
			ElkTestUtils.TEST_INPUT_LOCATION + "/classification/CompositionReflexivityComplex.owl",
			ElkTestUtils.TEST_INPUT_LOCATION + "/classification/BottomObjectProperty.owl",
			ElkTestUtils.TEST_INPUT_LOCATION + "/classification/TopObjectProperty.owl",
		};
	// @formatter:on

	static {
		Arrays.sort(IGNORE_LIST);
	}

	public OWLAPIDiffClassificationCorrectnessTest(
			final ReasoningTestManifest<TaxonomyTestOutput<?>> testManifest) {
		super(testManifest,
				new OwlApiReasoningTestDelegate<TaxonomyTestOutput<?>>(
						testManifest) {

					@Override
					public TaxonomyTestOutput<?> getActualOutput()
							throws Exception {
						final Taxonomy<ElkClass> taxonomy = getReasoner()
								.getInternalReasoner().getTaxonomyQuietly();
						return new TaxonomyTestOutput<Taxonomy<ElkClass>>(
								taxonomy);
					}

					@Override
					public Class<? extends Exception> getInterruptionExceptionClass() {
						return ElkInterruptedException.class;
					}

				});
	}

	@Override
	protected boolean ignore(final UrlTestInput input) {
		return super.ignore(input) || TestUtils.ignore(input,
				ElkTestUtils.TEST_INPUT_LOCATION, IGNORE_LIST);
	}

}
