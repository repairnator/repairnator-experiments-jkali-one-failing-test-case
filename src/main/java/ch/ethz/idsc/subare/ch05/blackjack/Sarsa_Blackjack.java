// code by jph
package ch.ethz.idsc.subare.ch05.blackjack;

import ch.ethz.idsc.subare.core.Policy;
import ch.ethz.idsc.subare.core.td.Sarsa;
import ch.ethz.idsc.subare.core.td.SarsaType;
import ch.ethz.idsc.subare.core.util.DefaultLearningRate;
import ch.ethz.idsc.subare.core.util.DiscreteQsa;
import ch.ethz.idsc.subare.core.util.EGreedyPolicy;
import ch.ethz.idsc.subare.core.util.ExploringStarts;
import ch.ethz.idsc.subare.util.UserHome;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.AnimationWriter;
import ch.ethz.idsc.tensor.sca.Round;

enum Sarsa_Blackjack {
  ;
  static void handle(SarsaType sarsaType) throws Exception {
    System.out.println(sarsaType);
    final Blackjack blackjack = new Blackjack();
    int batches = 40;
    Tensor epsilon = Subdivide.of(.1, .01, batches); // only used in egreedy
    DiscreteQsa qsa = DiscreteQsa.build(blackjack);
    AnimationWriter gsw = AnimationWriter.of(UserHome.Pictures("blackjack_qsa_" + sarsaType + ".gif"), 200);
    Sarsa sarsa = sarsaType.supply(blackjack, qsa, DefaultLearningRate.of(2, 0.6));
    for (int index = 0; index < batches; ++index) {
      // Scalar error = DiscreteQsas.distance(qsa, ref);
      System.out.println(index + " " + epsilon.Get(index).map(Round._2));
      Policy policy = EGreedyPolicy.bestEquiprobable(blackjack, qsa, epsilon.Get(index));
      // sarsa.supplyPolicy(() -> policy);
      sarsa.setExplore(epsilon.Get(index));
      for (int count = 0; count < 10; ++count)
        ExploringStarts.batch(blackjack, policy, sarsa);
      gsw.append(BlackjackHelper.joinAll(blackjack, qsa));
    }
    gsw.close();
  }

  public static void main(String[] args) throws Exception {
    handle(SarsaType.QLEARNING);
  }
}
