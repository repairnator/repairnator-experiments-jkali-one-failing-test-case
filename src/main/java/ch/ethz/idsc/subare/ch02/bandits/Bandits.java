// code by jph
package ch.ethz.idsc.subare.ch02.bandits;

import ch.ethz.idsc.subare.util.GlobalAssert;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Sort;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.red.StandardDeviation;
import ch.ethz.idsc.tensor.red.Variance;
import ch.ethz.idsc.tensor.sca.Chop;

/** implementation corresponds to Figure 2.1, p. 30 */
class Bandits {
  private static final Distribution STANDARD = NormalDistribution.standard();
  // ---
  private final Tensor prep;
  private Tensor states;

  Bandits(int n) {
    Tensor data = RandomVariate.of(STANDARD, n);
    Scalar mean = (Scalar) Mean.of(data);
    Tensor temp = data.map(x -> x.subtract(mean)).unmodifiable();
    prep = temp.divide(StandardDeviation.ofVector(temp));
    GlobalAssert.that(Chop._10.allZero(Mean.of(prep)));
    GlobalAssert.that(Chop._10.close(Variance.ofVector(prep), RealScalar.ONE));
  }

  Scalar min = RealScalar.ZERO;
  Scalar max = RealScalar.ZERO;

  void pullAll() {
    states = prep.add(RandomVariate.of(STANDARD, prep.length()));
    Tensor sorted = Sort.of(states);
    min = min.add(sorted.Get(0));
    max = max.add(sorted.Get(states.length() - 1));
  }

  Scalar getLever(int k) {
    return states.Get(k);
  }
}
