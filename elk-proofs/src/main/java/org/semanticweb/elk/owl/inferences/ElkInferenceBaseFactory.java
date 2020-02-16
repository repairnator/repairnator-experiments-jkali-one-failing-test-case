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

public class ElkInferenceBaseFactory implements ElkInference.Factory {

	@Override
	public ElkClassAssertionOfClassInclusion getElkClassAssertionOfClassInclusion(
			final ElkIndividual instance, final ElkClassExpression type) {
		return new ElkClassAssertionOfClassInclusion(instance, type);
	}

	@Override
	public ElkClassInclusionEmptyObjectOneOfOwlNothing getElkClassInclusionEmptyObjectOneOfOwlNothing() {
		return new ElkClassInclusionEmptyObjectOneOfOwlNothing();
	}

	@Override
	public ElkClassInclusionEmptyObjectUnionOfOwlNothing getElkClassInclusionEmptyObjectUnionOfOwlNothing() {
		return new ElkClassInclusionEmptyObjectUnionOfOwlNothing();
	}

	@Override
	public ElkClassInclusionExistentialComposition getElkClassInclusionExistentialComposition(
			List<? extends ElkClassExpression> classExpressions,
			List<? extends ElkObjectPropertyExpression> subChain,
			ElkObjectPropertyExpression superProperty) {
		return new ElkClassInclusionExistentialComposition(classExpressions,
				subChain, superProperty);
	}

	@Override
	public ElkClassInclusionExistentialFillerExpansion getElkClassInclusionExistentialFillerExpansion(
			ElkObjectPropertyExpression property, ElkClassExpression subClass,
			ElkClassExpression superClass) {
		return new ElkClassInclusionExistentialFillerExpansion(property,
				subClass, superClass);
	}

	@Override
	public ElkClassInclusionExistentialOfObjectHasSelf getElkClassInclusionExistentialOfObjectHasSelf(
			ElkClassExpression subClass, ElkObjectPropertyExpression property) {
		return new ElkClassInclusionExistentialOfObjectHasSelf(subClass,
				property);
	}

	@Override
	public ElkClassInclusionExistentialOwlNothing getElkClassInclusionExistentialOwlNothing(
			ElkObjectPropertyExpression property) {
		return new ElkClassInclusionExistentialOwlNothing(property);
	}

	@Override
	public ElkClassInclusionExistentialPropertyExpansion getElkClassInclusionExistentialPropertyExpansion(
			ElkObjectPropertyExpression subProperty,
			ElkObjectPropertyExpression superProperty,
			ElkClassExpression filler) {
		return new ElkClassInclusionExistentialPropertyExpansion(subProperty,
				superProperty, filler);
	}

	@Override
	public ElkClassInclusionExistentialRange getElkClassInclusionExistentialRange(
			ElkObjectPropertyExpression property, ElkClassExpression filler,
			ElkClassExpression... ranges) {
		return new ElkClassInclusionExistentialRange(property, filler, ranges);
	}

	@Override
	public ElkClassInclusionExistentialRange getElkClassInclusionExistentialRange(
			ElkObjectPropertyExpression property, ElkClassExpression filler,
			List<? extends ElkClassExpression> ranges) {
		return new ElkClassInclusionExistentialRange(property, filler, ranges);
	}

	@Override
	public ElkClassInclusionExistentialTransitivity getElkClassInclusionExistentialTransitivity(
			ElkObjectPropertyExpression transitiveProperty,
			ElkClassExpression... classExpressions) {
		return new ElkClassInclusionExistentialTransitivity(transitiveProperty,
				classExpressions);
	}

	@Override
	public ElkClassInclusionExistentialTransitivity getElkClassInclusionExistentialTransitivity(
			ElkObjectPropertyExpression transitiveProperty,
			List<? extends ElkClassExpression> classExpressions) {
		return new ElkClassInclusionExistentialTransitivity(transitiveProperty,
				classExpressions);
	}

	@Override
	public ElkClassInclusionHierarchy getElkClassInclusionHierarchy(
			ElkClassExpression... expressions) {
		return new ElkClassInclusionHierarchy(expressions);
	}

