/**
 * 
 */
package org.semanticweb.elk.owlapi.proofs;
/*
 * #%L
 * ELK Proofs Package
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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.liveontologies.puli.ProofNode;
import org.liveontologies.puli.ProofStep;
import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * 
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 * 
 * @author Yevgeny Kazakov
 */
public class ProofExplorer {

	/**
	 * listens and controls how nodes and inferences are visited
	 */
	private final Controller controller_;

	/**
	 * nodes to be visited yet
	 */
	private final Queue<ProofNode<OWLAxiom>> toDo_ = new LinkedList<ProofNode<OWLAxiom>>();

	/**
	 * all nodes ever encountered
	 */
	private final Set<ProofNode<OWLAxiom>> all_ = new HashSet<ProofNode<OWLAxiom>>();

	ProofExplorer(ProofNode<OWLAxiom> root, Controller listener) {
		this.controller_ = listener;
		toDo(root);
	}

	boolean toDo(ProofNode<OWLAxiom> node) {
		if (all_.add(node)) {
			toDo_.add(node);
			return true;
		}
		// else
		return false;
	}

	void process() {
		for (;;) {
			ProofNode<OWLAxiom> next = toDo_.poll();

			if (next == null) {
				break;
			}

			for (ProofStep<OWLAxiom> inf : next.getInferences()) {

				// recursively unwind premise inferences
				for (ProofNode<OWLAxiom> premise : inf.getPremises()) {
					// proof reader guarantees equality for structurally
					// equivalent expressions so we avoid infinite loops here
					toDo(premise);
				}

				// pass to the client
				if (controller_.inferenceVisited(inf)) {
					break;
				}
			}

			if (controller_.nodeVisited(next)) {
				break;
			}
		}
	}

	public static void visitInferences(ProofNode<OWLAxiom> root,
			Controller controller) {
		new ProofExplorer(root, controller).process();
	}

	static interface Controller {

		/**
		 * Signals that a node is visited
		 * 
		 * @param node
		 * @return {@code true} if no further nodes should be visited and
		 *         {@code false} otherwise
		 */
		boolean nodeVisited(ProofNode<OWLAxiom> node);

		/**
		 * Signals that an inference of a node is visited
		 * 
		 * @param inference
		 * @return {@code true} if no further inferences of this node should be
		 *         visited and {@code false} otherwise
		 */
		boolean inferenceVisited(ProofStep<OWLAxiom> inference);

	}
}
