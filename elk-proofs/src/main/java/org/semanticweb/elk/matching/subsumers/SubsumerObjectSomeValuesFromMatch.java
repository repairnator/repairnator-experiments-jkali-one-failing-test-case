package org.semanticweb.elk.matching.subsumers;

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

import org.semanticweb.elk.matching.root.IndexedContextRootMatch;
import org.semanticweb.elk.matching.root.IndexedContextRootMatch.Factory;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;

public class SubsumerObjectSomeValuesFromMatch extends
		AbstractIndexedObjectSomeValuesFromMatch<ElkObjectSomeValuesFrom> {

	SubsumerObjectSomeValuesFromMatch(ElkObjectSomeValuesFrom value) {
		super(value);
	}

	@Override
	public ElkObjectPropertyExpression getPropertyMatch() {
		return getValue().getProperty();
	}

	@Override
	public ElkClassExpression getFillerMatch() {
		return getValue().getFiller();
	}

	@Override
	public IndexedContextRootMatch getFillerRootMatch(Factory factory) {
		return factory.getIndexedContextRootClassExpressionMatch(
				getValue().getFiller());
	}

	@Override
	public IndexedContextRootMatch getRangeRootMatch(Factory factory) {
		return factory
				.getIndexedContextRootClassExpressionMatch(getFillerMatch());
	}

	@Override
	public <O> O accept(IndexedObjectSomeValuesFromMatch.Visitor<O> visitor) {
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
	interface Visitor<O> {

		O visit(SubsumerObjectSomeValuesFromMatch match);

	}

}
