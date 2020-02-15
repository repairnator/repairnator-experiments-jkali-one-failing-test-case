/*******************************************************************************
 * Copyright (c) 2010, 2012 Institute for Dutch Lexicology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package nl.inl.blacklab.search.lucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.spans.SpanWeight;

import nl.inl.blacklab.search.fimatch.ForwardIndexAccessor;
import nl.inl.blacklab.search.fimatch.NfaFragment;
import nl.inl.blacklab.search.lucene.optimize.ClauseCombiner;

/**
 * Combines spans, keeping only combinations of hits that occur one after the other. The order is
 * significant: a hit from the first span must be followed by a hit from the second.
 *
 * Note that this class is different from org.apache.lucene.search.spans.SpanNearQuery: it tries to
 * make sure it generates *all* possible sequence matches. SpanNearQuery doesn't do this; once a hit
 * is used in a SpanNearQuery match, it advances to the next hit.
 *
 * In the future, this class could be expanded to make the exact behaviour configurable: find all
 * matches / find longest matches / find shortest matches / ...
 *
 * See SpanSequenceRaw for details on the matching process.
 */
public class SpanQuerySequence extends BLSpanQueryAbstract {

	public SpanQuerySequence(BLSpanQuery first, BLSpanQuery second) {
		super(first, second);
	}

	public SpanQuerySequence(Collection<BLSpanQuery> clauscol) {
		super(clauscol);
	}

	public SpanQuerySequence(BLSpanQuery[] _clauses) {
		super(_clauses);
	}

	/**
	 * Flatten nested sequences in clauses array.
	 *
	 * Flattens in-place.
	 *
	 * @param clauses clauses which may need flattening
	 * @return true if any rewriting was done, false if not
	 */
	private static boolean flattenSequence(List<BLSpanQuery> clauses) {
		boolean anyRewritten = false;
		for (int i = 0; i < clauses.size(); i++) {
			BLSpanQuery child = clauses.get(i);
			if (child instanceof SpanQuerySequence) {
				clauses.remove(i);
				clauses.addAll(i, ((SpanQuerySequence)child).getClauses());
				anyRewritten = true;
			}
		}
		return anyRewritten;
	}

	/**
	 * Try to match separate start and end tags in this sequence, and convert into a
     * position filter (e.g. containing) query.
     *
     * For example:
     *  <s> []* 'bla' []* </s> ==> <s/> containing 'bla'
     *
	 * @param clauses clauses in which to find matching tags
	 * @return true if any rewriting was done, false if not
	 */
	protected boolean matchingTagsToPosFilter(List<BLSpanQuery> clauses) {
		boolean anyRewritten = false;

		// Try to match separate start and end tags in this sequence, and convert into a
		// containing query. (<s> []* 'bla' []* </s> ==> <s/> containing 'bla')
		for (int i = 0; i < clauses.size(); i++) {
			BLSpanQuery clause = clauses.get(i);
			if (clause instanceof SpanQueryEdge) {
				SpanQueryEdge start = (SpanQueryEdge)clause;
				if (!start.isRightEdge()) {
					String tagName = start.getElementName();
					if (tagName != null) {
						// Start tag found. Is there a matching end tag?
						for (int j = i + 1; j < clauses.size(); j++) {
							BLSpanQuery clause2 = clauses.get(j);
							if (clause2 instanceof SpanQueryEdge) {
								SpanQueryEdge end = (SpanQueryEdge)clause2;
								if (end.isRightEdge() && end.getElementName().equals(tagName)) {
									// Found start and end tags in sequence. Convert to containing query.
									List<BLSpanQuery> search = new ArrayList<>();
									clauses.remove(i); // start tag
									for (int k = 0; k < j - i - 1; k++) {
										search.add(clauses.remove(i));
									}
									clauses.remove(i); // end tag
									boolean startAny = false;
									if (search.get(0) instanceof SpanQueryAnyToken) {
										SpanQueryAnyToken any1 = (SpanQueryAnyToken)search.get(0);
										if (any1.hitsLengthMin() == 0 && any1.hitsLengthMax() == MAX_UNLIMITED) {
											startAny = true;
											search.remove(0);
										}
									}
									boolean endAny = false;
									int last = search.size() - 1;
									if (search.get(last) instanceof SpanQueryAnyToken) {
										SpanQueryAnyToken any2 = (SpanQueryAnyToken)search.get(last);
										if (any2.hitsLengthMin() == 0 && any2.hitsLengthMax() == MAX_UNLIMITED) {
											endAny = true;
											search.remove(last);
										}
									}
									BLSpanQuery producer = start.getClause();
									BLSpanQuery filter = new SpanQuerySequence(search.toArray(new BLSpanQuery[0]));
									SpanQueryPositionFilter.Operation op;
									if (startAny) {
										if (endAny) {
											op = SpanQueryPositionFilter.Operation.CONTAINING;
										} else {
											op = SpanQueryPositionFilter.Operation.CONTAINING_AT_END;
										}
									} else {
										if (endAny) {
											op = SpanQueryPositionFilter.Operation.CONTAINING_AT_START;
										} else {
											op = SpanQueryPositionFilter.Operation.MATCHES;
										}
									}
									clauses.add(i, new SpanQueryPositionFilter(producer, filter, op, false));
									anyRewritten = true;
								}
							}
						}
					}
				}
			}
		}
		return anyRewritten;
	}

