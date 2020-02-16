// code by jph
package ch.ethz.idsc.subare.ch06.windy;

import ch.ethz.idsc.subare.core.alg.ActionValueIterations;
import ch.ethz.idsc.subare.core.util.DiscreteQsa;
import ch.ethz.idsc.tensor.DecimalScalar;

enum WindygridHelper {
  ;
  static DiscreteQsa getOptimalQsa(Windygrid windygrid) {
    return ActionValueIterations.solve(windygrid, DecimalScalar.of(.0001));
  }
}
