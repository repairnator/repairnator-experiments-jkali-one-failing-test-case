package org.semanticweb.elk.matching.conclusions;

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

import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;

public class IndexedSubObjectPropertyOfAxiomMatch2 extends
		AbstractIndexedAxiomMatch<IndexedSubObjectPropertyOfAxiomMatch1> {

	private final ElkSubObjectPropertyExpression subPropertyChainMatch_;

	private final ElkObjectProperty superPropertyMatch_;

	IndexedSubObjectPropertyOfAxiomMatch2(
			IndexedSubObjectPropertyOfAxiomMatch1 parent,
			ElkSubObjectPropertyExpression subPropertyChainMatch,
			ElkObjectProperty superPropertyMatch) {
		super(parent);
		this.subPropertyChainMatch_ = subPropertyChainMatch;
		this.superPropertyMatch_ = superPropertyMatch;
	}

	public ElkSubObjectPropertyExpression getSubPropertyChainMatch() {
		return subPropertyChainMatch_;
	}

	public ElkObjectProperty getSuperPropertyMatch() {
		return superPropertyMatch_;
	}

	@Override
	public <O> O accept(IndexedAxiomMatch.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		IndexedSubObjectPropertyOfAxiomMatch2 getIndexedSubObjectPropertyOfAxiomMatch2(
				IndexedSubObjectPropertyOfAxiomMatch1 parent,
				ElkSubObjectPropertyExpression subPropertyChainMatch,
				ElkObjectProperty superPropertyMatch);

	}

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O> {

		O visit(IndexedSubObjectPropertyOfAxiomMatch2 conclusionMatch);

	}

}
