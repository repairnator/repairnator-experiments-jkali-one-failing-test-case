// code by jph
package ch.ethz.idsc.subare.core.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ch.ethz.idsc.subare.core.Policy;
import ch.ethz.idsc.subare.core.StateActionModel;
import ch.ethz.idsc.subare.util.Index;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** the term "equiprobable" appears in Exercise 4.1 */
public class EquiprobablePolicy implements Policy {
  /** @param stateActionModel
   * @return */
  public static Policy create(StateActionModel stateActionModel) {
    return new EquiprobablePolicy(stateActionModel);
  }

  private final StateActionModel stateActionModel;
  private final Map<Tensor, Index> map = new HashMap<>();

  private EquiprobablePolicy(StateActionModel stateActionModel) {
    this.stateActionModel = stateActionModel;
  }

  @Override
  public synchronized Scalar probability(Tensor state, Tensor action) {
    Index index = map.get(state);
    if (Objects.isNull(index)) {
      index = Index.build(stateActionModel.actions(state));
      map.put(state, index);
    }
    if (!index.containsKey(action)) // alternatively return 0
      throw new RuntimeException("action invalid " + action);
    return RationalScalar.of(1, index.size());
  }
}
