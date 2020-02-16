package org.semanticweb.elk.matching.inferences;

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

import org.semanticweb.elk.matching.conclusions.ConclusionMatchExpressionFactory;
import org.semanticweb.elk.matching.conclusions.PropagationMatch1;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionComposedMatch1;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionComposedMatch1Watch;
import org.semanticweb.elk.matching.root.IndexedContextRootMatch;
import org.semanticweb.elk.matching.subsumers.IndexedObjectSomeValuesFromMatch;
import org.semanticweb.elk.matching.subsumers.SubsumerMatches;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.reasoner.saturation.inferences.PropagationGenerated;

public class PropagationGeneratedMatch1
		extends AbstractInferenceMatch<PropagationGenerated>
		implements SubClassInclusionComposedMatch1Watch {

	private final IndexedContextRootMatch destinationMatch_;

	private final ElkObjectProperty subDestinationMatch_;

	private final IndexedObjectSomeValuesFromMatch conclusionCarryMatch_;

	PropagationGeneratedMatch1(PropagationGenerated parent,
			PropagationMatch1 conclusionMatch) {
		super(parent);
		this.destinationMatch_ = conclusionMatch.getDestinationMatch();
		this.subDestinationMatch_ = conclusionMatch.getSubDestinationMatch();
		this.conclusionCarryMatch_ = conclusionMatch.getCarryMatch();
		checkEquals(conclusionMatch, getConclusionMatch(DEBUG_FACTORY));
	}

	IndexedContextRootMatch getDestinationMatch() {
		return destinationMatch_;
	}

	public ElkObjectProperty getSubDestinationMatch() {
		return subDestinationMatch_;
	}

	public IndexedObjectSomeValuesFromMatch getConclusionCarryMatch() {
		return conclusionCarryMatch_;
	}

	PropagationMatch1 getConclusionMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getPropagationMatch1(getParent().getConclusion(factory),
				destinationMatch_, subDestinationMatch_, conclusionCarryMatch_);
	}

	public SubClassInclusionComposedMatch1 getSecondPremiseMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getSubClassInclusionComposedMatch1(
				getParent().getSecondPremise(factory), destinationMatch_,
				SubsumerMatches
						.create(getConclusionCarryMatch().getFillerMatch()));
	}	

	@Override
	public <O> O accept(InferenceMatch.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(
			SubClassInclusionComposedMatch1Watch.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	public interface Visitor<O> {

		O visit(PropagationGeneratedMatch1 inferenceMatch1);

	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		PropagationGeneratedMatch1 getPropagationGeneratedMatch1(
				PropagationGenerated parent, PropagationMatch1 conclusionMatch);

	}

}