	static boolean rewriteClauses(List<BLSpanQuery> clauses, IndexReader reader) throws IOException {
		boolean anyRewritten = false;
		// Rewrite all clauses and flatten again if necessary.
		for (int i = 0; i < clauses.size(); i++) {
			BLSpanQuery child = clauses.get(i);
			BLSpanQuery rewritten = child.rewrite(reader);
			if (child != rewritten) {
				// Replace the child with the rewritten version
				anyRewritten = true;
				clauses.set(i, rewritten);
			}
		}
		return anyRewritten;
	}

	static boolean combineAdjacentClauses(List<BLSpanQuery> cl, IndexReader reader, String fieldName) throws IOException {

		boolean anyRewritten = false;

		// Rewrite adjacent clauses according to rewriting precedence rules
		boolean anyRewrittenThisCycle = true;
		while (anyRewrittenThisCycle) {
			anyRewrittenThisCycle = false;

			// Find the highest-priority rewrite possible
			int highestPrio = ClauseCombiner.CANNOT_COMBINE, highestPrioIndex = -1;
			ClauseCombiner highestPrioCombiner = null;
			BLSpanQuery left, right;
			for (int i = 1; i < cl.size(); i++) {
				// See if any combiners apply, and if the priority is higher than found so far.
				left = cl.get(i - 1);
				right = cl.get(i);
				for (ClauseCombiner combiner: ClauseCombiner.getAll()) {
					int prio = combiner.priority(left, right, reader);
					if (prio < highestPrio) {
						highestPrio = prio;
						highestPrioIndex = i;
						highestPrioCombiner = combiner;
					}
				}
			}
			// Any combiners found?
			if (highestPrio < ClauseCombiner.CANNOT_COMBINE) {
				// Yes, execute the highest-prio combiner
				left = cl.get(highestPrioIndex - 1);
				right = cl.get(highestPrioIndex);
				BLSpanQuery combined = highestPrioCombiner.combine(left, right, reader);
				combined = combined.rewrite(reader); // just to be safe
				cl.remove(highestPrioIndex);
				cl.set(highestPrioIndex - 1, combined);
				anyRewrittenThisCycle = true;
			}
			if (anyRewrittenThisCycle)
				anyRewritten = true;
		}

		return anyRewritten;
	}

