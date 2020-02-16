/**
 * 
 */
package org.semanticweb.elk.benchmark.tracing;
/*
 * #%L
 * ELK Benchmarking Package
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.predefined.PredefinedElkIri;
import org.semanticweb.elk.reasoner.saturation.tracing.TracingTestVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.TracingTests;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNode;

/**
 * Randomly selects some subsumption tasks. Does not guarantee uniqueness.
 * Visits related tracing tasks, e.g. all subsumptions for a class and its
 * direct subclasses.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class RandomSubsumptionTracingTests implements TracingTests {

	private final Taxonomy<ElkClass> classTaxonomy_;
	
	private final int tasksNo_;
	
	private final Random rnd_;
	
	public RandomSubsumptionTracingTests(Taxonomy<ElkClass> tax, int numOfTasks, long seed) {
		classTaxonomy_ = tax;
		tasksNo_ = numOfTasks;
		rnd_ = new Random(seed);
	}
	
	@Override
	public void accept(TracingTestVisitor visitor) throws Exception {
		List<TaxonomyNode<ElkClass>> allNodes = new ArrayList<TaxonomyNode<ElkClass>>(classTaxonomy_.getNodes());
		int count = 0;
		
		while (count <= tasksNo_) {
			TaxonomyNode<ElkClass> next = allNodes.get(rnd_.nextInt(allNodes.size()));
			// visit all subsumptions from this class and its subclasses
			count += visitEquivalentClassesTracingTasks(next, visitor);
			
			for (TaxonomyNode<ElkClass> subNode : next.getDirectSubNodes()) {
				if (subNode != classTaxonomy_.getBottomNode() && next != classTaxonomy_.getTopNode()) {
					count += visitTracingTasksForDirectSubClasses(subNode, next, visitor);
				}
			}
		}
	}
	
	private int visitTracingTasksForDirectSubClasses(
			TaxonomyNode<ElkClass> node, TaxonomyNode<ElkClass> superNode,
			TracingTestVisitor visitor) throws Exception {
		int count = 0;
		
		for (ElkClass sub : node.getMembers()) {
			if (sub.getIri().equals(PredefinedElkIri.OWL_NOTHING.get())) {
				continue;
			}
			
			for (ElkClass sup : superNode.getMembers()) {
				if (sup.getIri().equals(PredefinedElkIri.OWL_THING.get())) {
					continue;
				}
				
				if (sub != sup) {
					visitor.subsumptionTest(sub, sup);
					count++;
				}
			}
		}
		
		return count;
	}

	private int  visitEquivalentClassesTracingTasks(TaxonomyNode<ElkClass> node, TracingTestVisitor visitor) throws Exception {
		return visitTracingTasksForDirectSubClasses(node, node, visitor);
	}

}
