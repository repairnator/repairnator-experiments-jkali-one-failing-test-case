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

import org.semanticweb.elk.matching.ElkMatchException;
import org.semanticweb.elk.matching.conclusions.ConclusionMatchExpressionFactory;
import org.semanticweb.elk.matching.conclusions.PropertyRangeMatch1;
import org.semanticweb.elk.matching.conclusions.PropertyRangeMatch1Watch;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionDecomposedMatch2;
import org.semanticweb.elk.matching.root.IndexedContextRootMatch;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasSelf;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;

public class SubClassInclusionObjectHasSelfPropertyRangeMatch2 extends
		AbstractInferenceMatch<SubClassInclusionObjectHasSelfPropertyRangeMatch1>
		implements PropertyRangeMatch1Watch {

	private final IndexedContextRootMatch extendedOriginMatch_;

	private final ElkObjectProperty propertyMatch_;

	SubClassInclusionObjectHasSelfPropertyRangeMatch2(
			SubClassInclusionObjectHasSelfPropertyRangeMatch1 parent,
			SubClassInclusionDecomposedMatch2 firstPremiseMatch) {
		super(parent);
		this.extendedOriginMatch_ = firstPremiseMatch
				.getExtendedDestinationMatch();
		ElkObjectHasSelf premiseSubsumerMatch = firstPremiseMatch
				.getSubsumerIndexedObjectHasSelfMatch();
		ElkObjectPropertyExpression premisePropertyMatch = premiseSubsumerMatch
				.getProperty();
		if (premisePropertyMatch instanceof ElkObjectProperty) {
			this.propertyMatch_ = (ElkObjectProperty) premisePropertyMatch;
		} else {
			throw new ElkMatchException(parent.getParent().getSubsumer(),
					premiseSubsumerMatch);
		}
		checkEquals(firstPremiseMatch, getFirstPremiseMatch(DEBUG_FACTORY));
	}

	public IndexedContextRootMatch getExtendedOriginMatch() {
		return extendedOriginMatch_;
	}

	public ElkObjectProperty getPropertyMatch() {
		return propertyMatch_;
	}

	SubClassInclusionDecomposedMatch2 getFirstPremiseMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getSubClassInclusionDecomposedMatch2(
				getParent().getFirstPremiseMatch(factory),
				getExtendedOriginMatch(),
				factory.getObjectHasSelf(getPropertyMatch()));
	}

	public PropertyRangeMatch1 getSecondPremiseMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getPropertyRangeMatch1(
				getParent().getParent().getSecondPremise(factory));
	}

	@Override
	public <O> O accept(InferenceMatch.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(PropertyRangeMatch1Watch.Visitor<O> visitor) {
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

		O visit(SubClassInclusionObjectHasSelfPropertyRangeMatch2 inferenceMatch2);

	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		SubClassInclusionObjectHasSelfPropertyRangeMatch2 getSubClassInclusionObjectHasSelfPropertyRangeMatch2(
				SubClassInclusionObjectHasSelfPropertyRangeMatch1 parent,
				SubClassInclusionDecomposedMatch2 firstPremiseMatch);

	}

}
