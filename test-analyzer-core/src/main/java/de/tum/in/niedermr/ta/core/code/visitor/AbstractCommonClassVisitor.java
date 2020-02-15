package de.tum.in.niedermr.ta.core.code.visitor;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import de.tum.in.niedermr.ta.core.code.util.BytecodeUtility;

public abstract class AbstractCommonClassVisitor extends ClassVisitor {
	private final String m_className;

	public AbstractCommonClassVisitor(ClassVisitor cv, String className) {
		super(Opcodes.ASM5, cv);

		this.m_className = className;
	}

	public final String getClassName() {
		return m_className;
	}

	@Override
	public final MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

		if (BytecodeUtility.isConstructor(name)) {
			return mv;
		} else {
			return visitNonConstructorMethod(mv, access, name, desc);
		}
	}

	protected abstract MethodVisitor visitNonConstructorMethod(MethodVisitor mv, int access, String methodName, String desc);
}
