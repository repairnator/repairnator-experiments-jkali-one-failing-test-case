package de.tum.in.niedermr.ta.runner.analysis.result.presentation.sql;

import java.util.ArrayList;
import java.util.List;

public class SqlMultiInsertStatementBuilder {

	private static final String VALUE_PART_SEPARATOR = ",\r\n";

	private final String m_statementShallow;
	private final List<String> m_valueStatementParts;

	/**
	 * Constructor.
	 * 
	 * @param statementShallow
	 *            must contain a <code>%s</code> placeholder.
	 */
	public SqlMultiInsertStatementBuilder(String statementShallow) {
		m_statementShallow = statementShallow;
		m_valueStatementParts = new ArrayList<>();

		if (!m_statementShallow.contains("%s")) {
			throw new IllegalArgumentException("statementShallow must contain a %s placeholder");
		}
	}

	/**
	 * @param valuesStatementPart
	 *            with brackets
	 */
	public void addValuesStatementPart(String valuesStatementPart) {
		m_valueStatementParts.add(valuesStatementPart);
	}

	public String toSql() {
		if (m_valueStatementParts.isEmpty()) {
			return "";
		}

		StringBuilder combinedValuesBuilder = new StringBuilder();

		for (String valueStatementPart : m_valueStatementParts) {
			combinedValuesBuilder.append(valueStatementPart);
			combinedValuesBuilder.append(VALUE_PART_SEPARATOR);
		}

		combinedValuesBuilder.setLength(combinedValuesBuilder.length() - VALUE_PART_SEPARATOR.length());

		return String.format(m_statementShallow, combinedValuesBuilder);
	}

	/** Wrap a string value in quotation marks. */
	public static String asSqlString(String value) {
		return "'" + value + "'";
	}
}
