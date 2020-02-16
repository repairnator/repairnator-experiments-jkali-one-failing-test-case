// code by jph
package ch.ethz.idsc.subare.ch05.blackjack;

import ch.ethz.idsc.subare.core.Policy;
import ch.ethz.idsc.subare.core.alg.ActionValueIteration;
import ch.ethz.idsc.subare.core.mc.MonteCarloExploringStarts;
import ch.ethz.idsc.subare.core.util.ActionValueStatistics;
import ch.ethz.idsc.subare.core.util.EGreedyPolicy;
import ch.ethz.idsc.subare.core.util.ExploringStarts;
import ch.ethz.idsc.subare.util.UserHome;
import ch.ethz.idsc.tensor.DecimalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.AnimationWriter;

/** finding optimal policy to stay or hit
 * 
 * Figure 5.3 p.108 */
enum AVS_Blackjack {
  ;
  public static void main(String[] args) throws Exception {
    Blackjack blackjack = new Blackjack();
    MonteCarloExploringStarts mces = new MonteCarloExploringStarts(blackjack);
    AnimationWriter gsw = AnimationWriter.of(UserHome.Pictures("blackjack_avs.gif"), 250);
    int batches = 3; // 40
    Tensor epsilon = Subdivide.of(.2, .05, batches);
    int episodes = 0;
    ActionValueStatistics avs = new ActionValueStatistics(blackjack);
    for (int index = 0; index < batches; ++index) {
      System.out.println(index + " " + epsilon.Get(index));
      for (int count = 0; count < batches; ++count) {
        Policy policy = EGreedyPolicy.bestEquiprobable(blackjack, mces.qsa(), epsilon.Get(index));
        episodes += //
            ExploringStarts.batchWithReplay(blackjack, policy, mces, avs);
      }
      ActionValueIteration avi = ActionValueIteration.of(blackjack, avs);
      avi.untilBelow(DecimalScalar.of(.0001), 3);
      gsw.append( //
          Join.of( //
              BlackjackHelper.joinAll(blackjack, mces.qsa()), //
              BlackjackHelper.joinAll(blackjack, avi.qsa())));
      System.out.println(episodes + " " + avs.coverage());
    }
    gsw.close();
  }
}