	@Override
	public ElkClassInclusionHierarchy getElkClassInclusionHierarchy(
			List<? extends ElkClassExpression> expressions) {
		return new ElkClassInclusionHierarchy(expressions);
	}

	@Override
	public ElkClassInclusionNegationClash getElkClassInclusionNegationClash(
			ElkClassExpression expression) {
		return new ElkClassInclusionNegationClash(expression);
	}

	@Override
	public ElkClassInclusionObjectIntersectionOfComposition getElkClassInclusionObjectIntersectionOfComposition(
			ElkClassExpression subExpression, ElkClassExpression firstConjunct,
			ElkClassExpression secondConjunct) {
		return new ElkClassInclusionObjectIntersectionOfComposition(
				subExpression, firstConjunct, secondConjunct);
	}

	@Override
	public ElkClassInclusionObjectIntersectionOfComposition getElkClassInclusionObjectIntersectionOfComposition(
			ElkClassExpression subExpression,
			List<? extends ElkClassExpression> conjuncts) {
		return new ElkClassInclusionObjectIntersectionOfComposition(
				subExpression, conjuncts);
	}

	@Override
	public ElkClassInclusionObjectIntersectionOfDecomposition getElkClassInclusionObjectIntersectionOfDecomposition(
			List<? extends ElkClassExpression> conjuncts, int conjunctPos) {
		return new ElkClassInclusionObjectIntersectionOfDecomposition(conjuncts,
				conjunctPos);
	}

	@Override
	public ElkClassInclusionObjectIntersectionOfInclusion getElkClassInclusionObjectIntersectionOfInclusion(
			List<? extends ElkClassExpression> subClasses,
			List<Integer> subPositions) {
		return new ElkClassInclusionObjectIntersectionOfInclusion(subClasses,
				subPositions);
	}

	@Override
	public ElkClassInclusionObjectOneOfInclusion getElkClassInclusionObjectOneOfInclusion(
			List<? extends ElkIndividual> superIndividuals,
			List<Integer> subPositions) {
		return new ElkClassInclusionObjectOneOfInclusion(superIndividuals,
				subPositions);
	}

	@Override
	public ElkClassInclusionObjectUnionOfComposition getElkClassInclusionObjectUnionOfComposition(
			List<? extends ElkClassExpression> disjuncts, int disjunctPos) {
		return new ElkClassInclusionObjectUnionOfComposition(disjuncts,
				disjunctPos);
	}

	@Override
	public ElkClassInclusionOfClassAssertion getElkClassInclusionOfClassAssertion(
			ElkIndividual instance, ElkClassExpression type) {
		return new ElkClassInclusionOfClassAssertion(instance, type);
	}

	@Override
	public ElkClassInclusionOfDisjointClasses getElkClassInclusionOfDisjointClasses(
			List<? extends ElkClassExpression> expressions, int firstPos,
			int secondPos) {
		return new ElkClassInclusionOfDisjointClasses(expressions, firstPos,
				secondPos);
	}

	@Override
	public ElkClassInclusionOfEquivaletClasses getElkClassInclusionOfEquivaletClasses(
			ElkClassExpression first, ElkClassExpression second,
			boolean sameOrder) {
		return new ElkClassInclusionOfEquivaletClasses(first, second,
				sameOrder);
	}

	@Override
	public ElkClassInclusionOfEquivaletClasses getElkClassInclusionOfEquivaletClasses(
			List<? extends ElkClassExpression> expressions, int subPos,
			int superPos) {
		return new ElkClassInclusionOfEquivaletClasses(expressions, subPos,
				superPos);
	}

	@Override
	public ElkClassInclusionOfObjectPropertyAssertion getElkClassInclusionOfObjectPropertyAssertion(
			ElkIndividual subject, ElkObjectPropertyExpression property,
			ElkIndividual object) {
		return new ElkClassInclusionOfObjectPropertyAssertion(subject, property,
				object);
	}

	@Override
	public ElkClassInclusionOfObjectPropertyDomain getElkClassInclusionOfObjectPropertyDomain(
			ElkObjectPropertyExpression property, ElkClassExpression domain) {
		return new ElkClassInclusionOfObjectPropertyDomain(property, domain);
	}

