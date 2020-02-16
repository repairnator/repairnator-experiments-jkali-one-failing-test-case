package org.pitest.mutationtest.build.intercept.timeout;

import static org.pitest.bytecode.analysis.InstructionMatchers.aConditionalJump;
import static org.pitest.bytecode.analysis.InstructionMatchers.aJump;
import static org.pitest.bytecode.analysis.InstructionMatchers.aPush;
import static org.pitest.bytecode.analysis.InstructionMatchers.aReturn;
import static org.pitest.bytecode.analysis.InstructionMatchers.anyInstruction;
import static org.pitest.bytecode.analysis.InstructionMatchers.increments;
import static org.pitest.bytecode.analysis.InstructionMatchers.isA;
import static org.pitest.bytecode.analysis.InstructionMatchers.jumpsTo;
import static org.pitest.bytecode.analysis.InstructionMatchers.load;
import static org.pitest.bytecode.analysis.InstructionMatchers.matchAndStore;
import static org.pitest.bytecode.analysis.InstructionMatchers.stores;
import static org.pitest.bytecode.analysis.InstructionMatchers.storesTo;
import static org.pitest.bytecode.analysis.MethodMatchers.forLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.pitest.bytecode.analysis.ClassTree;
import org.pitest.bytecode.analysis.MethodTree;
import org.pitest.functional.F;
import org.pitest.functional.FCollection;
import org.pitest.functional.Option;
import org.pitest.mutationtest.build.InterceptorType;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.engine.Location;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.sequence.Match;
import org.pitest.sequence.QueryStart;
import org.pitest.sequence.SequenceMatcher;
import org.pitest.sequence.SequenceQuery;
import org.pitest.sequence.Slot;

/**
 * Removes mutants that will create infinite loops.
 * 
 * Types of infinite loop detected
 *   For loops without increments
 *   While loops with check on counter without increments
 *   Iterator loops without call to iterator.next (NOT yet implemented)
 *   For loops with conditional hard coded to true (NOT yet implemented)
 *   While loop with conditional hard coded to true (NOT yet implemented)
 *
 * Check is not designed to catch all possible infinite loops. Aim is to improve
 * performance by reducing number of mutants that timeout (costing about 4 seconds).
 */
public class SimpleInfiniteLoopInterceptor implements MutationInterceptor {

  private static final Match<AbstractInsnNode> IGNORE = isA(LineNumberNode.class).or(isA(FrameNode.class));
  
  private static final Slot<AbstractInsnNode> LOOP_START       = Slot.create(AbstractInsnNode.class);
  private static final Slot<Integer>          COUNTER_VARIABLE = Slot.create(Integer.class);
  
  private static final SequenceQuery<AbstractInsnNode> DOES_NOT_BREAK_LOOP = QueryStart
      .match(storesTo(COUNTER_VARIABLE.read())
          .or(increments(COUNTER_VARIABLE.read()))
          .or(aReturn())
          .negate());
          
  static final SequenceQuery<AbstractInsnNode> INFINITE_LOOP_CONDITIONAL_AT_START = QueryStart
      .any(AbstractInsnNode.class)
      .then(stores(COUNTER_VARIABLE.write()))
      .then(matchAndStore(isA(LabelNode.class), LOOP_START.write()))
      .then(load(COUNTER_VARIABLE.read()))
      .then(aPush())
      .then(aConditionalJump())
      .zeroOrMore(DOES_NOT_BREAK_LOOP)
      .then(jumpsTo(LOOP_START.read()))
      // can't currently deal with loops with conditionals that cause additional jumps back
      .zeroOrMore(QueryStart.match(jumpsTo(LOOP_START.read()).negate()));
  
  static final SequenceQuery<AbstractInsnNode> INFINITE_LOOP_CONDITIONAL_AT_END = QueryStart
      .any(AbstractInsnNode.class)
      .then(stores(COUNTER_VARIABLE.write()))
      .then(isA(LabelNode.class))
      .then(aJump())
      .then(matchAndStore(isA(LabelNode.class), LOOP_START.write()))
      .zeroOrMore(DOES_NOT_BREAK_LOOP)
      .then(load(COUNTER_VARIABLE.read()))
      .then(anyInstruction())
      .then(jumpsTo(LOOP_START.read()))
      // can't currently deal with loops with conditionals that cause additional jumps back
      .zeroOrMore(QueryStart.match(jumpsTo(LOOP_START.read()).negate()));
  
  static final SequenceMatcher<AbstractInsnNode> INFINITE_LOOP = QueryStart
      .match(Match.<AbstractInsnNode>never())
      .or(INFINITE_LOOP_CONDITIONAL_AT_START)
      .or(INFINITE_LOOP_CONDITIONAL_AT_END)
      .compileIgnoring(IGNORE);
      
  private ClassTree currentClass;

  @Override
  public InterceptorType type() {
    return InterceptorType.FILTER;
  }

  @Override
  public void begin(ClassTree clazz) {
    currentClass = clazz;
  }

  @Override
  public Collection<MutationDetails> intercept(
      Collection<MutationDetails> mutations, Mutater m) {
    Map<Location,Collection<MutationDetails>> buckets = FCollection.bucket(mutations, mutationToLocation());
    
    List<MutationDetails> willTimeout = new ArrayList<MutationDetails>();
    for (Entry<Location, Collection<MutationDetails>> each : buckets.entrySet() ) {
      willTimeout.addAll(findTimeoutMutants(each.getKey(), each.getValue(), m));
    }
    mutations.removeAll(willTimeout);
    return mutations;
  }

  private Collection<MutationDetails> findTimeoutMutants(Location location,
      Collection<MutationDetails> mutations, Mutater m) {
    
    MethodTree method = currentClass.methods().findFirst(forLocation(location)).value();
    List<MutationDetails> timeouts = new ArrayList<MutationDetails>();
    for ( MutationDetails each : mutations ) {
      // avoid cost of static analysis by first checking mutant is on
      // on instruction that could affect looping
      if (mutatesAnInc(method, each) && isInfiniteLoop(each,m) ) {
        timeouts.add(each);
      }
    }
    return timeouts;
    
  }

  private boolean isInfiniteLoop(MutationDetails each, Mutater m) {
    ClassTree mutantClass = ClassTree.fromBytes(m.getMutation(each.getId()).getBytes());
    Option<MethodTree> mutantMethod = mutantClass.methods().findFirst(forLocation(each.getId().getLocation()));
    return INFINITE_LOOP.matches(mutantMethod.value().instructions());
  }

  private boolean mutatesAnInc(MethodTree method, MutationDetails each) {
    return method.instructions().get(each.getInstructionIndex()).getOpcode() == Opcodes.IINC;
  }

  private F<MutationDetails, Location> mutationToLocation() {
    return new F<MutationDetails, Location>() {
      @Override
      public Location apply(MutationDetails a) {
        return a.getId().getLocation();
      }
    };
  }

  @Override
  public void end() {
    currentClass = null;
  }

}
