package org.semanticweb.elk.matching.root;

import java.util.Collections;
import java.util.List;

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

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectOneOf;

public class IndexedContextRootIndividualMatch
		extends AbstractIndexedContextRootMatch<ElkIndividual> {

	private IndexedContextRootIndividualMatch(ElkIndividual value,
			List<? extends ElkClassExpression> rangeMatches) {
		super(value, rangeMatches);
	}

	IndexedContextRootIndividualMatch(ElkIndividual value) {
		super(value);
	}

	@Override
	public ElkClassExpression getMainFillerMatch(ElkObject.Factory factory) {
		return factory.getObjectOneOf(Collections.singletonList(getValue()));
	}

	@Override
	public IndexedContextRootIndividualMatch extend(
			ElkClassExpression rangeMatch) {
		if (rangeMatch instanceof ElkObjectOneOf) {
			List<? extends ElkIndividual> individuals = ((ElkObjectOneOf) rangeMatch)
					.getIndividuals();
			if (individuals.size() == 1
					&& getValue().equals(individuals.get(0))) {
				return this;
			}
		}
		return new IndexedContextRootIndividualMatch(getValue(),
				extendRangeMatches(rangeMatch));
	}

	@Override
	public <O> O accept(IndexedContextRootMatch.Visitor<O> visitor) {
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

		O visit(IndexedContextRootIndividualMatch match);

	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		IndexedContextRootIndividualMatch getIndexedContextRootIndividualMatch(
				ElkIndividual value);

	}

}