	@Override
	public ElkClassInclusionOfReflexiveObjectProperty getElkClassInclusionOfReflexiveObjectProperty(
			ElkObjectPropertyExpression property) {
		return new ElkClassInclusionOfReflexiveObjectProperty(property);
	}

	@Override
	public ElkClassInclusionOwlBottomObjectProperty getElkClassInclusionOwlBottomObjectProperty() {
		return ElkClassInclusionOwlBottomObjectProperty.INSTANCE;
	}

	@Override
	public ElkClassInclusionOwlNothing getElkClassInclusionOwlNothing(
			ElkClassExpression superClass) {
		return new ElkClassInclusionOwlNothing(superClass);
	}

	@Override
	public ElkClassInclusionOwlThing getElkClassInclusionOwlThing(
			ElkClassExpression subClass) {
		return new ElkClassInclusionOwlThing(subClass);
	}

	@Override
	public ElkClassInclusionOwlTopObjectProperty getElkClassInclusionOwlTopObjectProperty() {
		return ElkClassInclusionOwlTopObjectProperty.INSTANCE;
	}

	@Override
	public ElkClassInclusionOwlThingEmptyObjectIntersectionOf getElkClassInclusionOwlThingEmptyObjectIntersectionOf() {
		return new ElkClassInclusionOwlThingEmptyObjectIntersectionOf();
	}

	@Override
	public ElkClassInclusionReflexivePropertyRange getElkClassInclusionReflexivePropertyRange(
			ElkClassExpression subClass, ElkObjectPropertyExpression property,
			ElkClassExpression range) {
		return new ElkClassInclusionReflexivePropertyRange(subClass, property,
				range);
	}

	@Override
	public ElkClassInclusionSingletonObjectUnionOfDecomposition getElkClassInclusionSingletonObjectUnionOfDecomposition(
			ElkClassExpression disjunct) {
		return new ElkClassInclusionSingletonObjectUnionOfDecomposition(
				disjunct);
	}

	@Override
	public ElkClassInclusionTautology getElkClassInclusionTautology(
			ElkClassExpression expression) {
		return new ElkClassInclusionTautology(expression);
	}

	@Override
	public ElkClassInclusionTopObjectHasValue getElkClassInclusionTopObjectHasValue(
			ElkIndividual value) {
		return new ElkClassInclusionTopObjectHasValue(value);
	}

	@Override
	public ElkDifferentIndividualsOfDisjointClasses getElkDifferentIndividualsOfDisjointClasses(
			final List<? extends ElkIndividual> different) {
		return new ElkDifferentIndividualsOfDisjointClasses(different);
	}

	@Override
	public ElkDisjointClassesIntersectionInconsistencies getElkDisjointClassesIntersectionInconsistencies(
			final List<? extends ElkClassExpression> expressions) {
		return new ElkDisjointClassesIntersectionInconsistencies(expressions);
	}

	@Override
	public ElkDisjointClassesIntersectionInconsistencies getElkDisjointClassesIntersectionInconsistencies(
			final ElkClassExpression... expressions) {
		return new ElkDisjointClassesIntersectionInconsistencies(expressions);
	}

	@Override
	public ElkDisjointClassesOfDifferentIndividuals getElkDisjointClassesOfDifferentIndividuals(
			List<? extends ElkIndividual> different) {
		return new ElkDisjointClassesOfDifferentIndividuals(different);
	}

	@Override
	public ElkDisjointClassesOfDisjointUnion getElkDisjointClassesOfDisjointUnion(
			ElkClass defined, List<? extends ElkClassExpression> disjoint) {
		return new ElkDisjointClassesOfDisjointUnion(defined, disjoint);
	}

	@Override
	public ElkEquivalentClassesCycle getElkEquivalentClassesCycle(
			ElkClassExpression first, ElkClassExpression second) {
		return new ElkEquivalentClassesCycle(first, second);
	}

	@Override
	public ElkEquivalentClassesCycle getElkEquivalentClassesCycle(
			List<? extends ElkClassExpression> expressions) {
		return new ElkEquivalentClassesCycle(expressions);
	}

