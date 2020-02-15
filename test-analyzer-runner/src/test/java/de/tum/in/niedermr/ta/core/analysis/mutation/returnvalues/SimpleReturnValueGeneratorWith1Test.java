package de.tum.in.niedermr.ta.core.analysis.mutation.returnvalues;

import static org.junit.Assert.assertEquals;

/** Test {@link SimpleReturnValueGeneratorWith1}. */
public class SimpleReturnValueGeneratorWith1Test extends AbstractReturnValueGeneratorTest<ClassWithMethodsForMutation> {

	/** Constructor. */
	public SimpleReturnValueGeneratorWith1Test() {
		super(ClassWithMethodsForMutation.class, new SimpleReturnValueGeneratorWith1());
	}

	/** {@inheritDoc} */
	@Override
	protected void verifyModification(Class<?> mutatedClass, Object instanceOfMutatedClass,
			ClassWithMethodsForMutation instanceOfOriginalClass) throws ReflectiveOperationException {
		assertEquals(1, mutatedClass.getMethod("getIntValue").invoke(instanceOfMutatedClass));
		assertEquals("A", mutatedClass.getMethod("getStringValue").invoke(instanceOfMutatedClass));
		assertEquals(true, mutatedClass.getMethod("getFalse").invoke(instanceOfMutatedClass));
		assertEquals(1L, mutatedClass.getMethod("getLong").invoke(instanceOfMutatedClass));
		assertEquals(1.0F, mutatedClass.getMethod("getFloat").invoke(instanceOfMutatedClass));
		assertEquals(1.0, mutatedClass.getMethod("getDouble").invoke(instanceOfMutatedClass));
		assertEquals('A', mutatedClass.getMethod("getChar").invoke(instanceOfMutatedClass));
	}
}
