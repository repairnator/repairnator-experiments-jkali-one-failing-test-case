package de.tum.in.niedermr.ta.core.code.identifier;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import de.tum.in.niedermr.ta.core.code.constants.JavaConstants;
import de.tum.in.niedermr.ta.core.code.util.BytecodeUtility;
import de.tum.in.niedermr.ta.sample.SampleClass;

/** Test {@link MethodIdentifier}. */
public class MethodIdentifierTest {

	private static final String CLASS_NAME_BIG_INTEGER = "java.lang.BigInteger";
	private static final String DESCRIPTOR_BIG_INTEGER = "Ljava/lang/BigInteger;";

	private static final String SAMPLE_CLASS_NAME = "org.test.Class";
	private static final String SAMPLE_METHOD_NAME = "execute";
	private static final String SAMPLE_ARGUMENTS_1 = "(boolean,int,int)";
	private static final String SAMPLE_ARGUMENTS_2 = "(" + SAMPLE_CLASS_NAME + ")";
	private static final String SAMPLE_ARGUMENTS_3 = "(" + Integer.class.getName() + ")";
	private static final String SAMPLE_DESCRIPTOR_1 = "(ZII)" + DESCRIPTOR_BIG_INTEGER;
	private static final String SAMPLE_DESCRIPTOR_2 = "(L" + Integer.class.getName() + ";)V";

	private static final String EXPECTED_IDENTIFIER_STRING = "org.test.Class.execute(boolean,int,int)";
	private static final String EXPECTED_RETURN_TYPE_STRING = CLASS_NAME_BIG_INTEGER;
	private static final String EXPECTED_IDENTIFIER_WITH_RETURN_TYPE = EXPECTED_IDENTIFIER_STRING
			+ JavaConstants.RETURN_TYPE_SEPARATOR + EXPECTED_RETURN_TYPE_STRING;

	/** Test. */
	@Test
	public void testCreate1() {
		MethodIdentifier identifier = MethodIdentifier.create(SAMPLE_CLASS_NAME, SAMPLE_METHOD_NAME,
				SAMPLE_DESCRIPTOR_1);

		assertEquals(EXPECTED_IDENTIFIER_STRING, identifier.get());
		assertEquals(EXPECTED_IDENTIFIER_WITH_RETURN_TYPE, identifier.getWithReturnType());
		assertEquals(EXPECTED_RETURN_TYPE_STRING, identifier.getOnlyReturnType());
	}

	/** Test. */
	@Test
	public void testCreate2() {
		MethodIdentifier identifier1 = MethodIdentifier.create(SAMPLE_CLASS_NAME, SAMPLE_METHOD_NAME,
				SAMPLE_DESCRIPTOR_1);
		MethodIdentifier identifier2 = MethodIdentifier.create(
				SAMPLE_CLASS_NAME.replace(JavaConstants.PACKAGE_SEPARATOR, JavaConstants.PATH_SEPARATOR),
				SAMPLE_METHOD_NAME, SAMPLE_DESCRIPTOR_1);

		assertEquals(identifier1, identifier2);
	}

	/** Test. */
	@Test
	public void testCreate3() {
		ClassNode classNode = new ClassNode();
		classNode.name = SAMPLE_CLASS_NAME;

		MethodNode methodNode = new MethodNode();
		methodNode.name = SAMPLE_METHOD_NAME;
		methodNode.desc = SAMPLE_DESCRIPTOR_1;

		MethodIdentifier identifier1 = MethodIdentifier.create(classNode, methodNode);
		MethodIdentifier identifier2 = MethodIdentifier.create(SAMPLE_CLASS_NAME, methodNode);

		assertEquals(EXPECTED_IDENTIFIER_STRING, identifier1.get());
		assertEquals(EXPECTED_IDENTIFIER_STRING, identifier2.get());
		assertEquals(identifier1, identifier2);
	}

	@Test
	public void testCreateForConstructorMethod() throws IOException {
		ClassNode classNode = BytecodeUtility.getAcceptedClassNode(SampleClass.class);
		MethodNode methodNode = (MethodNode) classNode.methods.get(0);

		MethodIdentifier identifier = MethodIdentifier.create(classNode, methodNode);
		assertEquals("de.tum.in.niedermr.ta.sample.SampleClass.<init>()", identifier.get());
	}

	/** Test. */
	@Test(expected = Exception.class)
	public void testCreateWithWrongDescriptor() {
		MethodIdentifier.create(SAMPLE_CLASS_NAME, SAMPLE_METHOD_NAME, "(text.String)V");
	}

