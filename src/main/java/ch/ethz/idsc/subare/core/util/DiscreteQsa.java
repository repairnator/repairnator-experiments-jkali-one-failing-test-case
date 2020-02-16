// code by jph
package ch.ethz.idsc.subare.core.util;

import java.io.Serializable;
import java.util.stream.Stream;

import ch.ethz.idsc.subare.core.QsaInterface;
import ch.ethz.idsc.subare.core.StateActionModel;
import ch.ethz.idsc.subare.util.Index;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Min;

public class DiscreteQsa implements QsaInterface, DiscreteValueFunction, Serializable {
  /** @param stateActionModel
   * @return qsa with q(s,a) == 0 for all state-action pairs */
  public static DiscreteQsa build(StateActionModel stateActionModel) {
    Index index = DiscreteUtils.build(stateActionModel, stateActionModel.states());
    return new DiscreteQsa(index, Array.zeros(index.size()));
  }

  public static DiscreteQsa build(StateActionModel stateActionModel, Scalar init) {
    // TODO check for terminal states... should all have value == 0
    Index index = DiscreteUtils.build(stateActionModel, stateActionModel.states());
    return new DiscreteQsa(index, Array.of(list -> init, index.size()));
  }

  // ---
  private final Index index;
  private final Tensor values;

  private DiscreteQsa(Index index, Tensor values) {
    if (index.size() != values.length())
      throw new RuntimeException();
    this.index = index;
    this.values = values;
  }

  @Override
  public Scalar value(Tensor state, Tensor action) {
    return values.Get(index.of(StateAction.key(state, action)));
  }

  @Override
  public void assign(Tensor state, Tensor action, Scalar value) {
    values.set(value, index.of(StateAction.key(state, action)));
  }

  @Override
  public DiscreteQsa copy() {
    return new DiscreteQsa(index, values.copy());
  }

  /**************************************************/
  @Override
  public Tensor keys() {
    return index.keys();
  }

  @Override
  public Tensor values() {
    return values.unmodifiable();
  }

  @Override
  public DiscreteQsa create(Stream<? extends Tensor> stream) {
    return new DiscreteQsa(index, Tensor.of(stream));
  }

  /**************************************************/
  public Scalar getMin() {
    return values.flatten(-1).reduce(Min::of).get().Get();
  }

  public Scalar getMax() {
    return values.flatten(-1).reduce(Max::of).get().Get();
  }

  public int size() {
    return index.size();
  }
}
