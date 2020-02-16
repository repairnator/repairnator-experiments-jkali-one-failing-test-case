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
package org.semanticweb.elk.owl.inferences;

import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;

public class ElkInferenceDelegatingFactory implements ElkInference.Factory {

	private final ElkInference.Factory mainFactory_;

	public ElkInferenceDelegatingFactory(ElkInference.Factory mainFactory) {
		this.mainFactory_ = mainFactory;
	}

	protected <I extends ElkInference> I filter(I inference) {
		return inference;
	}

	@Override
	public ElkClassAssertionOfClassInclusion getElkClassAssertionOfClassInclusion(
			final ElkIndividual instance, final ElkClassExpression type) {
		return filter(mainFactory_
				.getElkClassAssertionOfClassInclusion(instance, type));
	}

	@Override
	public ElkClassInclusionEmptyObjectOneOfOwlNothing getElkClassInclusionEmptyObjectOneOfOwlNothing() {
		return filter(
				mainFactory_.getElkClassInclusionEmptyObjectOneOfOwlNothing());
	}

	@Override
	public ElkClassInclusionEmptyObjectUnionOfOwlNothing getElkClassInclusionEmptyObjectUnionOfOwlNothing() {
		return filter(mainFactory_
				.getElkClassInclusionEmptyObjectUnionOfOwlNothing());
	}

	@Override
	public ElkClassInclusionExistentialComposition getElkClassInclusionExistentialComposition(
			List<? extends ElkClassExpression> classExpressions,
			List<? extends ElkObjectPropertyExpression> subChain,
			ElkObjectPropertyExpression superProperty) {
		return filter(mainFactory_.getElkClassInclusionExistentialComposition(
				classExpressions, subChain, superProperty));
	}

	@Override
	public ElkClassInclusionExistentialFillerExpansion getElkClassInclusionExistentialFillerExpansion(
			ElkObjectPropertyExpression property, ElkClassExpression subClass,
			ElkClassExpression superClass) {
		return filter(
				mainFactory_.getElkClassInclusionExistentialFillerExpansion(
						property, subClass, superClass));
	}

	@Override
	public ElkClassInclusionExistentialOfObjectHasSelf getElkClassInclusionExistentialOfObjectHasSelf(
			ElkClassExpression subClass, ElkObjectPropertyExpression property) {
		return filter(
				mainFactory_.getElkClassInclusionExistentialOfObjectHasSelf(
						subClass, property));
	}

	@Override
	public ElkClassInclusionExistentialOwlNothing getElkClassInclusionExistentialOwlNothing(
			ElkObjectPropertyExpression property) {
		return filter(mainFactory_
				.getElkClassInclusionExistentialOwlNothing(property));
	}

	@Override
	public ElkClassInclusionExistentialPropertyExpansion getElkClassInclusionExistentialPropertyExpansion(
			ElkObjectPropertyExpression subProperty,
			ElkObjectPropertyExpression superProperty,
			ElkClassExpression filler) {
		return filter(
				mainFactory_.getElkClassInclusionExistentialPropertyExpansion(
						subProperty, superProperty, filler));
	}

	@Override
	public ElkClassInclusionExistentialRange getElkClassInclusionExistentialRange(
			ElkObjectPropertyExpression property, ElkClassExpression filler,
			ElkClassExpression... ranges) {
		return filter(mainFactory_.getElkClassInclusionExistentialRange(
				property, filler, ranges));
	}

	@Override
	public ElkClassInclusionExistentialRange getElkClassInclusionExistentialRange(
			ElkObjectPropertyExpression property, ElkClassExpression filler,
			List<? extends ElkClassExpression> ranges) {
		return filter(mainFactory_.getElkClassInclusionExistentialRange(
				property, filler, ranges));
	}

	@Override
	public ElkClassInclusionExistentialTransitivity getElkClassInclusionExistentialTransitivity(
			ElkObjectPropertyExpression transitiveProperty,
			ElkClassExpression... classExpressions) {
		return filter(mainFactory_.getElkClassInclusionExistentialTransitivity(
				transitiveProperty, classExpressions));
	}

	@Override
	public ElkClassInclusionExistentialTransitivity getElkClassInclusionExistentialTransitivity(
			ElkObjectPropertyExpression transitiveProperty,
			List<? extends ElkClassExpression> classExpressions) {
		return filter(mainFactory_.getElkClassInclusionExistentialTransitivity(
				transitiveProperty, classExpressions));
	}

