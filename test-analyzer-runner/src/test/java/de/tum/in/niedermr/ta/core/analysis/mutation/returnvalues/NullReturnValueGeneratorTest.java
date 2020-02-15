package de.tum.in.niedermr.ta.core.analysis.mutation.returnvalues;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.objectweb.asm.Type;

import de.tum.in.niedermr.ta.core.code.identifier.MethodIdentifier;

/** Test {@link NullReturnValueGenerator}. */
public class NullReturnValueGeneratorTest extends AbstractReturnValueGeneratorTest<ClassWithMethodsForMutation> {

	/** Constructor. */
	public NullReturnValueGeneratorTest() {
		super(ClassWithMethodsForMutation.class, new NullReturnValueGenerator());
	}

	/** {@inheritDoc} */
	@Override
	protected void verifyModification(Class<?> mutatedClass, Object instanceOfMutatedClass,
			ClassWithMethodsForMutation instanceOfOriginalClass) throws ReflectiveOperationException {
		assertNull(mutatedClass.getMethod("getStringValue").invoke(instanceOfMutatedClass));
		assertNull(mutatedClass.getMethod("getIntegerWrapper").invoke(instanceOfMutatedClass));
		assertNull(mutatedClass.getMethod("getIntArray").invoke(instanceOfMutatedClass));
		assertNull(mutatedClass.getMethod("getFile").invoke(instanceOfMutatedClass));

		assertEquals("no mutation expected", instanceOfOriginalClass.getIntValue(),
				mutatedClass.getMethod("getIntValue").invoke(instanceOfMutatedClass));
		assertEquals("no mutation expected", instanceOfOriginalClass.getTrue(),
				mutatedClass.getMethod("getTrue").invoke(instanceOfMutatedClass));
	}

	/** Test. */
	@Test
	public void testCanHandleReturn() {
		IReturnValueGenerator retValGen = new NullReturnValueGenerator();

		assertFalse(retValGen.checkReturnValueSupported(MethodIdentifier.EMPTY, Type.VOID_TYPE));
		assertFalse(retValGen.checkReturnValueSupported(MethodIdentifier.EMPTY, Type.BOOLEAN_TYPE));
		assertTrue(retValGen.checkReturnValueSupported(MethodIdentifier.EMPTY,
				Type.getReturnType("()Ljava/lang/String;")));
		assertTrue(retValGen.checkReturnValueSupported(MethodIdentifier.EMPTY, Type.getReturnType("()[I")));
	}
}