	/** Test. */
	@Test
	public void testParse1() {
		MethodIdentifier identifier = MethodIdentifier.create(SAMPLE_CLASS_NAME, SAMPLE_METHOD_NAME,
				SAMPLE_DESCRIPTOR_1);

		assertEquals(identifier, MethodIdentifier.parse(identifier.get()));
		assertEquals(identifier, MethodIdentifier.parse(identifier.getWithReturnType()));
		assertEquals(identifier.getWithReturnType(),
				MethodIdentifier.parse(identifier.getWithReturnType()).getWithReturnType());
	}

	/** Test. */
	@Test
	public void testParse2() {
		MethodIdentifier expected = MethodIdentifier.create(SAMPLE_CLASS_NAME, SAMPLE_METHOD_NAME, SAMPLE_DESCRIPTOR_1);
		MethodIdentifier identifier2 = MethodIdentifier.parse(
				SAMPLE_CLASS_NAME + JavaConstants.CLASS_METHOD_SEPARATOR + SAMPLE_METHOD_NAME + SAMPLE_ARGUMENTS_1);

		assertEquals(expected.get(), identifier2.get());
		assertEquals(expected.get() + JavaConstants.RETURN_TYPE_SEPARATOR + "?", identifier2.getWithReturnType());
	}

	/** Test. */
	@Test
	public void testEquals() {
		MethodIdentifier identifier1 = MethodIdentifier.parse(
				SAMPLE_CLASS_NAME + JavaConstants.CLASS_METHOD_SEPARATOR + SAMPLE_METHOD_NAME + SAMPLE_ARGUMENTS_1);
		MethodIdentifier identifier2 = MethodIdentifier
				.parse(SAMPLE_CLASS_NAME + JavaConstants.CLASS_METHOD_SEPARATOR + SAMPLE_METHOD_NAME
						+ SAMPLE_ARGUMENTS_1 + JavaConstants.RETURN_TYPE_SEPARATOR + CLASS_NAME_BIG_INTEGER);

		assertEquals(identifier1, identifier2);
	}

	/** Test. */
	@Test
	public void testGetOnlyClassName1() {
		MethodIdentifier identifier;

		identifier = MethodIdentifier.parse(
				SAMPLE_CLASS_NAME + JavaConstants.CLASS_METHOD_SEPARATOR + SAMPLE_METHOD_NAME + SAMPLE_ARGUMENTS_1);
		assertEquals(SAMPLE_CLASS_NAME, identifier.getOnlyClassName());

		identifier = MethodIdentifier.parse(Map.Entry.class.getName() + JavaConstants.CLASS_METHOD_SEPARATOR
				+ SAMPLE_METHOD_NAME + SAMPLE_ARGUMENTS_1);
		assertEquals(Map.Entry.class.getName(), identifier.getOnlyClassName());

		identifier = MethodIdentifier.parse(
				SAMPLE_CLASS_NAME + JavaConstants.CLASS_METHOD_SEPARATOR + SAMPLE_METHOD_NAME + SAMPLE_ARGUMENTS_2);
		assertEquals(SAMPLE_CLASS_NAME, identifier.getOnlyClassName());
	}

	/** Test. */
	@Test(expected = IllegalStateException.class)
	public void testGetOnlyClassName2() {
		MethodIdentifier.EMPTY.getOnlyClassName();
	}

	/** Test. */
	@Test
	public void testGetOnlyMethodName() {
		MethodIdentifier identifier;

		identifier = MethodIdentifier.parse(
				SAMPLE_CLASS_NAME + JavaConstants.CLASS_METHOD_SEPARATOR + SAMPLE_METHOD_NAME + SAMPLE_ARGUMENTS_1);
		assertEquals(SAMPLE_METHOD_NAME, identifier.getOnlyMethodName());

		identifier = MethodIdentifier.parse(
				SAMPLE_CLASS_NAME + JavaConstants.CLASS_METHOD_SEPARATOR + SAMPLE_METHOD_NAME + SAMPLE_ARGUMENTS_2);
		assertEquals(SAMPLE_METHOD_NAME, identifier.getOnlyMethodName());
	}

	/** Test. */
	@Test
	public void testCreateLambdaMethodName() {
		MethodIdentifier identifier = MethodIdentifier.create(SAMPLE_CLASS_NAME, "lambda$0", SAMPLE_DESCRIPTOR_2);
		assertEquals(SAMPLE_CLASS_NAME + JavaConstants.CLASS_METHOD_SEPARATOR + "lambda$0" + SAMPLE_ARGUMENTS_3,
				identifier.get());
	}
}
