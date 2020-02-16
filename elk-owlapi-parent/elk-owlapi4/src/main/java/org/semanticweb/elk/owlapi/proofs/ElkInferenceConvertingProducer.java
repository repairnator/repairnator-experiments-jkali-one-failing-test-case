/*-
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

import org.liveontologies.puli.Inference;
import org.liveontologies.puli.Producer;
import org.semanticweb.elk.owl.inferences.ElkInference;
import org.semanticweb.elk.owl.inferences.ElkInferenceProducer;
import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * Converts the produced {@link ElkInference}s to {@link OWLAxiom}
 * {@link Inference}s and passes them to the provided {@link Producer}
 * 
 * @author Yevgeny Kazakov
 */
public class ElkInferenceConvertingProducer implements ElkInferenceProducer {

	private final Producer<ElkOwlInference> targetProducer_;

	public ElkInferenceConvertingProducer(
			final Producer<ElkOwlInference> targetProducer) {
		this.targetProducer_ = targetProducer;
	}

	@Override
	public void produce(ElkInference inference) {
		targetProducer_.produce(new ElkOwlInference(inference));
	}

}
