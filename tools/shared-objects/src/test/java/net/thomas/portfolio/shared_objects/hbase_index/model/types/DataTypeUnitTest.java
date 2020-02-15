package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static net.thomas.portfolio.testing_tools.SerializationDeserializationUtil.assertCanSerializeAndDeserialize;
import static net.thomas.portfolio.testing_tools.SerializationDeserializationUtil.assertCanSerializeAndDeserializeWithNullValues;
import static net.thomas.portfolio.testing_tools.ToStringTestUtil.assertToStringContainsAllFieldsFromObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.fasterxml.jackson.core.JsonProcessingException;

@RunWith(Parameterized.class)
public class DataTypeUnitTest {
	private final DataType entityUnderTest;

	@Parameters
	public static List<DataType> entities() {
		return asList(SIMPLE_ENTITY, RECURSIVE_ENTITY, COMPLEX_ENTITY);
	}

	public DataTypeUnitTest(DataType entityUnderTest) {
		this.entityUnderTest = entityUnderTest;
	}

	/***
	 * Non-parametized, but kept here for simplicity
	 */
	@Test
	public void shouldContainIdAfterUsingExplicitConstructor() {
		final DataType entity = new RawDataType(SIMPLE_ENTITY_ID, emptyMap());
		assertEquals(SIMPLE_ENTITY_ID, entity.getId());
	}

	@Test
	public void shouldContainFieldsAfterUsingExplicitConstructor() {
		final DataType entity = new RawDataType(SIMPLE_ENTITY_ID, singletonMap(SOME_FIELD, SOME_VALUE));
		assertTrue(entity.containsKey(SOME_FIELD));
	}

	@Test
	public void shouldContainValueAfterInsertion() {
		final DataType entity = new RawDataType();
		entity.put(SOME_FIELD, SOME_VALUE);
		assertTrue(entity.containsKey(SOME_FIELD));
	}

	@Test
	public void shouldMatchInsertedValue() {
		final DataType entity = new RawDataType();
		entity.put(SOME_FIELD, SOME_VALUE);
		assertEquals(SOME_VALUE, entity.get(SOME_FIELD));
	}

	@Test
	public void shouldContainValueInRawForm() throws JsonProcessingException {
		final DataType entity = new RawDataType();
		entity.put(SOME_FIELD, SOME_VALUE);
		final String rawForm = entity.getInRawForm();
		assertTrue(rawForm.contains(SOME_VALUE));
	}

	/***
	 * Parametized
	 */
	@Test
	public void shouldHaveSymmetricProtocol() {
		assertCanSerializeAndDeserialize(entityUnderTest);
	}

	@Test
	public void shouldSurviveNullParameters() {
		assertCanSerializeAndDeserializeWithNullValues(entityUnderTest);
	}

	@Test
	public void shouldHaveValidToStringFunction() {
		assertToStringContainsAllFieldsFromObject(entityUnderTest);
	}

	@Test
	public void shouldBeEqual() {
		assertTrue(SIMPLE_ENTITY.equals(SIMPLE_ENTITY));
	}

	@Test
	public void shouldNotBeEqualWithDifferentValues() {
		assertFalse(SIMPLE_ENTITY.equals(COMPLEX_ENTITY));
	}

	@Test
	public void shouldNotBeEqualWithDifferentObject() {
		assertFalse(SIMPLE_ENTITY.equals(ANOTHER_OBJECT));
	}

	@Test
	public void shouldHaveSameHashCode() {
		assertEquals(SIMPLE_ENTITY.hashCode(), SIMPLE_ENTITY.hashCode());
	}

	@Test
	public void shouldNotHaveSameHashCode() {
		assertNotEquals(SIMPLE_ENTITY.hashCode(), COMPLEX_ENTITY.hashCode());
	}

	private static final String SOME_FIELD = "field";
	private static final String SOME_VALUE = "value";
	private static final DataTypeId SIMPLE_ENTITY_ID = new DataTypeId("SimpleType", "ABCD01");
	private static final DataType SIMPLE_ENTITY;
	private static final DataTypeId RECURSIVE_ENTITY_SUB_ID = new DataTypeId("RecursiveType", "ABCD02");
	private static final DataTypeId RECURSIVE_ENTITY_ID = new DataTypeId("RecursiveType", "ABCD03");
	private static final DataType RECURSIVE_ENTITY;
	private static final DataTypeId COMPLEX_ENTITY_ID = new DataTypeId("ComplexType", "ABCD04");
	private static final DataType COMPLEX_ENTITY;
	private static final Object ANOTHER_OBJECT = "object";

	static {
		SIMPLE_ENTITY = buildSimpleDataType();
		RECURSIVE_ENTITY = buildRecursiveDataType();
		COMPLEX_ENTITY = buildComplexDataType(SIMPLE_ENTITY, RECURSIVE_ENTITY);
	}

	private static DataType buildSimpleDataType() {
		final DataType simpleDataType = new RawDataType();
		simpleDataType.setId(SIMPLE_ENTITY_ID);
		simpleDataType.put("value", "aa bb");
		return simpleDataType;
	}

	private static DataType buildRecursiveDataType() {
		final DataType recursiveSubDataType = new RawDataType();
		recursiveSubDataType.setId(RECURSIVE_ENTITY_SUB_ID);
		recursiveSubDataType.put("value", "aa");
		final DataType recursiveDataType = new RawDataType();
		recursiveDataType.setId(RECURSIVE_ENTITY_ID);
		recursiveDataType.put("value", "aaa");
		recursiveDataType.put("reference", recursiveSubDataType);
		return recursiveDataType;
	}

	private static DataType buildComplexDataType(final DataType simpleDataType, final DataType recursiveDataType) {
		final DataType complexDataType = new RawDataType();
		complexDataType.setId(COMPLEX_ENTITY_ID);
		complexDataType.put("arrayReference", singletonList(simpleDataType));
		complexDataType.put("recursiveReference", recursiveDataType);
		return complexDataType;
	}
}