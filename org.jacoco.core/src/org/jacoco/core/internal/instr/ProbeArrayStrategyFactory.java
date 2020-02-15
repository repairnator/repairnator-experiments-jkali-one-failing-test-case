/*******************************************************************************
 * Copyright (c) 2009, 2018 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 *******************************************************************************/
package org.jacoco.core.internal.instr;

import org.jacoco.core.internal.BytecodeVersion;
import org.jacoco.core.internal.flow.ClassProbesAdapter;
import org.jacoco.core.runtime.IExecutionDataAccessorGenerator;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;

/**
 * Factory to find a suitable strategy to access the probe array for a given
 * class.
 */
public final class ProbeArrayStrategyFactory {

	private ProbeArrayStrategyFactory() {
	}

	/**
	 * Creates a suitable strategy instance for the class described by the given
	 * reader. Created instance must be used only to process a class or
	 * interface for which it has been created and must be used only once.
	 *
	 * @param classId
	 *            class identifier
	 * @param reader
	 *            reader to get information about the class
	 * @param accessorGenerator
	 *            accessor to the coverage runtime
	 * @return strategy instance
	 */
	public static IProbeArrayStrategy createFor(final long classId,
			final ClassReader reader,
			final IExecutionDataAccessorGenerator accessorGenerator) {

		final String className = reader.getClassName();
		final int version = BytecodeVersion.get(reader.b);

		if (isInterfaceOrModule(reader)) {
			final ProbeCounter counter = getProbeCounter(reader);
			if (counter.getCount() == 0) {
				return new NoneProbeArrayStrategy();
			}
			if (version >= Opcodes.V1_8 && counter.hasMethods()) {
				return new InterfaceFieldProbeArrayStrategy(className, classId,
						counter.getCount(), accessorGenerator);
			} else {
				return new LocalProbeArrayStrategy(className, classId,
						counter.getCount(), accessorGenerator);
			}
		} else {
			return new ClassFieldProbeArrayStrategy(className, classId,
					InstrSupport.needsFrames(version), accessorGenerator);
		}
	}

	private static boolean isInterfaceOrModule(final ClassReader reader) {
		return (reader.getAccess()
				& (Opcodes.ACC_INTERFACE | Opcodes.ACC_MODULE)) != 0;
	}

	private static ProbeCounter getProbeCounter(final ClassReader reader) {
		final ProbeCounter counter = new ProbeCounter();
		reader.accept(new ClassProbesAdapter(counter, false), 0);
		return counter;
	}

}
