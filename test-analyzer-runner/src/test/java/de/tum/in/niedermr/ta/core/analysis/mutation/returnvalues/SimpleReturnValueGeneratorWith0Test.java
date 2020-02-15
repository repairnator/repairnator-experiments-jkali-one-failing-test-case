package de.tum.in.niedermr.ta.core.analysis.mutation.returnvalues;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/** Test {@link SimpleReturnValueGeneratorWith0}. */
public class SimpleReturnValueGeneratorWith0Test extends AbstractReturnValueGeneratorTest<ClassWithMethodsForMutation> {

	/** Constructor. */
	public SimpleReturnValueGeneratorWith0Test() {
		super(ClassWithMethodsForMutation.class, new SimpleReturnValueGeneratorWith0());
	}

	/** {@inheritDoc} */
	@Override
	protected void verifyModification(Class<?> mutatedClass, Object instanceOfMutatedClass,
			ClassWithMethodsForMutation instanceOfOriginalClass) throws ReflectiveOperationException {
		verifyMutationForSimpleValueMethods(mutatedClass, instanceOfMutatedClass);

		assertEquals("no mutation expected", -100,
				mutatedClass.getMethod("getIntegerWrapper").invoke(instanceOfMutatedClass));
		assertTrue("no mutation expected", Arrays.equals(new int[] { 4 },
				(int[]) mutatedClass.getMethod("getIntArray").invoke(instanceOfMutatedClass)));
		assertEquals("no mutation expected", new File("./a.txt"),
				mutatedClass.getMethod("getFile").invoke(instanceOfMutatedClass));
	}

	/** Verify the mutation for methods that return a simple value. */
	protected static void verifyMutationForSimpleValueMethods(Class<?> mutatedClass, Object instanceOfMutatedClass)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		assertEquals(0, mutatedClass.getMethod("getIntValue").invoke(instanceOfMutatedClass));
		assertEquals("", mutatedClass.getMethod("getStringValue").invoke(instanceOfMutatedClass));
		assertEquals(false, mutatedClass.getMethod("getTrue").invoke(instanceOfMutatedClass));
		assertEquals(0L, mutatedClass.getMethod("getLong").invoke(instanceOfMutatedClass));
		assertEquals(0.0F, mutatedClass.getMethod("getFloat").invoke(instanceOfMutatedClass));
		assertEquals(0.0, mutatedClass.getMethod("getDouble").invoke(instanceOfMutatedClass));
		assertEquals(' ', mutatedClass.getMethod("getChar").invoke(instanceOfMutatedClass));
	}
}
