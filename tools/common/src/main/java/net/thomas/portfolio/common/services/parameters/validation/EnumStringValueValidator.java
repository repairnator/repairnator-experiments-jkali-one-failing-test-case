package net.thomas.portfolio.common.services.parameters.validation;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

public class EnumStringValueValidator<ENUM_TYPE extends Enum<ENUM_TYPE>> extends ParameterValidator<String> {

	private final Class<ENUM_TYPE> enumType;
	private final String values;
	private final boolean required;

	public EnumStringValueValidator(String parameterName, Class<ENUM_TYPE> enumType, ENUM_TYPE[] values,
			boolean required) {
		super(parameterName);
		this.enumType = enumType;
		this.required = required;
		this.values = "[" + stream(values).map(Enum::name).collect(joining(", ")) + " ]";
	}

	@Override
	public boolean isValid(String value) {
		return canBeParsed(value);
	}
	
	private boolean canBeParsed(String value) {
		if(value == null)
			return !required;
		try {
			Enum.valueOf(enumType, value);
			return true;
		} catch (final IllegalArgumentException e) {
			return false;
		}
	}

	@Override
	public String getReason(String value) {
		if (value == null) {
			return parameterName + " is missing" + (required ? " and is required" : ", but not required")
					+ ". It should belong to " + values;
		} else if (!canBeParsed(value)) {
			return parameterName + " ( was " + value + " ) should belong to " + values;
		} else {
			return parameterName + " ( was " + value + " ) is valid";
		}
	}
}