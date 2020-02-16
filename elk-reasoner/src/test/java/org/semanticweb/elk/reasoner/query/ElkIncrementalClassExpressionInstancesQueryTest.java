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
package org.semanticweb.elk.reasoner.query;

import java.util.Map;
import java.util.Set;

import org.junit.runner.RunWith;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
import org.semanticweb.elk.reasoner.incremental.ElkIncrementalReasoningTestDelegate;
import org.semanticweb.elk.reasoner.taxonomy.ElkIndividualKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.TestManifest;

import com.google.common.collect.ImmutableMap;

@RunWith(PolySuite.class)
public class ElkIncrementalClassExpressionInstancesQueryTest extends
		ElkIncrementalClassExpressionQueryTest<RelatedEntitiesTestOutput<ElkNamedIndividual>> {

	public ElkIncrementalClassExpressionInstancesQueryTest(
			final TestManifest<QueryTestInput<ElkClassExpression>> manifest) {
		super(manifest,
				new ElkIncrementalReasoningTestDelegate<RelatedEntitiesTestOutput<ElkNamedIndividual>>(
						manifest) {

					@Override
					public RelatedEntitiesTestOutput<ElkNamedIndividual> getExpectedOutput()
							throws Exception {
						final Set<? extends Node<ElkNamedIndividual>> subNodes = getStandardReasoner()
								.getInstancesQuietly(
										manifest.getInput().getQuery(),
										true);
						return new ElkRelatedEntitiesTestOutput<ElkNamedIndividual>(
								subNodes, ElkIndividualKeyProvider.INSTANCE);
					}

					@Override
					public RelatedEntitiesTestOutput<ElkNamedIndividual> getActualOutput()
							throws Exception {
						final Set<? extends Node<ElkNamedIndividual>> subNodes = getIncrementalReasoner()
								.getInstancesQuietly(
										manifest.getInput().getQuery(),
										true);
						return new ElkRelatedEntitiesTestOutput<ElkNamedIndividual>(
								subNodes, ElkIndividualKeyProvider.INSTANCE);
					}

					@Override
					protected Map<String, String> additionalConfigIncremental() {
						return ImmutableMap.<String, String> builder()
								.put(ReasonerConfiguration.CLASS_EXPRESSION_QUERY_EVICTOR,
										"NQEvictor(0, 0.75)")
								.build();
					}

					@Override
					protected Map<String, String> additionalConfigWithInterrupts() {
						return ImmutableMap.<String, String> builder()
								.put(ReasonerConfiguration.CLASS_EXPRESSION_QUERY_EVICTOR,
										"NQEvictor(0, 0.75)")
								.build();
					}

				});
	}

}
