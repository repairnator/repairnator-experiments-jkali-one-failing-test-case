// code by jph
package ch.ethz.idsc.subare.core.adapter;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class PolynomialBasisTest extends TestCase {
  public void testLo() {
    TensorUnaryOperator tuo = PolynomialBasis.create(4, Clip.function(50, 100));
    assertEquals(tuo.apply(RealScalar.of(50)), Tensors.vector(1, 0, 0, 0));
    assertEquals(tuo.apply(RealScalar.of(100)), Tensors.vector(1, 1, 1, 1));
  }

  public void testFail() {
    TensorUnaryOperator tuo = PolynomialBasis.create(4, Clip.function(50, 100));
    try {
      tuo.apply(RealScalar.ZERO);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
