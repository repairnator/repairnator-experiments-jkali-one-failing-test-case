package de.tum.in.niedermr.ta.extensions.analysis.workflows.statistics.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.Opcodes;

import de.tum.in.niedermr.ta.core.code.identifier.MethodIdentifier;
import de.tum.in.niedermr.ta.extensions.analysis.workflows.statistics.tests.AssertionInformation.AssertionResult;

public class AssertionInformationTest {
	@Test
	public void testIsAssertionMethod() throws ClassNotFoundException {
		AssertionInformation assertionInformation = new AssertionInformation();

		MethodIdentifier identifier;

		identifier = MethodIdentifier.create(Assert.class, "assertEquals", "(Ljava/lang/String;JJ)V");
		assertTrue(assertionInformation.isAssertionMethod(identifier).isAssertion());

		identifier = MethodIdentifier.create(Assert.class, "fail", "()V");
		assertTrue(assertionInformation.isAssertionMethod(identifier).isAssertion());

		identifier = MethodIdentifier.create(Assert.class, "assertNotNull", "(Ljava/lang/Object;)V");
		assertTrue(assertionInformation.isAssertionMethod(identifier).isAssertion());

		identifier = MethodIdentifier.create(InheritedAssertionClass.class, "assertTrue", "(Z)V");
		assertTrue(assertionInformation.isAssertionMethod(identifier).isAssertion());

		identifier = MethodIdentifier.create(String.class, "substring", "(I)V");
		assertFalse(assertionInformation.isAssertionMethod(identifier).isAssertion());
	}

	@Test
	public void testIsAssertionMethodWithOwnClass() throws ClassNotFoundException {
		AssertionInformation assertionInformation = new AssertionInformation(OwnAssertionClass.class);

		MethodIdentifier identifier;

		identifier = MethodIdentifier.create(OwnAssertionClass.class, "assertIsTen", "(I)V");
		assertTrue(assertionInformation.isAssertionMethod(identifier).isAssertion());

		identifier = MethodIdentifier.create(OwnAssertionClass.class, "assertIsEleven", "(I)V");
		assertTrue(assertionInformation.isAssertionMethod(identifier).isAssertion());
	}

	@Test
	public void testPopOpcodes() throws ClassNotFoundException {
		AssertionInformation assertionInformation = new AssertionInformation();

		MethodIdentifier identifier = MethodIdentifier.create(Assert.class, "assertEquals", "(JJ)V");
		AssertionResult result = assertionInformation.isAssertionMethod(identifier);

		assertTrue(result.isAssertion());
		assertEquals(2, result.getPopInstructionOpcodes().length);
		assertEquals(Opcodes.POP2, result.getPopInstructionOpcodes()[0]);
	}

	static class InheritedAssertionClass extends Assert {
		// NOP
	}

	static class OwnAssertionClass {
		public static void assertIsTen(int value) {
			if (value != 10) {
				throw new NotTenAssertionError();
			}
		}

		public void assertIsEleven(int value) {
			if (value != 11) {
				throw new AssertionError();
			}
		}

		static class NotTenAssertionError extends AssertionError {
			private static final long serialVersionUID = 1L;
		}
	}
}
