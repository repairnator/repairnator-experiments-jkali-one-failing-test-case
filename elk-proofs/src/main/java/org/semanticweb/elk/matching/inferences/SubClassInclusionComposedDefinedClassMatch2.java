package org.semanticweb.elk.matching.inferences;

import org.semanticweb.elk.matching.ElkMatchException;

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
import org.semanticweb.elk.matching.conclusions.IndexedEquivalentClassesAxiomMatch2;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionComposedMatch1;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionComposedMatch1Watch;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;

public class SubClassInclusionComposedDefinedClassMatch2 extends
		AbstractInferenceMatch<SubClassInclusionComposedDefinedClassMatch1>
		implements SubClassInclusionComposedMatch1Watch {

	private final ElkClassExpression definitionMatch_;

	SubClassInclusionComposedDefinedClassMatch2(
			SubClassInclusionComposedDefinedClassMatch1 parent,
			IndexedEquivalentClassesAxiomMatch2 secondPremiseMatch) {
		super(parent);
		ElkClassExpression definedClass = secondPremiseMatch
				.getFirstMemberMatch();
		if (!parent.getDefinedClassMatch().equals(definedClass)) {
			throw new ElkMatchException(
					parent.getParent().getConclusionSubsumer(), definedClass);
		}
		this.definitionMatch_ = secondPremiseMatch.getSecondMemberMatch();
		checkEquals(secondPremiseMatch, getSecondPremiseMatch(DEBUG_FACTORY));
	}

	public ElkClassExpression getDefinitionMatch() {
		return definitionMatch_;
	}

	IndexedEquivalentClassesAxiomMatch2 getSecondPremiseMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getIndexedEquivalentClassesAxiomMatch2(
				getParent().getSecondPremiseMatch(factory),
				getParent().getDefinedClassMatch(), getDefinitionMatch());
	}

	public SubClassInclusionComposedMatch1 getFirstPremiseMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getSubClassInclusionComposedMatch1(
				getParent().getParent().getFirstPremise(factory),
				getParent().getOriginMatch(), getDefinitionMatch());
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

		O visit(SubClassInclusionComposedDefinedClassMatch2 inferenceMatch2);

	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		SubClassInclusionComposedDefinedClassMatch2 getSubClassInclusionComposedDefinedClassMatch2(
				SubClassInclusionComposedDefinedClassMatch1 parent,
				IndexedEquivalentClassesAxiomMatch2 secondPremiseMatch);

	}

}