	@Override
	public BLSpanQuery rewrite(IndexReader reader) throws IOException {

		boolean anyRewritten = false;

		// Make a copy, because our methods rewrite things in-place.
		List<BLSpanQuery> cl = new ArrayList<>(clauses);

		// Flatten nested sequences.
		// This doesn't change the query because the sequence operator is associative.
		anyRewritten |= flattenSequence(cl);

		// Find matching tags and rewrite them to position filter (e.g. containing) to execute more efficiently
		anyRewritten |= matchingTagsToPosFilter(cl);

		// Rewrite each clause
		anyRewritten |= rewriteClauses(cl, reader);

		// Re-flatten in case a rewrite generated a new nested sequence
		if (anyRewritten)
			flattenSequence(cl);

		// Try to combine adjacent clauses into more efficient ones
		anyRewritten |= combineAdjacentClauses(cl, reader, getField());

		// If any part of the sequence matches the empty sequence, we must
		// rewrite it to several alternatives combined with OR. Do so now.
		List<List<BLSpanQuery>> results = makeAlternatives(cl, reader);
		if (results.size() == 1 && !anyRewritten)
			return this;
		List<BLSpanQuery> orCl = new ArrayList<>();
		for (List<BLSpanQuery> seq: results) {
			if (seq.size() == 1)
				orCl.add(seq.get(0));
			else
				orCl.add(new SpanQuerySequence(seq.toArray(new BLSpanQuery[0])));
		}
		if (orCl.size() == 1)
			return orCl.get(0);
		return new BLSpanOrQuery(orCl.toArray(new BLSpanQuery[0])).rewrite(reader);
	}

	/**
	 * Given translated clauses, builds several alternatives and combines them with OR.
	 *
	 * This is necessary because of how sequence matching works: first the hits in each
	 * of the clauses are located, then we try to detect valid sequences by looking at these
	 * hits. But when a clause also matches the empty sequence, you may miss valid sequence
	 * matches because there's no hit in the clause to combine with the hits from other clauses.
	 *
	 * @param alternatives the alternative sequences we have built so far
	 * @param parts translation results for each of the clauses so far
	 * @param reader the index reader
	 * @return several alternatives combined with or
	 * @throws IOException
	 */
	List<List<BLSpanQuery>> makeAlternatives(List<BLSpanQuery> parts, IndexReader reader) throws IOException {
		if (parts.size() == 1) {
			// Last clause in the sequence; just return it
			// (noEmpty() version because we will build alternatives
			//  in the caller if the input matched the empty sequence)
			return Arrays.asList(Arrays.asList(parts.get(0).noEmpty().rewrite(reader)));
		}

		// Recursively determine the query for the "tail" of the list,
		// and whether it matches the empty sequence or not.
		List<BLSpanQuery> partsTail = parts.subList(1, parts.size());
		boolean restMatchesEmpty = true;
		for (BLSpanQuery part: partsTail) {
			if (!part.matchesEmptySequence()) {
				restMatchesEmpty = false;
				break;
			}
		}
		List<List<BLSpanQuery>> altTail = makeAlternatives(partsTail, reader);

		// Now, add the head part and check if that matches the empty sequence.
		return combine(parts.get(0), altTail, restMatchesEmpty, reader);
	}

	private static List<List<BLSpanQuery>> combine(BLSpanQuery head,
			List<List<BLSpanQuery>> tailAlts, boolean tailMatchesEmpty, IndexReader reader) throws IOException {
		List<List<BLSpanQuery>> results = new ArrayList<>();
		BLSpanQuery headNoEmpty = head.noEmpty().rewrite(reader);
		boolean headMatchesEmpty = head.matchesEmptySequence();
		for (List<BLSpanQuery> tailAlt: tailAlts) {
			// Add head in front of each tail alternative
			List<BLSpanQuery> n = new ArrayList<>(tailAlt);
			n.add(0, headNoEmpty);
			results.add(n);

			// If head can be empty, also add original tail alternative
			if (headMatchesEmpty)
				results.add(tailAlt);
		}
		// If tail can be empty, also add the head separately
		if (tailMatchesEmpty)
			results.add(Arrays.asList(headNoEmpty));
		return results;
	}

