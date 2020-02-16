package org.pitest.bytecode.analysis;

import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.DRETURN;
import static org.objectweb.asm.Opcodes.FRETURN;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.LRETURN;
import static org.objectweb.asm.Opcodes.RETURN;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.pitest.functional.prelude.Prelude;
import org.pitest.sequence.Context;
import org.pitest.sequence.Match;
import org.pitest.sequence.SlotRead;
import org.pitest.sequence.SlotWrite;

public class InstructionMatchers {
  
  public static Match<AbstractInsnNode> anyInstruction() {
    return Match.always();
  }
  
  public static Match<AbstractInsnNode> opCode(final int opcode) {
    return new Match<AbstractInsnNode>() {
      @Override
      public boolean test(Context<AbstractInsnNode> c, AbstractInsnNode a) {
        return a.getOpcode() == opcode;
      }
    };
  }
  
  public static <T extends AbstractInsnNode> Match<AbstractInsnNode> isA(
      final Class<T> cls) {
    return new Match<AbstractInsnNode>() {
      @Override
      public boolean test(Context<AbstractInsnNode>  c, AbstractInsnNode a) {
        return a.getClass().isAssignableFrom(cls);
      }
    };
  }

  public static <T> Match<T> matchAndStore(final Match<T> target,
      final SlotWrite<T> slot) {
    return new Match<T>() {
      @Override
      public boolean test(Context<T> c, T t) {
        if (target.test(c, t)) {
          c.store(slot, t);
          return true;
        }
        return false;
      }

    };
  }

  public static Match<AbstractInsnNode> increments(final SlotRead<Integer> counterVariable) {
    return new Match<AbstractInsnNode>() {
      @Override
      public boolean test(Context<AbstractInsnNode> context, AbstractInsnNode a) {
        if (a instanceof IincInsnNode) {
          IincInsnNode inc = (IincInsnNode) a;
          return context.retrieve(counterVariable).contains(Prelude.isEqualTo(inc.var));
        } else {
          return false;
        }
      }
    };
  }

  public static Match<AbstractInsnNode> stores(
      final SlotWrite<Integer> counterVariable) {
    return new Match<AbstractInsnNode>() {
      @Override
      public boolean test(Context<AbstractInsnNode> context, AbstractInsnNode a) {
        if (!(a instanceof VarInsnNode)) {
          return false;
        }
        VarInsnNode varNode = (VarInsnNode) a;

        if (a.getOpcode() == Opcodes.ISTORE) {
          context.store(counterVariable, varNode.var);
          return true;
        }
        return false;
      }

    };
  }
  
  public static Match<AbstractInsnNode> storesTo(
      final SlotRead<Integer> counterVariable) {
    return new Match<AbstractInsnNode>() {
      @Override
      public boolean test(Context<AbstractInsnNode> context, AbstractInsnNode a) {
        if (!(a instanceof VarInsnNode)) {
          return false;
        }
        VarInsnNode varNode = (VarInsnNode) a;

        if (a.getOpcode() == Opcodes.ISTORE) {
          context.retrieve(counterVariable).contains(Prelude.isEqualTo(varNode.var));
          return true;
        }
        return false;
      }

    };
  }

  public static Match<AbstractInsnNode> load(
      final SlotRead<Integer> counterVariable) {
    return new Match<AbstractInsnNode>() {
      @Override
      public boolean test(Context<AbstractInsnNode> context, AbstractInsnNode a) {
        if (a.getOpcode() != Opcodes.ILOAD) {
          return false;
        }

        VarInsnNode varNode = (VarInsnNode) a;
        return context.retrieve(counterVariable).contains(Prelude.isEqualTo(varNode.var));
      }

    };
  }

  public static Match<AbstractInsnNode> aGoto() {
    return new Match<AbstractInsnNode>() {
      @Override
      public boolean test(Context<AbstractInsnNode> c, AbstractInsnNode a) {
        return a.getOpcode() == Opcodes.GOTO;
      }
    };
  }
  
  public static Match<AbstractInsnNode> aPush() {
    return opCode(Opcodes.BIPUSH).or(opCode(Opcodes.SIPUSH));
  }
  
  public static Match<AbstractInsnNode> aReturn() {
    return opCode(IRETURN)
        .or(opCode(LRETURN))
        .or(opCode(FRETURN))
        .or(opCode(DRETURN))
        .or(opCode(ARETURN))
        .or(opCode(RETURN));
  }
  
  public static Match<AbstractInsnNode> aJump() {
    return isA(JumpInsnNode.class);
  }
  
  public static Match<AbstractInsnNode> aConditionalJump() {
    // FIXME incomplete
    return opCode(Opcodes.IF_ACMPEQ)
        .or(opCode(Opcodes.IF_ACMPNE))
        .or(opCode(Opcodes.IF_ICMPEQ));
  }
  
  
  public static Match<AbstractInsnNode> jumpsTo(
      final SlotRead<AbstractInsnNode> loopStart) {
    return new Match<AbstractInsnNode>() {
      @Override
      public boolean test(Context<AbstractInsnNode> context, AbstractInsnNode a) {
        if (!(a instanceof JumpInsnNode)) {
          return false;
        }
        JumpInsnNode jump = (JumpInsnNode) a;
        
        return context.retrieve(loopStart).contains(Prelude.<AbstractInsnNode>isEqualTo(jump.label));
      }
    };
  }  
}
