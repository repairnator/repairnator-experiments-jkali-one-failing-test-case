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

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.runner.RunWith;
import org.semanticweb.elk.ElkTestUtils;
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.reasoner.ElkReasoningTestDelegate;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
import org.semanticweb.elk.reasoner.taxonomy.ElkClassKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;
import org.semanticweb.elk.testing.ConfigurationUtils;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;

import com.google.common.collect.ImmutableMap;

import org.semanticweb.elk.testing.TestManifestWithOutput;
import org.semanticweb.elk.testing.TestUtils;

@RunWith(PolySuite.class)
public class ElkClassExpressionSuperClassesQueryTest extends
		BaseQueryTest<ElkClassExpression, RelatedEntitiesTestOutput<ElkClass>> {

	// @formatter:off
	static final String[] IGNORE_LIST = {
			ElkTestUtils.TEST_INPUT_LOCATION + "/query/class/Disjunctions.owl",// Disjuctions not supported
			ElkTestUtils.TEST_INPUT_LOCATION + "/query/class/OneOf.owl",// Disjuctions not supported
		};
	// @formatter:on

	static {
		Arrays.sort(IGNORE_LIST);
	}

	@Override
	protected boolean ignore(final QueryTestInput<ElkClassExpression> input) {
		return super.ignore(input) || TestUtils.ignore(input,
				ElkTestUtils.TEST_INPUT_LOCATION, IGNORE_LIST);
	}

	public ElkClassExpressionSuperClassesQueryTest(
			final QueryTestManifest<ElkClassExpression, RelatedEntitiesTestOutput<ElkClass>> manifest) {
		super(manifest,
				new ElkReasoningTestDelegate<RelatedEntitiesTestOutput<ElkClass>>(
						manifest) {

					@Override
					public RelatedEntitiesTestOutput<ElkClass> getActualOutput()
							throws Exception {
						final Set<? extends Node<ElkClass>> subNodes = getReasoner()
								.getSuperClassesQuietly(
										manifest.getInput().getQuery(), true);
						return new ElkRelatedEntitiesTestOutput<ElkClass>(
								subNodes, ElkClassKeyProvider.INSTANCE);
					}

					@Override
					protected Map<String, String> additionalConfigWithOutput() {
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

	@Config
	public static Configuration getConfig()
			throws IOException, URISyntaxException {

		return ConfigurationUtils.loadFileBasedTestConfiguration(
				ElkTestUtils.TEST_INPUT_LOCATION, BaseQueryTest.class,
				new ConfigurationUtils.ManifestCreator<TestManifestWithOutput<QueryTestInput<ElkClassExpression>, RelatedEntitiesTestOutput<ElkClass>>>() {

					@Override
					public Collection<? extends TestManifestWithOutput<QueryTestInput<ElkClassExpression>, RelatedEntitiesTestOutput<ElkClass>>> createManifests(
							final String name, final List<URL> urls)
							throws IOException {

						if (urls == null || urls.size() < 2) {
							// Not enough inputs. Probably forgot something.
							throw new IllegalArgumentException(
									"Need at least 2 URL-s!");
						}
						if (urls.get(0) == null || urls.get(1) == null) {
							// No inputs, no manifests.
							return Collections.emptySet();
						}

						InputStream outputIS = null;
						try {
							outputIS = urls.get(1).openStream();

							return ElkExpectedTestOutputLoader.load(outputIS)
									.getSuperEntitiesManifests(name,
											urls.get(0));

						} finally {
							IOUtils.closeQuietly(outputIS);
						}

					}

				}, "owl", "classquery");

	}

}
