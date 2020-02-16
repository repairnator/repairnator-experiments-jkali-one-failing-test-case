// code by jph
package ch.ethz.idsc.subare.ch04.gambler;

import ch.ethz.idsc.subare.core.alg.Random1StepTabularQPlanning;
import ch.ethz.idsc.subare.core.util.ActionValueStatistics;
import ch.ethz.idsc.subare.core.util.DefaultLearningRate;
import ch.ethz.idsc.subare.core.util.DiscreteQsa;
import ch.ethz.idsc.subare.core.util.DiscreteValueFunctions;
import ch.ethz.idsc.subare.core.util.Infoline;
import ch.ethz.idsc.subare.core.util.StateActionCounter;
import ch.ethz.idsc.subare.core.util.TabularSteps;
import ch.ethz.idsc.subare.core.util.gfx.StateActionRasters;
import ch.ethz.idsc.subare.util.UserHome;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.io.AnimationWriter;

// R1STQP algorithm is not suited for gambler's dilemma
enum RSTQP_Gambler {
  ;
  public static void main(String[] args) throws Exception {
    Gambler gambler = new Gambler(20, RationalScalar.of(4, 10));
    GamblerRaster gamblerRaster = new GamblerRaster(gambler);
    final DiscreteQsa ref = GamblerHelper.getOptimalQsa(gambler);
    DiscreteQsa qsa = DiscreteQsa.build(gambler);
    Random1StepTabularQPlanning rstqp = new Random1StepTabularQPlanning(gambler, qsa, //
        DefaultLearningRate.of(4, 0.71));
    ActionValueStatistics avs = new ActionValueStatistics(gambler);
    StateActionCounter sac = new StateActionCounter(gambler);
    AnimationWriter gsw = AnimationWriter.of(UserHome.Pictures("gambler_qsa_rstqp.gif"), 100);
    AnimationWriter gsc = AnimationWriter.of(UserHome.Pictures("gambler_sac_rstqp.gif"), 200);
    int batches = 200;
    for (int index = 0; index < batches; ++index) {
      Infoline infoline = Infoline.print(gambler, index, ref, qsa);
      TabularSteps.batch(gambler, gambler, rstqp, avs, sac);
      gsw.append(StateActionRasters.qsaPolicyRef(gamblerRaster, qsa, ref));
      gsc.append(StateActionRasters.qsa( //
          gamblerRaster, DiscreteValueFunctions.rescaled(sac.qsa(StateActionCounter.LOGARITHMIC))));
      if (infoline.isLossfree())
        break;
    }
    gsw.close();
    gsc.close();
    // ---
    // ActionValueIteration avi = new ActionValueIteration(gambler, avs);
    // avi.setMachinePrecision();
    // avi.untilBelow(RealScalar.of(.0001));
    // Scalar error = DiscreteValueFunctions.distance(ref, avi.qsa());
    // System.out.println(error);
    // Export.of(UserHome.Pictures("gambler_avs.png"),
    // // GamblerHelper.qsaPolicyRef(gambler, avi.qsa(), ref)
    // StateActionRasters.qsaPolicyRef(new GamblerRaster(gambler), qsa, ref));
  }
}
