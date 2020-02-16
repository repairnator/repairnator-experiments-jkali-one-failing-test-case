// code by jph
package ch.ethz.idsc.subare.core.alg;

import ch.ethz.idsc.subare.core.ActionValueInterface;
import ch.ethz.idsc.subare.core.DiscreteModel;
import ch.ethz.idsc.subare.core.DiscreteVsSupplier;
import ch.ethz.idsc.subare.core.StandardModel;
import ch.ethz.idsc.subare.core.VsInterface;
import ch.ethz.idsc.subare.core.util.ActionValueAdapter;
import ch.ethz.idsc.subare.core.util.DiscreteValueFunctions;
import ch.ethz.idsc.subare.core.util.DiscreteVs;
import ch.ethz.idsc.subare.core.util.GreedyPolicy;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Max;

/** value iteration: "policy evaluation is stopped after just one sweep"
 * eq (3.14) in 3.5, p.46
 * eq (4.10) in 4.4, p.65
 * see box in 4.4, on p.65
 * 
 * approximately equivalent to iterating with {@link GreedyPolicy}
 * 
 * parallel implementation
 * initial values are set to zeros
 * Jacobi style, i.e. updates take effect only in the next iteration */
public class ValueIteration implements DiscreteVsSupplier {
  private final DiscreteModel discreteModel;
  private final ActionValueAdapter actionValueAdapter;
  private final Scalar gamma;
  private DiscreteVs vs_new;
  private DiscreteVs vs_old;
  private int iterations = 0;
  private int alternate = 0;

  /** @param standardModel */
  public ValueIteration(StandardModel standardModel) {
    this(standardModel, standardModel);
  }

  /** @param standardModel */
  public ValueIteration(DiscreteModel discreteModel, ActionValueInterface actionValueInterface) {
    this.discreteModel = discreteModel;
    actionValueAdapter = new ActionValueAdapter(actionValueInterface);
    this.gamma = discreteModel.gamma();
    vs_new = DiscreteVs.build(discreteModel.states());
  }

  /** perform iteration until values don't change more than threshold
   * 
   * @param threshold
   * @return */
  public void untilBelow(Scalar threshold) {
    untilBelow(threshold, Integer.MAX_VALUE);
  }

  public void untilBelow(Scalar threshold, int flips) {
    Scalar past = null;
    final long tic = System.nanoTime();
    while (true) {
      step();
      final Scalar delta = DiscreteValueFunctions.distance(vs_new, vs_old);
      final long toc = System.nanoTime();
      if (3e9 < toc - tic)
        System.out.println(past + " -> " + delta + " " + alternate);
      if (past != null && Scalars.lessThan(past, delta))
        if (flips < ++alternate) {
          System.out.println("give up at " + past + " -> " + delta);
          break;
        }
      past = delta;
      if (Scalars.lessThan(delta, threshold))
        break;
    }
  }

  /** perform one step of the iteration
   * 
   * @return */
  public void step() {
    vs_old = vs_new.copy();
    VsInterface discounted = vs_new.discounted(gamma);
    vs_new = vs_new.create(vs_new.keys().stream() //
        .parallel() //
        .map(state -> jacobiMax(state, discounted)));
    ++iterations;
  }

  private Scalar jacobiMax(Tensor state, VsInterface gvalues) {
    return discreteModel.actions(state).stream() //
        .map(action -> actionValueAdapter.qsa(state, action, gvalues)) //
        .reduce(Max::of).get();
  }

  @Override
  public DiscreteVs vs() {
    return vs_new;
  }

  public int iterations() {
    return iterations;
  }
}
