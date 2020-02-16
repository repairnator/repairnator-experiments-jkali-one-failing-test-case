// code by jph
package ch.ethz.idsc.subare.ch00.fish;

import ch.ethz.idsc.subare.core.alg.Random1StepTabularQPlanning;
import ch.ethz.idsc.subare.core.util.ConstantLearningRate;
import ch.ethz.idsc.subare.core.util.DiscreteQsa;
import ch.ethz.idsc.subare.core.util.DiscreteUtils;
import ch.ethz.idsc.subare.core.util.Infoline;
import ch.ethz.idsc.subare.core.util.TabularSteps;
import ch.ethz.idsc.subare.core.util.gfx.StateRasters;
import ch.ethz.idsc.subare.util.UserHome;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.io.AnimationWriter;
import ch.ethz.idsc.tensor.sca.Round;

enum RSTQP_Fishfarm {
  ;
  public static void main(String[] args) throws Exception {
    Fishfarm fishfarm = new Fishfarm(20, 20);
    FishfarmRaster cliffwalkRaster = new FishfarmRaster(fishfarm);
    final DiscreteQsa ref = FishfarmHelper.getOptimalQsa(fishfarm);
    DiscreteQsa qsa = DiscreteQsa.build(fishfarm, DoubleScalar.POSITIVE_INFINITY);
    Random1StepTabularQPlanning rstqp = new Random1StepTabularQPlanning( //
        fishfarm, qsa, ConstantLearningRate.one());
    AnimationWriter gsw = AnimationWriter.of(UserHome.Pictures("fishfarm_qsa_rstqp.gif"), 200);
    int batches = 20;
    for (int index = 0; index < batches; ++index) {
      Infoline infoline = Infoline.print(fishfarm, index, ref, qsa);
      TabularSteps.batch(fishfarm, fishfarm, rstqp);
      gsw.append(StateRasters.qsaLossRef(cliffwalkRaster, qsa, ref));
      if (infoline.isLossfree())
        break;
    }
    gsw.close();
    DiscreteUtils.print(qsa, Round._2);
  }
}
