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

import org.liveontologies.puli.Inference;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObject;

public interface ElkInference extends Inference<ElkAxiom> {

	@Override
	String getName();

	/**
	 * @return the number of premises of this inference
	 */
	int getPremiseCount();

	/**
	 * @param index
	 *            index of the premise to return
	 * @param factory
	 *            the factory for creating the premises
	 * @return the premise at the specified position in this inference.
	 * @throws IndexOutOfBoundsException
	 *             if the position is out of range (
	 *             <tt>index &lt; 0 || index &gt;= size()</tt>)
	 */
	ElkAxiom getPremise(int index, ElkObject.Factory factory);

	/**
	 * @param factory
	 *            the factory for creating conclusion
	 * @return the conclusion of this inference
	 */
	ElkAxiom getConclusion(ElkObject.Factory factory);

	<O> O accept(Visitor<O> visitor);

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 * @author Peter Skocovsky
	 */
	interface Factory extends ElkClassAssertionOfClassInclusion.Factory,
			ElkClassInclusionOwlThingEmptyObjectIntersectionOf.Factory,
			ElkClassInclusionEmptyObjectOneOfOwlNothing.Factory,
			ElkClassInclusionEmptyObjectUnionOfOwlNothing.Factory,
			ElkClassInclusionExistentialComposition.Factory,
			ElkClassInclusionExistentialFillerExpansion.Factory,
			ElkClassInclusionExistentialOfObjectHasSelf.Factory,
			ElkClassInclusionExistentialOwlNothing.Factory,
			ElkClassInclusionExistentialPropertyExpansion.Factory,
			ElkClassInclusionExistentialRange.Factory,
			ElkClassInclusionExistentialTransitivity.Factory,
			ElkClassInclusionHierarchy.Factory,
			ElkClassInclusionNegationClash.Factory,
			ElkClassInclusionObjectIntersectionOfComposition.Factory,
			ElkClassInclusionObjectIntersectionOfDecomposition.Factory,
			ElkClassInclusionObjectIntersectionOfInclusion.Factory,
			ElkClassInclusionObjectOneOfInclusion.Factory,
			ElkClassInclusionObjectUnionOfComposition.Factory,
			ElkClassInclusionOfClassAssertion.Factory,
			ElkClassInclusionOfDisjointClasses.Factory,
			ElkClassInclusionOfEquivaletClasses.Factory,
			ElkClassInclusionOfObjectPropertyAssertion.Factory,
			ElkClassInclusionOfObjectPropertyDomain.Factory,
			ElkClassInclusionOfReflexiveObjectProperty.Factory,
			ElkClassInclusionOwlBottomObjectProperty.Factory,
			ElkClassInclusionOwlNothing.Factory,
			ElkClassInclusionOwlThing.Factory,
			ElkClassInclusionOwlTopObjectProperty.Factory,
			ElkClassInclusionReflexivePropertyRange.Factory,
			ElkClassInclusionSingletonObjectUnionOfDecomposition.Factory,
			ElkClassInclusionTautology.Factory,
			ElkClassInclusionTopObjectHasValue.Factory,
			ElkDifferentIndividualsOfDisjointClasses.Factory,
			ElkDisjointClassesIntersectionInconsistencies.Factory,
			ElkDisjointClassesOfDifferentIndividuals.Factory,
			ElkDisjointClassesOfDisjointUnion.Factory,
			ElkEquivalentClassesCycle.Factory,
			ElkEquivalentClassesObjectHasValue.Factory,
			ElkEquivalentClassesObjectOneOf.Factory,
			ElkEquivalentClassesOfDisjointUnion.Factory,
			ElkEquivalentClassesOfSameIndividual.Factory,
			ElkObjectPropertyAssertionOfClassInclusion.Factory,
			ElkObjectPropertyDomainOfClassInclusion.Factory,
			ElkPropertyInclusionHierarchy.Factory,
			ElkPropertyInclusionOfEquivalence.Factory,
			ElkPropertyInclusionOfTransitiveObjectProperty.Factory,
			ElkPropertyInclusionTautology.Factory,
			ElkPropertyRangePropertyExpansion.Factory,
			ElkSameIndividualOfEquivalentClasses.Factory, ElkToldAxiom.Factory {

		// combined interface

	}

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 * @author Peter Skocovsky
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O> extends ElkClassAssertionOfClassInclusion.Visitor<O>,
			ElkClassInclusionOwlThingEmptyObjectIntersectionOf.Visitor<O>,
			ElkClassInclusionEmptyObjectOneOfOwlNothing.Visitor<O>,
			ElkClassInclusionEmptyObjectUnionOfOwlNothing.Visitor<O>,
			ElkClassInclusionExistentialComposition.Visitor<O>,
			ElkClassInclusionExistentialFillerExpansion.Visitor<O>,
			ElkClassInclusionExistentialOfObjectHasSelf.Visitor<O>,
			ElkClassInclusionExistentialOwlNothing.Visitor<O>,
			ElkClassInclusionExistentialPropertyExpansion.Visitor<O>,
			ElkClassInclusionExistentialRange.Visitor<O>,
			ElkClassInclusionExistentialTransitivity.Visitor<O>,
			ElkClassInclusionHierarchy.Visitor<O>,
			ElkClassInclusionNegationClash.Visitor<O>,
			ElkClassInclusionObjectIntersectionOfComposition.Visitor<O>,
			ElkClassInclusionObjectIntersectionOfDecomposition.Visitor<O>,
			ElkClassInclusionObjectIntersectionOfInclusion.Visitor<O>,
			ElkClassInclusionObjectOneOfInclusion.Visitor<O>,
			ElkClassInclusionObjectUnionOfComposition.Visitor<O>,
			ElkClassInclusionOfClassAssertion.Visitor<O>,
			ElkClassInclusionOfDisjointClasses.Visitor<O>,
			ElkClassInclusionOfEquivaletClasses.Visitor<O>,
			ElkClassInclusionOfObjectPropertyAssertion.Visitor<O>,
			ElkClassInclusionOfObjectPropertyDomain.Visitor<O>,
			ElkClassInclusionOfReflexiveObjectProperty.Visitor<O>,
			ElkClassInclusionOwlBottomObjectProperty.Visitor<O>,
			ElkClassInclusionOwlNothing.Visitor<O>,
			ElkClassInclusionOwlThing.Visitor<O>,
			ElkClassInclusionOwlTopObjectProperty.Visitor<O>,
			ElkClassInclusionReflexivePropertyRange.Visitor<O>,
			ElkClassInclusionSingletonObjectUnionOfDecomposition.Visitor<O>,
			ElkClassInclusionTautology.Visitor<O>,
			ElkClassInclusionTopObjectHasValue.Visitor<O>,
			ElkDifferentIndividualsOfDisjointClasses.Visitor<O>,
			ElkDisjointClassesIntersectionInconsistencies.Visitor<O>,
			ElkDisjointClassesOfDifferentIndividuals.Visitor<O>,
			ElkDisjointClassesOfDisjointUnion.Visitor<O>,
			ElkEquivalentClassesCycle.Visitor<O>,
			ElkEquivalentClassesObjectHasValue.Visitor<O>,
			ElkEquivalentClassesObjectOneOf.Visitor<O>,
			ElkEquivalentClassesOfDisjointUnion.Visitor<O>,
			ElkEquivalentClassesOfSameIndividual.Visitor<O>,
			ElkObjectPropertyAssertionOfClassInclusion.Visitor<O>,
			ElkObjectPropertyDomainOfClassInclusion.Visitor<O>,
			ElkPropertyInclusionHierarchy.Visitor<O>,
			ElkPropertyInclusionOfEquivalence.Visitor<O>,
			ElkPropertyInclusionOfTransitiveObjectProperty.Visitor<O>,
			ElkPropertyInclusionTautology.Visitor<O>,
			ElkPropertyRangePropertyExpansion.Visitor<O>,
			ElkSameIndividualOfEquivalentClasses.Visitor<O>,
			ElkToldAxiom.Visitor<O> {

		// combined interface

	}

}