	@Override
	public ElkClassInclusionHierarchy getElkClassInclusionHierarchy(
			ElkClassExpression... expressions) {
		return filter(mainFactory_.getElkClassInclusionHierarchy(expressions));
	}

	@Override
	public ElkClassInclusionHierarchy getElkClassInclusionHierarchy(
			List<? extends ElkClassExpression> expressions) {
		return filter(mainFactory_.getElkClassInclusionHierarchy(expressions));
	}

	@Override
	public ElkClassInclusionNegationClash getElkClassInclusionNegationClash(
			ElkClassExpression expression) {
		return filter(
				mainFactory_.getElkClassInclusionNegationClash(expression));
	}

	@Override
	public ElkClassInclusionObjectIntersectionOfComposition getElkClassInclusionObjectIntersectionOfComposition(
			ElkClassExpression subExpression, ElkClassExpression firstConjunct,
			ElkClassExpression secondConjunct) {
		return filter(mainFactory_
				.getElkClassInclusionObjectIntersectionOfComposition(
						subExpression, firstConjunct, secondConjunct));
	}

	@Override
	public ElkClassInclusionObjectIntersectionOfComposition getElkClassInclusionObjectIntersectionOfComposition(
			ElkClassExpression subExpression,
			List<? extends ElkClassExpression> conjuncts) {
		return filter(mainFactory_
				.getElkClassInclusionObjectIntersectionOfComposition(
						subExpression, conjuncts));
	}

	@Override
	public ElkClassInclusionObjectIntersectionOfDecomposition getElkClassInclusionObjectIntersectionOfDecomposition(
			List<? extends ElkClassExpression> conjuncts, int conjunctPos) {
		return filter(mainFactory_
				.getElkClassInclusionObjectIntersectionOfDecomposition(
						conjuncts, conjunctPos));
	}

	@Override
	public ElkClassInclusionObjectIntersectionOfInclusion getElkClassInclusionObjectIntersectionOfInclusion(
			List<? extends ElkClassExpression> subClasses,
			List<Integer> superPositions) {
		return filter(
				mainFactory_.getElkClassInclusionObjectIntersectionOfInclusion(
						subClasses, superPositions));
	}

	@Override
	public ElkClassInclusionObjectOneOfInclusion getElkClassInclusionObjectOneOfInclusion(
			List<? extends ElkIndividual> superIndividuals,
			List<Integer> subPositions) {
		return filter(mainFactory_.getElkClassInclusionObjectOneOfInclusion(
				superIndividuals, subPositions));
	}

	@Override
	public ElkClassInclusionObjectUnionOfComposition getElkClassInclusionObjectUnionOfComposition(
			List<? extends ElkClassExpression> disjuncts, int disjunctPos) {
		return filter(mainFactory_.getElkClassInclusionObjectUnionOfComposition(
				disjuncts, disjunctPos));
	}

	@Override
	public ElkClassInclusionOfClassAssertion getElkClassInclusionOfClassAssertion(
			ElkIndividual instance, ElkClassExpression type) {
		return filter(mainFactory_
				.getElkClassInclusionOfClassAssertion(instance, type));
	}

	@Override
	public ElkClassInclusionOfDisjointClasses getElkClassInclusionOfDisjointClasses(
			List<? extends ElkClassExpression> expressions, int firstPos,
			int secondPos) {
		return filter(mainFactory_.getElkClassInclusionOfDisjointClasses(
				expressions, firstPos, secondPos));
	}

	@Override
	public ElkClassInclusionOfEquivaletClasses getElkClassInclusionOfEquivaletClasses(
			ElkClassExpression first, ElkClassExpression second,
			boolean sameOrder) {
		return filter(mainFactory_.getElkClassInclusionOfEquivaletClasses(first,
				second, sameOrder));
	}

	@Override
	public ElkClassInclusionOfEquivaletClasses getElkClassInclusionOfEquivaletClasses(
			List<? extends ElkClassExpression> expressions, int subPos,
			int superPos) {
		return filter(mainFactory_.getElkClassInclusionOfEquivaletClasses(
				expressions, subPos, superPos));
	}

	@Override
	public ElkClassInclusionOfObjectPropertyAssertion getElkClassInclusionOfObjectPropertyAssertion(
			ElkIndividual subject, ElkObjectPropertyExpression property,
			ElkIndividual object) {
		return filter(
				mainFactory_.getElkClassInclusionOfObjectPropertyAssertion(
						subject, property, object));
	}

	@Override
	public ElkClassInclusionOfObjectPropertyDomain getElkClassInclusionOfObjectPropertyDomain(
			ElkObjectPropertyExpression property, ElkClassExpression domain) {
		return filter(mainFactory_
				.getElkClassInclusionOfObjectPropertyDomain(property, domain));
	}