	@Override
	public ElkEquivalentClassesObjectHasValue getElkEquivalentClassesObjectHasValue(
			ElkObjectPropertyExpression property, ElkIndividual value) {
		return new ElkEquivalentClassesObjectHasValue(property, value);
	}

	@Override
	public ElkEquivalentClassesObjectOneOf getElkEquivalentClassesObjectOneOf(
			List<? extends ElkIndividual> members) {
		return new ElkEquivalentClassesObjectOneOf(members);
	}

	@Override
	public ElkEquivalentClassesOfDisjointUnion getElkEquivalentClassesOfDisjointUnion(
			ElkClass defined, List<? extends ElkClassExpression> disjoint) {
		return new ElkEquivalentClassesOfDisjointUnion(defined, disjoint);
	}

	@Override
	public ElkEquivalentClassesOfSameIndividual getElkEquivalentClassesOfSameIndividual(
			List<? extends ElkIndividual> same) {
		return new ElkEquivalentClassesOfSameIndividual(same);
	}

	@Override
	public ElkObjectPropertyAssertionOfClassInclusion getElkObjectPropertyAssertionOfClassInclusion(
			final ElkIndividual subject,
			final ElkObjectPropertyExpression property,
			final ElkIndividual object) {
		return new ElkObjectPropertyAssertionOfClassInclusion(subject, property,
				object);
	}

	@Override
	public ElkObjectPropertyDomainOfClassInclusion getElkObjectPropertyDomainOfClassInclusion(
			final ElkObjectPropertyExpression property,
			final ElkClassExpression domain) {
		return new ElkObjectPropertyDomainOfClassInclusion(property, domain);
	}

	@Override
	public ElkPropertyInclusionHierarchy getElkPropertyInclusionHierarchy(
			ElkSubObjectPropertyExpression subExpression,
			ElkObjectPropertyExpression... expressions) {
		return new ElkPropertyInclusionHierarchy(subExpression, expressions);
	}

	@Override
	public ElkPropertyInclusionHierarchy getElkPropertyInclusionHierarchy(
			ElkSubObjectPropertyExpression subExpression,
			List<? extends ElkObjectPropertyExpression> expressions) {
		return new ElkPropertyInclusionHierarchy(subExpression, expressions);
	}

	@Override
	public ElkPropertyInclusionOfEquivalence getElkPropertyInclusionOfEquivalence(
			ElkObjectPropertyExpression first,
			ElkObjectPropertyExpression second, boolean sameOrder) {
		return new ElkPropertyInclusionOfEquivalence(first, second, sameOrder);
	}

	@Override
	public ElkPropertyInclusionOfEquivalence getElkPropertyInclusionOfEquivalence(
			List<? extends ElkObjectPropertyExpression> expressions, int subPos,
			int superPos) {
		return new ElkPropertyInclusionOfEquivalence(expressions, subPos,
				superPos);
	}

	@Override
	public ElkPropertyInclusionOfTransitiveObjectProperty getElkPropertyInclusionOfTransitiveObjectProperty(
			ElkObjectPropertyExpression property) {
		return new ElkPropertyInclusionOfTransitiveObjectProperty(property);
	}

	@Override
	public ElkPropertyInclusionTautology getElkPropertyInclusionTautology(
			ElkObjectPropertyExpression expression) {
		return new ElkPropertyInclusionTautology(expression);
	}

	@Override
	public ElkPropertyRangePropertyExpansion getElkPropertyRangePropertyExpansion(
			ElkObjectPropertyExpression subProperty,
			ElkObjectPropertyExpression superProperty,
			ElkClassExpression range) {
		return new ElkPropertyRangePropertyExpansion(subProperty, superProperty,
				range);
	}

	@Override
	public ElkSameIndividualOfEquivalentClasses getElkSameIndividualOfEquivalentClasses(
			final List<? extends ElkIndividual> same) {
		return new ElkSameIndividualOfEquivalentClasses(same);
	}

	@Override
	public ElkToldAxiom getElkToldAxiom(ElkAxiom axiom) {
		return new ElkToldAxiom(axiom);
	}

}
