package org.semanticweb.elk.matching.inferences;

/*-
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

import org.semanticweb.elk.matching.conclusions.BackwardLinkMatch3;
import org.semanticweb.elk.matching.conclusions.ConclusionMatchExpressionFactory;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch3;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch3Watch;
import org.semanticweb.elk.matching.root.IndexedContextRootMatch;

public class BackwardLinkReversedExpandedMatch4
		extends AbstractInferenceMatch<BackwardLinkReversedExpandedMatch3>
		implements ForwardLinkMatch3Watch {

	private final IndexedContextRootMatch extendedDestinationMatch_;

	BackwardLinkReversedExpandedMatch4(
			BackwardLinkReversedExpandedMatch3 parent,
			BackwardLinkMatch3 conclusionMatch) {
		super(parent);
		this.extendedDestinationMatch_ = conclusionMatch
				.getExtendedDestinationMatch();
		checkEquals(conclusionMatch, getConclusionMatch(DEBUG_FACTORY));
	}

	public IndexedContextRootMatch getExtendedDestinationMatch() {
		return extendedDestinationMatch_;
	}

	public ForwardLinkMatch3 getFirstPremiseMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getForwardLinkMatch3(
				getParent().getFirstPremiseMatch(factory),
				getExtendedDestinationMatch());
	}

	BackwardLinkMatch3 getConclusionMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getBackwardLinkMatch3(
				getParent().getConclusionMatch(factory),
				getExtendedDestinationMatch());
	}

	@Override
	public <O> O accept(InferenceMatch.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(ForwardLinkMatch3Watch.Visitor<O> visitor) {
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

		O visit(BackwardLinkReversedExpandedMatch4 inferenceMatch4);

	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		BackwardLinkReversedExpandedMatch4 getBackwardLinkReversedExpandedMatch4(
				BackwardLinkReversedExpandedMatch3 parent,
				BackwardLinkMatch3 conclusionMatch);

	}

}
