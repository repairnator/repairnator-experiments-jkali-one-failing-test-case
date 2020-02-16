// code by jph
package ch.ethz.idsc.subare.core;

import ch.ethz.idsc.tensor.Tensor;

public interface MoveInterface {
  /** the move function is not necessarily deterministic
   * 
   * @param state
   * @param action
   * @return new state as consequence of given state and action */
  Tensor move(Tensor state, Tensor action);
}