	@Override
	public ElkClassInclusionOfReflexiveObjectProperty getElkClassInclusionOfReflexiveObjectProperty(
			ElkObjectPropertyExpression property) {
		return filter(mainFactory_
				.getElkClassInclusionOfReflexiveObjectProperty(property));
	}

	@Override
	public ElkClassInclusionOwlBottomObjectProperty getElkClassInclusionOwlBottomObjectProperty() {
		return filter(
				mainFactory_.getElkClassInclusionOwlBottomObjectProperty());
	}

	@Override
	public ElkClassInclusionOwlNothing getElkClassInclusionOwlNothing(
			ElkClassExpression superClass) {
		return filter(mainFactory_.getElkClassInclusionOwlNothing(superClass));
	}

	@Override
	public ElkClassInclusionOwlThing getElkClassInclusionOwlThing(
			ElkClassExpression subClass) {
		return filter(mainFactory_.getElkClassInclusionOwlThing(subClass));
	}

	@Override
	public ElkClassInclusionOwlTopObjectProperty getElkClassInclusionOwlTopObjectProperty() {
		return filter(mainFactory_.getElkClassInclusionOwlTopObjectProperty());
	}

	@Override
	public ElkClassInclusionOwlThingEmptyObjectIntersectionOf getElkClassInclusionOwlThingEmptyObjectIntersectionOf() {
		return filter(mainFactory_
				.getElkClassInclusionOwlThingEmptyObjectIntersectionOf());
	}

	@Override
	public ElkClassInclusionReflexivePropertyRange getElkClassInclusionReflexivePropertyRange(
			ElkClassExpression subClass, ElkObjectPropertyExpression property,
			ElkClassExpression range) {
		return filter(mainFactory_.getElkClassInclusionReflexivePropertyRange(
				subClass, property, range));
	}

	@Override
	public ElkClassInclusionSingletonObjectUnionOfDecomposition getElkClassInclusionSingletonObjectUnionOfDecomposition(
			ElkClassExpression disjunct) {
		return filter(mainFactory_
				.getElkClassInclusionSingletonObjectUnionOfDecomposition(
						disjunct));
	}

	@Override
	public ElkClassInclusionTautology getElkClassInclusionTautology(
			ElkClassExpression expression) {
		return filter(mainFactory_.getElkClassInclusionTautology(expression));
	}

	@Override
	public ElkClassInclusionTopObjectHasValue getElkClassInclusionTopObjectHasValue(
			ElkIndividual value) {
		return filter(
				mainFactory_.getElkClassInclusionTopObjectHasValue(value));
	}

	@Override
	public ElkDifferentIndividualsOfDisjointClasses getElkDifferentIndividualsOfDisjointClasses(
			final List<? extends ElkIndividual> different) {
		return filter(mainFactory_
				.getElkDifferentIndividualsOfDisjointClasses(different));
	}

	@Override
	public ElkDisjointClassesIntersectionInconsistencies getElkDisjointClassesIntersectionInconsistencies(
			List<? extends ElkClassExpression> expressions) {
		return filter(mainFactory_
				.getElkDisjointClassesIntersectionInconsistencies(expressions));
	}

	@Override
	public ElkDisjointClassesIntersectionInconsistencies getElkDisjointClassesIntersectionInconsistencies(
			final ElkClassExpression... expressions) {
		return filter(mainFactory_
				.getElkDisjointClassesIntersectionInconsistencies(expressions));
	}

	@Override
	public ElkDisjointClassesOfDifferentIndividuals getElkDisjointClassesOfDifferentIndividuals(
			List<? extends ElkIndividual> different) {
		return filter(mainFactory_
				.getElkDisjointClassesOfDifferentIndividuals(different));
	}

	@Override
	public ElkDisjointClassesOfDisjointUnion getElkDisjointClassesOfDisjointUnion(
			ElkClass defined, List<? extends ElkClassExpression> disjoint) {
		return filter(mainFactory_.getElkDisjointClassesOfDisjointUnion(defined,
				disjoint));
	}

	@Override
	public ElkEquivalentClassesCycle getElkEquivalentClassesCycle(
			ElkClassExpression first, ElkClassExpression second) {
		return filter(mainFactory_.getElkEquivalentClassesCycle(first, second));
	}

	@Override
	public ElkEquivalentClassesCycle getElkEquivalentClassesCycle(
			List<? extends ElkClassExpression> expressions) {
		return filter(mainFactory_.getElkEquivalentClassesCycle(expressions));
	}

