/*
 * #%L
 * ELK Proofs Package
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
package org.semanticweb.elk.matching;

import java.util.Collection;

import org.semanticweb.elk.matching.conclusions.BackwardLinkMatch1;
import org.semanticweb.elk.matching.conclusions.BackwardLinkMatch1Watch;
import org.semanticweb.elk.matching.conclusions.BackwardLinkMatch2;
import org.semanticweb.elk.matching.conclusions.BackwardLinkMatch2Watch;
import org.semanticweb.elk.matching.conclusions.BackwardLinkMatch3;
import org.semanticweb.elk.matching.conclusions.BackwardLinkMatch3Watch;
import org.semanticweb.elk.matching.conclusions.ClassInconsistencyMatch1;
import org.semanticweb.elk.matching.conclusions.ClassInconsistencyMatch1Watch;
import org.semanticweb.elk.matching.conclusions.ConclusionMatch;
import org.semanticweb.elk.matching.conclusions.DisjointSubsumerMatch1;
import org.semanticweb.elk.matching.conclusions.DisjointSubsumerMatch1Watch;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch1;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch1Watch;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch2;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch2Watch;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch3;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch3Watch;
import org.semanticweb.elk.matching.conclusions.IndexedDisjointClassesAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedDisjointClassesAxiomMatch1Watch;
import org.semanticweb.elk.matching.conclusions.IndexedEquivalentClassesAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedEquivalentClassesAxiomMatch1Watch;
import org.semanticweb.elk.matching.conclusions.IndexedObjectPropertyRangeAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedObjectPropertyRangeAxiomMatch1Watch;
import org.semanticweb.elk.matching.conclusions.IndexedSubClassOfAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedSubClassOfAxiomMatch1Watch;
import org.semanticweb.elk.matching.conclusions.IndexedSubObjectPropertyOfAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedSubObjectPropertyOfAxiomMatch1Watch;
import org.semanticweb.elk.matching.conclusions.PropagationMatch1;
import org.semanticweb.elk.matching.conclusions.PropagationMatch1Watch;
import org.semanticweb.elk.matching.conclusions.PropertyRangeMatch1;
import org.semanticweb.elk.matching.conclusions.PropertyRangeMatch1Watch;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionComposedMatch1;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionComposedMatch1Watch;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionDecomposedMatch1;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionDecomposedMatch1Watch;
import org.semanticweb.elk.matching.conclusions.SubPropertyChainMatch1;
import org.semanticweb.elk.matching.conclusions.SubPropertyChainMatch1Watch;
import org.semanticweb.elk.matching.inferences.InferenceMatch;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDeclarationAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDeclarationAxiomInference;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDisjointClassesAxiomInference;
import org.semanticweb.elk.reasoner.indexing.model.IndexedEquivalentClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedEquivalentClassesAxiomInference;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectPropertyRangeAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectPropertyRangeAxiomInference;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubClassOfAxiomInference;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubObjectPropertyOfAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubObjectPropertyOfAxiomInference;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassInconsistency;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.PropertyRange;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionDecomposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubPropertyChain;
import org.semanticweb.elk.reasoner.saturation.inferences.BackwardLinkInference;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInconsistencyInference;
import org.semanticweb.elk.reasoner.saturation.inferences.DisjointSubsumerInference;
import org.semanticweb.elk.reasoner.saturation.inferences.ForwardLinkInference;
import org.semanticweb.elk.reasoner.saturation.inferences.PropagationInference;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionComposedInference;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionDecomposedInference;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.PropertyRangeInference;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.SubPropertyChainInference;
import org.semanticweb.elk.reasoner.tracing.TracingProof;
import org.semanticweb.elk.util.collections.HashListMultimap;
import org.semanticweb.elk.util.collections.Multimap;

// TODO: avoid casts
@SuppressWarnings("unchecked")
class InferenceMatchMapImpl
		implements InferenceMap, InferenceMatchMap, InferenceMatchMapWriter {

	private final TracingProof inferences_;

	private final Multimap<ConclusionMatch, InferenceMatch> watchedInferences_ = new HashListMultimap<ConclusionMatch, InferenceMatch>();

	InferenceMatchMapImpl(TracingProof inferences) {
		this.inferences_ = inferences;
	}

	@Override
	public void add(BackwardLinkMatch1 conclusion,
			BackwardLinkMatch1Watch inference) {
		watchedInferences_.add(conclusion, inference);
	}

	@Override
	public void add(BackwardLinkMatch2 conclusion,
			BackwardLinkMatch2Watch inference) {
		watchedInferences_.add(conclusion, inference);
	}

	@Override
	public void add(BackwardLinkMatch3 conclusion,
			BackwardLinkMatch3Watch inference) {
		watchedInferences_.add(conclusion, inference);
	}

	@Override
	public void add(ClassInconsistencyMatch1 conclusion,
			ClassInconsistencyMatch1Watch inference) {
		watchedInferences_.add(conclusion, inference);
	}

	@Override
	public void add(DisjointSubsumerMatch1 conclusion,
			DisjointSubsumerMatch1Watch inference) {
		watchedInferences_.add(conclusion, inference);

	}

	@Override
	public void add(ForwardLinkMatch1 conclusion,
			ForwardLinkMatch1Watch inference) {
		watchedInferences_.add(conclusion, inference);

	}

	@Override
	public void add(ForwardLinkMatch2 conclusion,
			ForwardLinkMatch2Watch inference) {
		watchedInferences_.add(conclusion, inference);
	}

	@Override
	public void add(ForwardLinkMatch3 conclusion,
			ForwardLinkMatch3Watch inference) {
		watchedInferences_.add(conclusion, inference);
	}

	@Override
	public void add(IndexedDisjointClassesAxiomMatch1 conclusion,
			IndexedDisjointClassesAxiomMatch1Watch inference) {
		watchedInferences_.add(conclusion, inference);

	}

	@Override
	public void add(IndexedEquivalentClassesAxiomMatch1 conclusion,
			IndexedEquivalentClassesAxiomMatch1Watch inference) {
		watchedInferences_.add(conclusion, inference);

	}

	@Override
	public void add(IndexedObjectPropertyRangeAxiomMatch1 conclusion,
			IndexedObjectPropertyRangeAxiomMatch1Watch inference) {
		watchedInferences_.add(conclusion, inference);

	}

	@Override
	public void add(IndexedSubClassOfAxiomMatch1 conclusion,
			IndexedSubClassOfAxiomMatch1Watch inference) {
		watchedInferences_.add(conclusion, inference);
	}

	@Override
	public void add(IndexedSubObjectPropertyOfAxiomMatch1 conclusion,
			IndexedSubObjectPropertyOfAxiomMatch1Watch inference) {
		watchedInferences_.add(conclusion, inference);

	}

	@Override
	public void add(PropagationMatch1 conclusion,
			PropagationMatch1Watch inference) {
		watchedInferences_.add(conclusion, inference);
	}

	@Override
	public void add(PropertyRangeMatch1 conclusion,
			PropertyRangeMatch1Watch inference) {
		watchedInferences_.add(conclusion, inference);

	}

	@Override
	public void add(SubClassInclusionComposedMatch1 conclusion,
			SubClassInclusionComposedMatch1Watch inference) {
		watchedInferences_.add(conclusion, inference);
	}

	@Override
	public void add(SubClassInclusionDecomposedMatch1 conclusion,
			SubClassInclusionDecomposedMatch1Watch inference) {
		watchedInferences_.add(conclusion, inference);

	}

	@Override
	public void add(SubPropertyChainMatch1 conclusion,
			SubPropertyChainMatch1Watch inference) {
		watchedInferences_.add(conclusion, inference);

	}

	@Override
	public Iterable<? extends BackwardLinkInference> get(
			BackwardLink conclusion) {
		return (Iterable<? extends BackwardLinkInference>) inferences_
				.getInferences(conclusion);
	}

	@Override
	public Iterable<? extends BackwardLinkMatch1Watch> get(
			BackwardLinkMatch1 conclusion) {
		return (Iterable<? extends BackwardLinkMatch1Watch>) getWatchInferences(
				conclusion);
	}

	@Override
	public Iterable<? extends BackwardLinkMatch2Watch> get(
			BackwardLinkMatch2 conclusion) {
		return (Iterable<? extends BackwardLinkMatch2Watch>) getWatchInferences(
				conclusion);
	}

	@Override
	public Iterable<? extends BackwardLinkMatch3Watch> get(
			BackwardLinkMatch3 conclusion) {
		return (Iterable<? extends BackwardLinkMatch3Watch>) getWatchInferences(
				conclusion);
	}

	@Override
	public Iterable<? extends ClassInconsistencyInference> get(
			ClassInconsistency conclusion) {
		return (Iterable<? extends ClassInconsistencyInference>) inferences_
				.getInferences(conclusion);
	}

	@Override
	public Iterable<? extends ClassInconsistencyMatch1Watch> get(
			ClassInconsistencyMatch1 conclusion) {
		return (Collection<? extends ClassInconsistencyMatch1Watch>) getWatchInferences(
				conclusion);
	}

	@Override
	public Iterable<? extends DisjointSubsumerInference> get(
			DisjointSubsumer conclusion) {
		return (Iterable<? extends DisjointSubsumerInference>) inferences_
				.getInferences(conclusion);
	}

	@Override
	public Iterable<? extends DisjointSubsumerMatch1Watch> get(
			DisjointSubsumerMatch1 conclusion) {
		return (Iterable<? extends DisjointSubsumerMatch1Watch>) getWatchInferences(
				conclusion);
	}

	@Override
	public Iterable<? extends ForwardLinkInference> get(
			ForwardLink conclusion) {
		return (Iterable<? extends ForwardLinkInference>) inferences_
				.getInferences(conclusion);
	}

	@Override
	public Iterable<? extends ForwardLinkMatch1Watch> get(
			ForwardLinkMatch1 conclusion) {
		return (Collection<? extends ForwardLinkMatch1Watch>) getWatchInferences(
				conclusion);
	}

	@Override
	public Iterable<? extends ForwardLinkMatch2Watch> get(
			ForwardLinkMatch2 conclusion) {
		return (Collection<? extends ForwardLinkMatch2Watch>) getWatchInferences(
				conclusion);
	}

	@Override
	public Iterable<? extends ForwardLinkMatch3Watch> get(
			ForwardLinkMatch3 conclusion) {
		return (Collection<? extends ForwardLinkMatch3Watch>) getWatchInferences(
				conclusion);
	}

	@Override
	public Iterable<? extends IndexedDeclarationAxiomInference> get(
			IndexedDeclarationAxiom conclusion) {
		return (Iterable<? extends IndexedDeclarationAxiomInference>) inferences_
				.getInferences(conclusion);
	}

	@Override
	public Iterable<? extends IndexedDisjointClassesAxiomInference> get(
			IndexedDisjointClassesAxiom conclusion) {
		return (Iterable<? extends IndexedDisjointClassesAxiomInference>) inferences_
				.getInferences(conclusion);
	}

	@Override
	public Iterable<? extends IndexedDisjointClassesAxiomMatch1Watch> get(
			IndexedDisjointClassesAxiomMatch1 conclusion) {
		return (Collection<? extends IndexedDisjointClassesAxiomMatch1Watch>) getWatchInferences(
				conclusion);
	}

	@Override
	public Iterable<? extends IndexedEquivalentClassesAxiomInference> get(
			IndexedEquivalentClassesAxiom conclusion) {
		return (Iterable<? extends IndexedEquivalentClassesAxiomInference>) inferences_
				.getInferences(conclusion);
	}

	@Override
	public Iterable<? extends IndexedEquivalentClassesAxiomMatch1Watch> get(
			IndexedEquivalentClassesAxiomMatch1 conclusion) {
		return (Collection<? extends IndexedEquivalentClassesAxiomMatch1Watch>) getWatchInferences(
				conclusion);
	}

	@Override
	public Iterable<? extends IndexedObjectPropertyRangeAxiomInference> get(
			IndexedObjectPropertyRangeAxiom conclusion) {
		return (Iterable<? extends IndexedObjectPropertyRangeAxiomInference>) inferences_
				.getInferences(conclusion);
	}

	@Override
	public Iterable<? extends IndexedObjectPropertyRangeAxiomMatch1Watch> get(
			IndexedObjectPropertyRangeAxiomMatch1 conclusion) {
		return (Collection<? extends IndexedObjectPropertyRangeAxiomMatch1Watch>) getWatchInferences(
				conclusion);
	}

	@Override
	public Iterable<? extends IndexedSubClassOfAxiomInference> get(
			IndexedSubClassOfAxiom conclusion) {
		return (Iterable<? extends IndexedSubClassOfAxiomInference>) inferences_
				.getInferences(conclusion);
	}

	@Override
	public Iterable<? extends IndexedSubClassOfAxiomMatch1Watch> get(
			IndexedSubClassOfAxiomMatch1 conclusion) {
		return (Collection<? extends IndexedSubClassOfAxiomMatch1Watch>) getWatchInferences(
				conclusion);
	}

	@Override
	public Iterable<? extends IndexedSubObjectPropertyOfAxiomInference> get(
			IndexedSubObjectPropertyOfAxiom conclusion) {
		return (Iterable<? extends IndexedSubObjectPropertyOfAxiomInference>) inferences_
				.getInferences(conclusion);
	}

	@Override
	public Iterable<? extends IndexedSubObjectPropertyOfAxiomMatch1Watch> get(
			IndexedSubObjectPropertyOfAxiomMatch1 conclusion) {
		return (Collection<? extends IndexedSubObjectPropertyOfAxiomMatch1Watch>) getWatchInferences(
				conclusion);
	}

	@Override
	public Iterable<? extends PropagationInference> get(
			Propagation conclusion) {
		return (Iterable<? extends PropagationInference>) inferences_
				.getInferences(conclusion);
	}

	@Override
	public Iterable<? extends PropagationMatch1Watch> get(
			PropagationMatch1 conclusion) {
		return (Collection<? extends PropagationMatch1Watch>) getWatchInferences(
				conclusion);
	}

	@Override
	public Iterable<? extends PropertyRangeInference> get(
			PropertyRange conclusion) {
		return (Iterable<? extends PropertyRangeInference>) inferences_
				.getInferences(conclusion);
	}

	@Override
	public Iterable<? extends PropertyRangeMatch1Watch> get(
			PropertyRangeMatch1 conclusion) {
		return (Collection<? extends PropertyRangeMatch1Watch>) getWatchInferences(
				conclusion);
	}

	@Override
	public Iterable<? extends SubClassInclusionComposedInference> get(
			SubClassInclusionComposed conclusion) {
		return (Iterable<? extends SubClassInclusionComposedInference>) inferences_
				.getInferences(conclusion);
	}

	@Override
	public Iterable<? extends SubClassInclusionComposedMatch1Watch> get(
			SubClassInclusionComposedMatch1 conclusion) {
		return (Collection<? extends SubClassInclusionComposedMatch1Watch>) getWatchInferences(
				conclusion);
	}

	@Override
	public Iterable<? extends SubClassInclusionDecomposedInference> get(
			SubClassInclusionDecomposed conclusion) {
		return (Iterable<? extends SubClassInclusionDecomposedInference>) inferences_
				.getInferences(conclusion);
	}

	@Override
	public Iterable<? extends SubClassInclusionDecomposedMatch1Watch> get(
			SubClassInclusionDecomposedMatch1 conclusion) {
		return (Collection<? extends SubClassInclusionDecomposedMatch1Watch>) getWatchInferences(
				conclusion);
	}

	@Override
	public Iterable<? extends SubPropertyChainInference> get(
			SubPropertyChain conclusion) {
		return (Iterable<? extends SubPropertyChainInference>) inferences_
				.getInferences(conclusion);
	}

	@Override
	public Iterable<? extends SubPropertyChainMatch1Watch> get(
			SubPropertyChainMatch1 conclusion) {
		return (Collection<? extends SubPropertyChainMatch1Watch>) getWatchInferences(
				conclusion);
	}

	private Collection<? extends InferenceMatch> getWatchInferences(
			ConclusionMatch conclusion) {
		// need this method since some java compilers have problems for casting
		// directly
		return watchedInferences_.get(conclusion);
	}

}
