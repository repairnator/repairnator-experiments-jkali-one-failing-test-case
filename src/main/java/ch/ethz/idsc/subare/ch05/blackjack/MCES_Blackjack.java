// code by jph
package ch.ethz.idsc.subare.ch05.blackjack;

import ch.ethz.idsc.subare.core.Policy;
import ch.ethz.idsc.subare.core.mc.MonteCarloExploringStarts;
import ch.ethz.idsc.subare.core.util.EGreedyPolicy;
import ch.ethz.idsc.subare.core.util.ExploringStarts;
import ch.ethz.idsc.subare.util.UserHome;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.AnimationWriter;

/** Example 5.3 p.108: Solving Blackjack
 * Figure 5.3 p.108
 * 
 * finding optimal policy to stay or hit */
enum MCES_Blackjack {
  ;
  public static void main(String[] args) throws Exception {
    Blackjack blackjack = new Blackjack();
    MonteCarloExploringStarts mces = new MonteCarloExploringStarts(blackjack);
    AnimationWriter gsw = AnimationWriter.of(UserHome.Pictures("blackjack_mces.gif"), 250);
    int batches = 10; // 40
    Tensor epsilon = Subdivide.of(.2, .05, batches);
    int episodes = 0;
    for (int index = 0; index < batches; ++index) {
      System.out.println(index + " " + epsilon.Get(index));
      for (int count = 0; count < batches; ++count) {
        Policy policy = EGreedyPolicy.bestEquiprobable(blackjack, mces.qsa(), epsilon.Get(index));
        episodes += //
            ExploringStarts.batchWithReplay(blackjack, policy, mces);
      }
      gsw.append(BlackjackHelper.joinAll(blackjack, mces.qsa()));
      System.out.println(episodes);
    }
    gsw.close();
  }
}
