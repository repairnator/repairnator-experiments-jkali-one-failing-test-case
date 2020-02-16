// code by jph
// inspired by Shangtong Zhang
package ch.ethz.idsc.subare.ch08.maze;

import ch.ethz.idsc.subare.core.alg.ActionValueIteration;
import ch.ethz.idsc.subare.core.util.DiscreteQsa;
import ch.ethz.idsc.subare.core.util.Infoline;
import ch.ethz.idsc.subare.core.util.gfx.StateRasters;
import ch.ethz.idsc.subare.util.UserHome;
import ch.ethz.idsc.tensor.io.AnimationWriter;

/** action value iteration for cliff walk */
enum AVH_Dynamaze {
  ;
  public static void create(String name, Dynamaze dynamaze) throws Exception {
    DiscreteQsa est = DynamazeHeuristic.create(dynamaze);
    // est = DiscreteQsa.build(dynamaze);
    ActionValueIteration avi = ActionValueIteration.of(dynamaze, est);
    // ---
    DiscreteQsa ref = DynamazeHelper.getOptimalQsa(dynamaze);
    AnimationWriter gsw = AnimationWriter.of(UserHome.Pictures(name + "_qsa_avi.gif"), 500);
    DynamazeRaster dynamazeRaster = new DynamazeRaster(dynamaze);
    for (int index = 0; index < 50; ++index) {
      Infoline infoline = Infoline.print(dynamaze, index, ref, avi.qsa());
      gsw.append(StateRasters.qsaLossRef(dynamazeRaster, avi.qsa(), ref));
      avi.step();
      if (infoline.isLossfree())
        break;
    }
    gsw.close();
  }

  public static void main(String[] args) throws Exception {
    // create("maze2", DynamazeHelper.original("maze2"));
    create("maze5", DynamazeHelper.create5(2));
  }
}