	@Override
	public ElkEquivalentClassesObjectHasValue getElkEquivalentClassesObjectHasValue(
			ElkObjectPropertyExpression property, ElkIndividual value) {
		return filter(mainFactory_
				.getElkEquivalentClassesObjectHasValue(property, value));
	}

	@Override
	public ElkEquivalentClassesObjectOneOf getElkEquivalentClassesObjectOneOf(
			List<? extends ElkIndividual> members) {
		return filter(mainFactory_.getElkEquivalentClassesObjectOneOf(members));
	}

	@Override
	public ElkEquivalentClassesOfDisjointUnion getElkEquivalentClassesOfDisjointUnion(
			ElkClass defined, List<? extends ElkClassExpression> disjoint) {
		return filter(mainFactory_
				.getElkEquivalentClassesOfDisjointUnion(defined, disjoint));
	}

	@Override
	public ElkEquivalentClassesOfSameIndividual getElkEquivalentClassesOfSameIndividual(
			List<? extends ElkIndividual> same) {
		return filter(
				mainFactory_.getElkEquivalentClassesOfSameIndividual(same));
	}

	@Override
	public ElkObjectPropertyAssertionOfClassInclusion getElkObjectPropertyAssertionOfClassInclusion(
			final ElkIndividual subject,
			final ElkObjectPropertyExpression property,
			final ElkIndividual object) {
		return filter(
				mainFactory_.getElkObjectPropertyAssertionOfClassInclusion(
						subject, property, object));
	}

	@Override
	public ElkObjectPropertyDomainOfClassInclusion getElkObjectPropertyDomainOfClassInclusion(
			final ElkObjectPropertyExpression property,
			final ElkClassExpression domain) {
		return filter(mainFactory_
				.getElkObjectPropertyDomainOfClassInclusion(property, domain));
	}

	@Override
	public ElkPropertyInclusionHierarchy getElkPropertyInclusionHierarchy(
			ElkSubObjectPropertyExpression subExpression,
			ElkObjectPropertyExpression... expressions) {
		return filter(mainFactory_
				.getElkPropertyInclusionHierarchy(subExpression, expressions));
	}

	@Override
	public ElkPropertyInclusionHierarchy getElkPropertyInclusionHierarchy(
			ElkSubObjectPropertyExpression subExpression,
			List<? extends ElkObjectPropertyExpression> expressions) {
		return filter(mainFactory_
				.getElkPropertyInclusionHierarchy(subExpression, expressions));
	}

	@Override
	public ElkPropertyInclusionOfEquivalence getElkPropertyInclusionOfEquivalence(
			ElkObjectPropertyExpression first,
			ElkObjectPropertyExpression second, boolean sameOrder) {
		return filter(mainFactory_.getElkPropertyInclusionOfEquivalence(first,
				second, sameOrder));
	}

	@Override
	public ElkPropertyInclusionOfEquivalence getElkPropertyInclusionOfEquivalence(
			List<? extends ElkObjectPropertyExpression> expressions, int subPos,
			int superPos) {
		return filter(mainFactory_.getElkPropertyInclusionOfEquivalence(
				expressions, subPos, superPos));
	}

	@Override
	public ElkPropertyInclusionOfTransitiveObjectProperty getElkPropertyInclusionOfTransitiveObjectProperty(
			ElkObjectPropertyExpression property) {
		return filter(mainFactory_
				.getElkPropertyInclusionOfTransitiveObjectProperty(property));
	}

	@Override
	public ElkPropertyInclusionTautology getElkPropertyInclusionTautology(
			ElkObjectPropertyExpression expression) {
		return filter(
				mainFactory_.getElkPropertyInclusionTautology(expression));
	}

	@Override
	public ElkPropertyRangePropertyExpansion getElkPropertyRangePropertyExpansion(
			ElkObjectPropertyExpression subProperty,
			ElkObjectPropertyExpression superProperty,
			ElkClassExpression range) {
		return filter(mainFactory_.getElkPropertyRangePropertyExpansion(
				subProperty, superProperty, range));
	}

	@Override
	public ElkSameIndividualOfEquivalentClasses getElkSameIndividualOfEquivalentClasses(
			final List<? extends ElkIndividual> same) {
		return filter(
				mainFactory_.getElkSameIndividualOfEquivalentClasses(same));
	}

	@Override
	public ElkToldAxiom getElkToldAxiom(ElkAxiom axiom) {
		return filter(mainFactory_.getElkToldAxiom(axiom));
	}

}
