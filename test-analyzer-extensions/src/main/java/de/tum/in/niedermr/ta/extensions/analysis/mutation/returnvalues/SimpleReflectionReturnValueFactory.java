package de.tum.in.niedermr.ta.extensions.analysis.mutation.returnvalues;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import de.tum.in.niedermr.ta.core.analysis.mutation.returnvalues.base.AbstractReturnValueFactory;
import de.tum.in.niedermr.ta.core.analysis.mutation.returnvalues.base.UnwantedReturnTypeException;
import de.tum.in.niedermr.ta.core.code.constants.JavaConstants;
import de.tum.in.niedermr.ta.core.code.identifier.MethodIdentifier;
import de.tum.in.niedermr.ta.core.code.util.JavaUtility;

/**
 * Uses reflection to create instances of
 * <li>classes with public parameterless constructors</li>
 * <li>classes with a public constructor with parameters of primitive types or String</li>
 * <li>enums</li>
 * <li>arrays (one dimensional only)</li>
 */
public class SimpleReflectionReturnValueFactory extends AbstractReturnValueFactory {

	/** Instance. */
	public static final SimpleReflectionReturnValueFactory INSTANCE = new SimpleReflectionReturnValueFactory();

	/**
	 * {@link #createInstanceWithSimpleParameters(Constructor)} supports primitive types, arrays and this set of classes
	 * as parameter types.
	 */
	private static final Set<Class<?>> SUPPORTED_CONSTRUCTOR_PARAMETER_TYPES = new HashSet<>(
			Arrays.asList(Object.class, String.class, Optional.class, Collection.class, List.class, ArrayList.class));

	/** {@inheritDoc} */
	@Override
	public Object createWithException(MethodIdentifier methodIdentifier, String returnType)
			throws NoSuchElementException, UnwantedReturnTypeException {
		try {
			if (isBlacklistedType(returnType)) {
				throw new UnwantedReturnTypeException("Blacklisted: " + returnType);
			}

			Class<?> cls = resolveClass(returnType);

			if (returnType.endsWith(JavaConstants.ARRAY_BRACKETS)) {
				return createArray(returnType, cls);
			}

			if (cls.isEnum()) {
				return getEnumInstance(cls);
			}

			if (cls.isInterface()) {
				throw new NoSuchElementException("Interfaces are not supported: " + cls);
			}

			return createInstance(cls);
		} catch (ReflectiveOperationException e) {
			throw new NoSuchElementException();
		}
	}

	protected Class<?> resolveClass(String returnType) throws ClassNotFoundException {
		String cleanedClassName = returnType.replace(JavaConstants.ARRAY_BRACKETS, "");
		return JavaUtility.loadClass(cleanedClassName);
	}

	protected static Object createInstance(Class<?> cls) throws ReflectiveOperationException {
		Constructor<?>[] publicConstructors = cls.getConstructors();

		for (Constructor<?> constructor : publicConstructors) {
			try {
				if (constructor.getParameterTypes().length == 0) {
					return constructor.newInstance();
				} else if (areAllPrimitiveTypeOrStringParameters(constructor)) {
					return createInstanceWithSimpleParameters(constructor);
				}
			} catch (Throwable t) {
				continue;
			}
		}

		try {
			return tryFindInstanceCreationMethod(cls);
		} catch (NoSuchElementException e) {
			throw new NoSuchElementException("No supported constructor or public static method exists: " + cls);
		}
	}

	protected static Object tryFindInstanceCreationMethod(Class<?> cls) throws ReflectiveOperationException {
		for (Method method : cls.getDeclaredMethods()) {
			if (Modifier.isStatic(method.getModifiers()) && Modifier.isPublic(method.getModifiers())
					&& method.getParameterCount() == 0 && method.getReturnType() == cls) {
				return method.invoke(null);
			}
		}

		throw new NoSuchElementException();
	}

	/** Create an instance using reflection. */
	protected static Object createInstanceWithSimpleParameters(Constructor<?> constructor)
			throws ReflectiveOperationException {
		Class<?>[] parameterTypes = constructor.getParameterTypes();
		Object[] parameterValues = new Object[parameterTypes.length];

		for (int i = 0; i < parameterTypes.length; i++) {
			Class<?> parameterType = parameterTypes[i];
			Object parameterValue = createSimpleParameterValue(parameterType);

			parameterValues[i] = parameterValue;
		}

		return constructor.newInstance(parameterValues);
	}

	/**
	 * Create a simple parameter value.
	 * 
	 * @see #SUPPORTED_CONSTRUCTOR_PARAMETER_TYPES
	 */
	protected static Object createSimpleParameterValue(Class<?> parameterType) {
		if (parameterType.isArray()) {
			return Array.newInstance(parameterType.getComponentType(), 0);
		} else if (parameterType.isEnum()) {
			return getEnumInstance(parameterType);
		} else if (parameterType == Collection.class || parameterType == List.class
				|| parameterType == ArrayList.class) {
			return new ArrayList<>();
		} else if (parameterType == String.class) {
			return "";
		} else if (parameterType == Object.class) {
			return new Object();
		} else if (parameterType == Optional.class) {
			return Optional.empty();
		} else if (parameterType.isPrimitive()) {
			return createPrimitiveParameterValue(parameterType);
		}

		throw new IllegalStateException("Not a supported parameter type: " + parameterType);
	}

	/** Create a primitive parameter value. */
	protected static Object createPrimitiveParameterValue(Class<?> parameterType) {
		if (parameterType == boolean.class) {
			return true;
		} else if (parameterType == char.class) {
			return ' ';
		} else if (parameterType == byte.class) {
			return (byte) 1;
		} else if (parameterType == short.class) {
			return (short) 1;
		} else if (parameterType == int.class) {
			return 1;
		} else if (parameterType == long.class) {
			return 1L;
		} else if (parameterType == float.class) {
			return (float) 1.0;
		} else if (parameterType == double.class) {
			return 1.0;
		}

		throw new IllegalStateException("Unexpected type: " + parameterType);
	}

	private static boolean areAllPrimitiveTypeOrStringParameters(Constructor<?> constructor) {
		for (Class<?> parameterType : constructor.getParameterTypes()) {
			if (!parameterType.isPrimitive() && !parameterType.isArray() && !parameterType.isEnum()
					&& !SUPPORTED_CONSTRUCTOR_PARAMETER_TYPES.contains(parameterType)) {
				return false;
			}
		}

		return true;
	}

	protected static Object getEnumInstance(Class<?> enumCls) {
		Object[] enumConstants = enumCls.getEnumConstants();

		if (enumConstants.length > 0) {
			return enumConstants[0];
		}

		throw new NoSuchElementException("Enum does not contain any values: " + enumCls);
	}

	protected static Object createArray(String returnType, Class<?> arrayCls) {
		int countArrayDimensions = (returnType.length() - returnType.replace(JavaConstants.ARRAY_BRACKETS, "").length())
				/ 2;

		// keep all values 0
		int[] dimensionLengths = new int[countArrayDimensions];
		return Array.newInstance(arrayCls, dimensionLengths);
	}

	/**
	 * Is blacklisted type.
	 * 
	 * @param className
	 *            may contain array brackets
	 */
	protected boolean isBlacklistedType(String className) {
		if (String.class.getName().equals(className) || JavaConstants.WRAPPER_TYPE_CLASS_NAMES.contains(className)) {
			// already handled by the simple return value generator classes
			return true;
		}

		return false;
	}
}
