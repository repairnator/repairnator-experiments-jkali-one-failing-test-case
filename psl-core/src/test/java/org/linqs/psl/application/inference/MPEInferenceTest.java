package org.linqs.psl.application.inference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.linqs.psl.TestModelFactory;
import org.linqs.psl.application.inference.MPEInference;
import org.linqs.psl.database.Database;
import org.linqs.psl.model.Model;
import org.linqs.psl.model.atom.QueryAtom;
import org.linqs.psl.model.formula.Conjunction;
import org.linqs.psl.model.formula.Implication;
import org.linqs.psl.model.predicate.StandardPredicate;
import org.linqs.psl.model.rule.arithmetic.WeightedArithmeticRule;
import org.linqs.psl.model.rule.arithmetic.expression.ArithmeticRuleExpression;
import org.linqs.psl.model.rule.arithmetic.expression.SummationAtomOrAtom;
import org.linqs.psl.model.rule.arithmetic.expression.coefficient.Coefficient;
import org.linqs.psl.model.rule.arithmetic.expression.coefficient.ConstantNumber;
import org.linqs.psl.model.rule.logical.WeightedLogicalRule;
import org.linqs.psl.model.term.Variable;
import org.linqs.psl.reasoner.function.FunctionComparator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MPEInferenceTest {
	/**
	 * A quick test that only checks to see if MPEInference is running.
	 * This is not a targeted or exhaustive test, just a starting point.
	 */
	@Test
	public void baseTest() {
		TestModelFactory.ModelInformation info = TestModelFactory.getModel();

		Set<StandardPredicate> toClose = new HashSet<StandardPredicate>();
		Database inferDB = info.dataStore.getDatabase(info.targetPartition, toClose, info.observationPartition);
		MPEInference mpe = null;

		try {
			mpe = new MPEInference(info.model, inferDB, info.config);
		} catch (Exception ex) {
			System.out.println(ex);
			ex.printStackTrace();
			fail("Exception thrown during MPE constructor.");
		}

		mpe.mpeInference();
		mpe.close();
		inferDB.close();
	}

	/**
	 * Make sure we do not crash on a logical rule with no open predicates.
	 */
	@Test
	public void testLogicalNoOpenPredicates() {
		TestModelFactory.ModelInformation info = TestModelFactory.getModel();

		// Reset the model with only a single rule.
		info.model = new Model();

		// Nice(A) & Nice(B) -> Friends(A, B)
		info.model.addRule(new WeightedLogicalRule(
			new Implication(
				new Conjunction(
					new QueryAtom(info.predicates.get("Nice"), new Variable("A")),
					new QueryAtom(info.predicates.get("Nice"), new Variable("B"))
				),
				new QueryAtom(info.predicates.get("Friends"), new Variable("A"), new Variable("B"))
			),
			1.0,
			true
		));

		// Close the predicates we are using.
		Set<StandardPredicate> toClose = new HashSet<StandardPredicate>();
		toClose.add(info.predicates.get("Nice"));
		toClose.add(info.predicates.get("Friends"));

		Database inferDB = info.dataStore.getDatabase(info.targetPartition, toClose, info.observationPartition);
		MPEInference mpe = null;

		try {
			mpe = new MPEInference(info.model, inferDB, info.config);
		} catch (Exception ex) {
			System.out.println(ex);
			ex.printStackTrace();
			fail("Exception thrown during MPE constructor.");
		}

		mpe.mpeInference();
		mpe.close();
		inferDB.close();
	}

	/**
	 * Make sure we do not crash on an arithmetic rule with no open predicates.
	 */
	@Test
	public void testArithmeticNoOpenPredicates() {
		TestModelFactory.ModelInformation info = TestModelFactory.getModel();

		// Reset the model with only a single rule.
		// info.model = new Model();

		List<Coefficient> coefficients;
		List<SummationAtomOrAtom> atoms;

		coefficients = Arrays.asList(
			(Coefficient)(new ConstantNumber(1.0)),
			(Coefficient)(new ConstantNumber(1.0))
		);

		atoms = Arrays.asList(
			(SummationAtomOrAtom)(new QueryAtom(info.predicates.get("Nice"), new Variable("A"))),
			(SummationAtomOrAtom)(new QueryAtom(info.predicates.get("Nice"), new Variable("B")))
		);

		// Nice(A) + Nice(B) >= 1.0
		info.model.addRule(new WeightedArithmeticRule(
				new ArithmeticRuleExpression(coefficients, atoms, FunctionComparator.LargerThan, new ConstantNumber(1)),
				1.0,
				true
		));

		// Close the predicates we are using.
		Set<StandardPredicate> toClose = new HashSet<StandardPredicate>();
		toClose.add(info.predicates.get("Nice"));

		Database inferDB = info.dataStore.getDatabase(info.targetPartition, toClose, info.observationPartition);
		MPEInference mpe = null;

		try {
			mpe = new MPEInference(info.model, inferDB, info.config);
		} catch (Exception ex) {
			System.out.println(ex);
			ex.printStackTrace();
			fail("Exception thrown during MPE constructor.");
		}

		mpe.mpeInference();
		mpe.close();
		inferDB.close();
	}
}