	@Override
	public boolean matchesEmptySequence() {
		for (BLSpanQuery cl: clauses) {
			if (!cl.matchesEmptySequence())
				return false;
		}
		return true;
	}

	@Override
	public BLSpanQuery noEmpty() {
		if (!matchesEmptySequence())
			return this;
		throw new RuntimeException("Sequence should have been rewritten!");
	}

	@Override
	public BLSpanWeight createWeight(IndexSearcher searcher, boolean needsScores) throws IOException {
		List<BLSpanWeight> weights = new ArrayList<>();
		for (BLSpanQuery clause: clauses) {
			weights.add(clause.createWeight(searcher, needsScores));
		}
		Map<Term, TermContext> contexts = needsScores ? getTermContexts(weights.toArray(new SpanWeight[0])) : null;
		return new SpanWeightSequence(weights, searcher, contexts);
	}

	public class SpanWeightSequence extends BLSpanWeight {

		final List<BLSpanWeight> weights;

		public SpanWeightSequence(List<BLSpanWeight> weights, IndexSearcher searcher, Map<Term, TermContext> terms) throws IOException {
			super(SpanQuerySequence.this, searcher, terms);
			this.weights = weights;
		}

		@Override
		public void extractTerms(Set<Term> terms) {
			for (SpanWeight weight: weights) {
				weight.extractTerms(terms);
			}
		}

		@Override
		public void extractTermContexts(Map<Term, TermContext> contexts) {
			for (SpanWeight weight: weights) {
				weight.extractTermContexts(contexts);
			}
		}

		@Override
		public BLSpans getSpans(final LeafReaderContext context, Postings requiredPostings) throws IOException {
			BLSpans combi = weights.get(0).getSpans(context, requiredPostings);
			if (combi == null)
				return null;
			boolean combiUniqueEnds = clauses.get(0).hitsHaveUniqueEnd();
			boolean combiEndpointSorted = clauses.get(0).hitsEndPointSorted();
			for (int i = 1; i < weights.size(); i++) {
				BLSpanWeight weight = weights.get(i);
				BLSpans si = weight.getSpans(context, requiredPostings);
				if (si == null)
					return null;

				// Note: the spans coming from SequenceSpansRaw are not sorted by end point.
				// This is okay in this loop because combi is used as the left part of the next
				// sequence (so it is explicitly sorted by end point when we put it back in
				// SequenceSpansRaw for the next part of the sequence), but before returning the
				// final spans, we wrap it in a per-document (start-point) sorter.
				if (combiUniqueEnds && combiEndpointSorted &&
					clauses.get(i).hitsStartPointSorted() && clauses.get(i).hitsHaveUniqueStart()) {
					// We can take a shortcut because of what we know about the Spans we're combining.
					combi = new SpansSequenceSimple(combi, si);
					combiEndpointSorted = clauses.get(i).hitsAllSameLength();
					combiUniqueEnds = clauses.get(i).hitsHaveUniqueEnd();
				} else {
					if (!combiEndpointSorted)
						combi = new PerDocumentSortedSpans(combi, PerDocumentSortedSpans.cmpEndPoint, false);
					if (!clauses.get(i).hitsStartPointSorted())
						si = new PerDocumentSortedSpans(si, PerDocumentSortedSpans.cmpStartPoint, false);
					combi = new SpansSequenceRaw(combi, si);
					combiUniqueEnds = combiUniqueEnds && clauses.get(i).hitsHaveUniqueEnd();
					combiEndpointSorted = clauses.get(i).hitsAllSameLength();
				}
			}

			return combi;
		}

	}

	@Override
	public String toString(String field) {
		return "SEQ(" + clausesToString(field) + ")";
	}

	@Override
	public boolean hitsAllSameLength() {
		for (BLSpanQuery clause: clauses) {
			if (!clause.hitsAllSameLength())
				return false;
		}
		return true;
	}

	@Override
	public int hitsLengthMin() {
		int n = 0;
		for (BLSpanQuery clause: clauses) {
			n += clause.hitsLengthMin();
		}
		return n;
	}

