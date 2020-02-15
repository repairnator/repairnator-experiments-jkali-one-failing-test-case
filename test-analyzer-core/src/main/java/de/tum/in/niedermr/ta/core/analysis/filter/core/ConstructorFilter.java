package de.tum.in.niedermr.ta.core.analysis.filter.core;

import org.objectweb.asm.tree.MethodNode;

import de.tum.in.niedermr.ta.core.analysis.filter.FilterResult;
import de.tum.in.niedermr.ta.core.analysis.filter.IMethodFilter;
import de.tum.in.niedermr.ta.core.code.identifier.MethodIdentifier;
import de.tum.in.niedermr.ta.core.code.util.BytecodeUtility;

public class ConstructorFilter implements IMethodFilter {

	/** {@inheritDoc} */
	@Override
	public FilterResult apply(MethodIdentifier methodIdentifier, MethodNode method) {
		if (BytecodeUtility.isConstructor(methodIdentifier.getOnlyMethodName())) {
			return FilterResult.reject(ConstructorFilter.class);
		}

		return FilterResult.accepted();
	}
}
