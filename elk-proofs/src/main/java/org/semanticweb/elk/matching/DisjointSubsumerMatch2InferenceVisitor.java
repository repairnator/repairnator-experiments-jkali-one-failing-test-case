package org.semanticweb.elk.matching;

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

import org.semanticweb.elk.matching.conclusions.DisjointSubsumerMatch1Watch;
import org.semanticweb.elk.matching.conclusions.DisjointSubsumerMatch2;
import org.semanticweb.elk.matching.inferences.ClassInconsistencyOfDisjointSubsumersMatch1;
import org.semanticweb.elk.matching.inferences.ClassInconsistencyOfDisjointSubsumersMatch2;
import org.semanticweb.elk.matching.inferences.InferenceMatch;

class DisjointSubsumerMatch2InferenceVisitor
		extends AbstractConclusionMatchInferenceVisitor<DisjointSubsumerMatch2>
		implements DisjointSubsumerMatch1Watch.Visitor<Void> {

	DisjointSubsumerMatch2InferenceVisitor(InferenceMatch.Factory factory,
			DisjointSubsumerMatch2 child) {
		super(factory, child);
	}

	@Override
	public Void visit(
			ClassInconsistencyOfDisjointSubsumersMatch1 inferenceMatch1) {
		factory.getClassInconsistencyOfDisjointSubsumersMatch2(inferenceMatch1,
				child);
		return null;
	}

	@Override
	public Void visit(
			ClassInconsistencyOfDisjointSubsumersMatch2 inferenceMatch2) {
		factory.getClassInconsistencyOfDisjointSubsumersMatch3(inferenceMatch2,
				child);
		return null;
	}

}
