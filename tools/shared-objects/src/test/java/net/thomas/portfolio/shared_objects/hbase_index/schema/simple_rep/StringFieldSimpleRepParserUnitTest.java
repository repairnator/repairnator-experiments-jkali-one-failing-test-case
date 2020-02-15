package net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep;

import static net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep.StringFieldSimpleRepParser.newStringFieldParser;
import static net.thomas.portfolio.shared_objects.test_utils.DataTypeFieldMatcher.matchesField;
import static net.thomas.portfolio.testing_tools.ToStringTestUtil.assertToStringContainsAllFieldsFromObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.hbase_index.schema.IdCalculator;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;

public class StringFieldSimpleRepParserUnitTest {

	private StringFieldSimpleRepParser parser;
	private IdCalculator idCalculatorMock;

	@Before
	public void setupForTest() {
		idCalculatorMock = mock(IdCalculator.class);
		when(idCalculatorMock.calculate(eq(TYPE), argThat(matchesField(FIELD, SIMPLE_REP)))).thenReturn(ID);
		parser = newStringFieldParser(TYPE, FIELD, idCalculatorMock);
	}

	@Test
	public void shouldParseSimpleRepAndBuildSelector() {
		final Selector selector = parser.parse(TYPE, SIMPLE_REP);
		assertEquals(ID, selector.getId());
	}

	@Test
	public void shouldAlwaysReturnSameHashCode() {
		assertEquals(parser.hashCode(), newStringFieldParser(TYPE, FIELD, idCalculatorMock).hashCode());
	}

	@Test
	public void shouldBeEqualIfSameTypeAndNotNull() {
		assertEquals(parser, newStringFieldParser(TYPE, FIELD, idCalculatorMock));
	}

	@Test
	public void shouldNotBeEqualIfDifferentType() {
		assertNotEquals(parser, "");
	}

	@Test
	public void shouldNotBeEqualIfNull() {
		assertNotEquals(parser, (DomainSimpleRepParser) null);
	}

	@Test
	public void shouldHaveValidToStringFunction() {
		assertToStringContainsAllFieldsFromObject(parser);
	}

	private static final String TYPE = "TYPE";
	private static final String FIELD = "FIELD";
	private static final String UID = "AA";
	private static final String SIMPLE_REP = "ABCD";
	private static final DataTypeId ID = new DataTypeId(TYPE, UID);
}