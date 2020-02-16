// code by jph
package ch.ethz.idsc.subare.ch00.bus;

import ch.ethz.idsc.tensor.Scalar;

interface TripProfile {
  int length();

  Scalar costPerUnit(int time);

  Scalar unitsDrawn(int time);
}
