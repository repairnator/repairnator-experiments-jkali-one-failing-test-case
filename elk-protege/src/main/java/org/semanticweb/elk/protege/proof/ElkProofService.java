/*-
 * #%L
 * ELK Reasoner Protege Plug-in
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
package org.semanticweb.elk.protege.proof;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.liveontologies.protege.explanation.proof.service.ProofService;
import org.liveontologies.puli.DynamicProof;
import org.liveontologies.puli.Inference;
import org.liveontologies.puli.ProofNode;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.semanticweb.elk.owlapi.ElkReasoner;
import org.semanticweb.elk.owlapi.proofs.ElkOwlProof;
import org.semanticweb.elk.protege.ElkPreferences;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.UnsupportedEntailmentTypeException;

public class ElkProofService extends ProofService
		implements OWLModelManagerListener {

	private ElkReasoner reasoner_ = null;

	private final List<ChangeListener> listeners_ = new ArrayList<ChangeListener>();

	@Override
	public void initialise() throws Exception {
		changeReasoner();
		getEditorKit().getOWLModelManager().addListener(this);
	}

	@Override
	public void dispose() {
		reasoner_ = null;
		getEditorKit().getOWLModelManager().removeListener(this);
	}

	@Override
	public boolean hasProof(OWLAxiom entailment) {
		return (reasoner_ != null && entailment instanceof OWLSubClassOfAxiom
				|| entailment instanceof OWLEquivalentClassesAxiom);
	}

	@Override
	public DynamicProof<Inference<? extends OWLAxiom>> getProof(
			final OWLAxiom entailment)
			throws UnsupportedEntailmentTypeException {
		return new DynamicOwlProof(entailment);
	}

	@Override
	public void handleChange(OWLModelManagerChangeEvent event) {
		if (event.isType(EventType.REASONER_CHANGED)
				|| event.isType(EventType.ACTIVE_ONTOLOGY_CHANGED)) {
			changeReasoner();
		}
	}

	void changeReasoner() {
		ElkReasoner newReasoner = null;
		OWLReasoner reasoner = getEditorKit().getOWLModelManager()
				.getOWLReasonerManager().getCurrentReasoner();
		if (reasoner instanceof ElkReasoner) {
			newReasoner = (ElkReasoner) reasoner;
		}
		if (newReasoner == reasoner_) {
			return;
		}
		// else
		if (reasoner_ != null && reasoner_.equals(newReasoner)) {
			return;
		}
		// else
		reasoner_ = newReasoner;
		for (ChangeListener listener : listeners_) {
			listener.reasonerChanged();
		}
	}

	private static interface ChangeListener {

		void reasonerChanged();

	}

	private class DynamicOwlProof implements
			DynamicProof<Inference<? extends OWLAxiom>>, ChangeListener {

		private final OWLAxiom entailment_;

		private DynamicProof<? extends Inference<? extends OWLAxiom>> proof_;

		private final Set<DynamicProof.ChangeListener> listeners_ = new HashSet<DynamicProof.ChangeListener>();

		DynamicOwlProof(OWLAxiom entailment) {
			this.entailment_ = entailment;
			this.proof_ = ElkOwlProof.create(reasoner_, entailment);
			ElkProofService.this.listeners_.add(this);
		}

		@Override
		public void reasonerChanged() {
			for (DynamicProof.ChangeListener listener : listeners_) {
				proof_.removeListener(listener);
			}
			this.proof_ = ElkOwlProof.create(reasoner_, entailment_);
			for (DynamicProof.ChangeListener listener : listeners_) {
				proof_.addListener(listener);
			}
		}

		@Override
		public void dispose() {
			proof_.dispose();
			ElkProofService.this.listeners_.remove(this);
		}

		@Override
		public Collection<? extends Inference<? extends OWLAxiom>> getInferences(
				Object conclusion) {
			return proof_.getInferences(conclusion);
		}

		@Override
		public void addListener(DynamicProof.ChangeListener listener) {
			if (listeners_.add(listener)) {
				proof_.addListener(listener);
			}
		}

		@Override
		public void removeListener(DynamicProof.ChangeListener listener) {
			if (listeners_.remove(listener)) {
				proof_.removeListener(listener);
			}
		}

	}

	@Override
	public Inference<? extends OWLAxiom> getExample(
			Inference<? extends OWLAxiom> inference) {
		return ElkOwlInferenceExamples.getExample(inference);
	}

	@Override
	public ProofNode<OWLAxiom> postProcess(ProofNode<OWLAxiom> node) {
		ElkPreferences prefs = new ElkPreferences().load();
		if (prefs.inlineInferences) {
			return new InlinedOwlProofNode(node);
		}
		// else
		return node;
	}

}
