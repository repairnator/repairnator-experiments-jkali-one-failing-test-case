// code by jph
package ch.ethz.idsc.subare.core.util;

import java.util.HashMap;
import java.util.Map;

import ch.ethz.idsc.subare.core.DiscreteModel;
import ch.ethz.idsc.subare.core.Policy;
import ch.ethz.idsc.subare.core.QsaInterface;
import ch.ethz.idsc.subare.core.StandardModel;
import ch.ethz.idsc.subare.core.VsInterface;
import ch.ethz.idsc.subare.util.FairArgMax;
import ch.ethz.idsc.subare.util.Index;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;

public enum GreedyPolicy {
  ;
  // ---
  /** exact implementation of equiprobable greedy policy:
   * if two or more states s1,s2, ... have equal value
   * v(s1)==v(s2)
   * then they are assigned equal probability
   * 
   * in case there is no unique maximum value
   * there are infinitely many greedy policies
   * and not a unique one policy.
   * 
   * @param standardModel
   * @param vs of standardModel.states()
   * @return */
  public static Policy bestEquiprobable(StandardModel standardModel, VsInterface vs) {
    ActionValueAdapter actionValueAdapter = new ActionValueAdapter(standardModel);
    Map<Tensor, Index> map = new HashMap<>();
    for (Tensor state : standardModel.states()) {
      Tensor actions = standardModel.actions(state);
      Tensor va = Tensor.of(actions.stream() //
          .map(action -> actionValueAdapter.qsa(state, action, vs)));
      FairArgMax fairArgMax = FairArgMax.of(va);
      Tensor feasible = Tensor.of(fairArgMax.options().stream().map(actions::get));
      // Tensor feasible = Extract.of(actions, fairArgMax.options());
      map.put(state, Index.build(feasible));
    }
    return new EGreedyPolicy(map, RealScalar.ZERO, null);
  }

  // this simplicity may be the reason why q(s,a) is preferred over v(s)
  public static Policy bestEquiprobable(DiscreteModel discreteModel, QsaInterface qsa) {
    Map<Tensor, Index> map = new HashMap<>();
    for (Tensor state : discreteModel.states()) {
      Tensor actions = discreteModel.actions(state);
      Tensor va = Tensor.of(actions.stream().map(action -> qsa.value(state, action)));
      FairArgMax fairArgMax = FairArgMax.of(va);
      Tensor feasible = Tensor.of(fairArgMax.options().stream().map(actions::get));
      // Tensor feasible = Extract.of(actions, fairArgMax.options());
      map.put(state, Index.build(feasible));
    }
    return new EGreedyPolicy(map, RealScalar.ZERO, null);
  }
}