	@Override
	public int hitsLengthMax() {
		int n = 0;
		for (BLSpanQuery clause: clauses) {
			int max = clause.hitsLengthMax();
			if (max == Integer.MAX_VALUE)
				return max; // infinite
			n += max;
		}
		return n;
	}

	@Override
	public boolean hitsEndPointSorted() {
		for (int i = 0; i < clauses.size() - 1; i++) {
			if (!clauses.get(i).hitsHaveUniqueEnd())
				return false;
		}
		for (int i = 1; i < clauses.size(); i++) {
			if (!clauses.get(i).hitsAllSameLength())
				return false;
		}
		return true;
	}

	@Override
	public boolean hitsStartPointSorted() {
		for (int i = 0; i < clauses.size() - 1; i++) {
			if (!clauses.get(i).hitsAllSameLength())
				return false;
		}
		return true;
	}

	@Override
	public boolean hitsHaveUniqueStart() {
		for (BLSpanQuery clause: clauses) {
			if (!clause.hitsHaveUniqueStart())
				return false;
		}
		return true;
	}

	@Override
	public boolean hitsHaveUniqueEnd() {
		for (BLSpanQuery clause: clauses) {
			if (!clause.hitsHaveUniqueEnd())
				return false;
		}
		return true;

	}

	@Override
	public boolean hitsAreUnique() {
		return hitsHaveUniqueStart() || hitsHaveUniqueEnd();
	}

	@Override
	public NfaFragment getNfa(ForwardIndexAccessor fiAccessor, int direction) {
		NfaFragment frag = null;
		int start = direction == 1 ? 0 : clauses.size() - 1;
		int end   = direction == 1 ? clauses.size() : -1;
		for (int i = start; i != end; i += direction) {
			BLSpanQuery clause = clauses.get(i);
			if (frag == null)
				frag = clause.getNfa(fiAccessor, direction);
			else
				frag.append(clause.getNfa(fiAccessor, direction));
		}
		return frag;
	}

	@Override
	public boolean canMakeNfa() {
		for (BLSpanQuery clause: clauses) {
			if (!clause.canMakeNfa())
				return false;
		}
		return true;
	}

	@Override
	public long estimatedNumberOfHits(IndexReader reader) {
		long cost = Integer.MAX_VALUE;
		for (BLSpanQuery clause: clauses) {
			cost = Math.min(cost, clause.estimatedNumberOfHits(reader));
		}
		return cost;
	}

	/**
	 * Create a new sequence with a clause added to it.
	 *
	 * @param clause clause to add
	 * @param addToRight if true, add to the right; if false, to the left
	 * @return new sequence with clause added
	 */
	public SpanQuerySequence internalize(BLSpanQuery clause, boolean addToRight) {
		List<BLSpanQuery> cl = new ArrayList<>(clauses);
		if (addToRight)
			cl.add(clause);
		else
			cl.add(0, clause);
		return new SpanQuerySequence(cl);
	}

	/**
	 * Either add a clause to an existing SpanQuerySequence, or create a new
	 * SpanQuerySequence with the two specified clauses.
	 *
	 * @param whereToInternalize existing sequence, or existing non-sequence clause
	 * @param clauseToInternalize clause to add to sequence or add to existing clause
	 * @param addToRight if true, add new clause to the right of existing; if false, to the left
	 * @return the expanded or newly created sequence
	 */
	public static SpanQuerySequence sequenceInternalize(BLSpanQuery whereToInternalize, BLSpanQuery clauseToInternalize, boolean addToRight) {
		SpanQuerySequence seq;
		if (whereToInternalize instanceof SpanQuerySequence) {
			seq = (SpanQuerySequence)whereToInternalize;
			seq = seq.internalize(clauseToInternalize, addToRight);
		} else {
			if (addToRight)
				seq = new SpanQuerySequence(whereToInternalize, clauseToInternalize);
			else
				seq = new SpanQuerySequence(clauseToInternalize, whereToInternalize);
		}
		return seq;
	}
}
