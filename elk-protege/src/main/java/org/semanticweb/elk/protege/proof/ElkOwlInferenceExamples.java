package org.semanticweb.elk.protege.proof;

/*-
 * #%L
 * ELK Reasoner Protege Plug-in
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2017 Department of Computer Science, University of Oxford
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

import org.liveontologies.puli.Inference;
import org.semanticweb.elk.owl.inferences.ElkInferenceExamples;
import org.semanticweb.elk.owlapi.proofs.ElkOwlInference;
import org.semanticweb.owlapi.model.OWLAxiom;

public class ElkOwlInferenceExamples {

	public static Inference<? extends OWLAxiom> getExample(
			Inference<? extends OWLAxiom> inference) {
		if (inference instanceof ElkOwlInference) {
			return new ElkOwlInference(ElkInferenceExamples
					.getExample(((ElkOwlInference) inference).getInput()));

		}
		// else
		return null;
	}

}
