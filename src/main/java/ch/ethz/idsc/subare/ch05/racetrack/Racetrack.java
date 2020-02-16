// code by jph
package ch.ethz.idsc.subare.ch05.racetrack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.ethz.idsc.subare.core.MonteCarloInterface;
import ch.ethz.idsc.subare.core.adapter.DeterministicStandardModel;
import ch.ethz.idsc.subare.core.util.StateActionMap;
import ch.ethz.idsc.subare.util.Index;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.opt.NearestInterpolation;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Decrement;

/** Exercise 5.8 p.119: Racetrack (programming)
 * Figure 5.6
 * 
 * the book states that the velocity components should be non-negative
 * the track layout however encourages nudging in the negative direction
 * so we make a compromise by using the following integration procedure
 * p' = p + v + a
 * v' = clip(v + a)
 * 
 * References:
 * Barto, Bradtke, and Singh (1995)
 * Gardner (1973) */
class Racetrack extends DeterministicStandardModel implements MonteCarloInterface {
  static final Tensor WHITE = Tensors.vector(255, 255, 255, 255);
  static final Tensor RED = Tensors.vector(255, 0, 0, 255);
  static final Tensor GREEN = Tensors.vector(0, 255, 0, 255);
  static final Tensor BLACK = Tensors.vector(0, 0, 0, 255);
  static final Scalar MINUS_ONE = RealScalar.ONE.negate();
  // ---
  private static final Tensor STATE_COLLISION = Tensors.vector(9999);
  // ---
  private final Clip clipPositionY;
  private final Clip clipSpeed;
  private final Tensor dimensions;
  private final Tensor states = Tensors.empty(); // (px, py, vx, vy)
  final Tensor statesStart = Tensors.empty();
  final Tensor statesTerminal = Tensors.empty();
  final Tensor actions = //
      Tensor.of(Array.of(Tensors::vector, 3, 3).flatten(1)).map(Decrement.ONE).unmodifiable();
  final Index statesIndex;
  final Index statesStartIndex;
  final Index statesTerminalIndex;
  private final StateActionMap stateActionMap;
  private final Interpolation interpolation;
  /** memo map is populated and reused in {@link #move(Tensor, Tensor)} */
  private final Map<Tensor, Boolean> memo_collisions = new HashMap<>();
  private final Tensor image;
  private final Tensor actionsTerminal = Tensors.vector(0); // do nothing

  Racetrack(Tensor image, int maxSpeed) {
    interpolation = NearestInterpolation.of(Transpose.of(image.get(Tensor.ALL, Tensor.ALL, 2)));
    List<Integer> list = Dimensions.of(image);
    dimensions = Tensors.vector(list.get(1), list.get(0)).map(Decrement.ONE);
    clipPositionY = Clip.function(RealScalar.ZERO, dimensions.Get(1));
    clipSpeed = Clip.function(0, maxSpeed);
    for (int y = 0; y < list.get(0); ++y)
      for (int x = 0; x < list.get(1); ++x) {
        final Tensor rgba = image.get(y, x).unmodifiable();
        if (!rgba.equals(WHITE)) {
          final Tensor pstate = Tensors.vector(x, y);
          if (rgba.equals(BLACK))
            for (int vx = 0; vx <= maxSpeed; ++vx)
              for (int vy = 0; vy <= maxSpeed; ++vy)
                if (vx != 0 || vy != 0)
                  states.append(Join.of(pstate, Tensors.vector(vx, vy)));
          // ---
          if (rgba.equals(GREEN)) {
            Tensor state = Join.of(pstate, Tensors.vector(0, 0));
            states.append(state);
            statesStart.append(state);
          }
          // ---
          if (rgba.equals(RED))
            for (int vx = 0; vx <= maxSpeed; ++vx)
              for (int vy = 0; vy <= maxSpeed; ++vy)
                if (vx != 0 || vy != 0) {
                  Tensor state = Join.of(pstate, Tensors.vector(vx, vy));
                  states.append(state);
                  statesTerminal.append(state);
                }
        }
      }
    states.append(STATE_COLLISION);
    statesTerminal.append(STATE_COLLISION);
    // ---
    statesIndex = Index.build(states);
    statesStartIndex = Index.build(statesStart);
    statesTerminalIndex = Index.build(statesTerminal);
    stateActionMap = StateActionMap.build(this, actions, this);
    this.image = image;
  }

  @Override
  public Tensor states() {
    return states.unmodifiable();
  }

  @Override
  public Tensor actions(Tensor state) {
    return isTerminal(state) ? actionsTerminal : stateActionMap.actions(state);
  }

  private static Tensor shift(Tensor state, Tensor action) {
    Tensor pos = state.extract(0, 2);
    Tensor vel = state.extract(2, 4);
    vel = vel.add(action);
    return Join.of(pos.add(vel), vel);
  }

  private Tensor integrate(Tensor state, Tensor action) {
    Tensor next = shift(state, action); // add velocity
    next.set(clipPositionY, 1);
    next.set(clipSpeed, 2); // vx
    next.set(clipSpeed, 3); // vy
    return next;
  }

  @Override
  public Scalar gamma() {
    return RealScalar.ONE;
  }

  /**************************************************/
  @Override
  public Tensor move(Tensor state, Tensor action) {
    if (isTerminal(state))
      return state;
    Tensor next = integrate(state, action); // vy
    if (statesIndex.containsKey(next)) { // proper move
      Tensor pos0 = state.extract(0, 2);
      Tensor pos1 = next.extract(0, 2);
      Tensor key = Tensors.of(pos0, pos1);
      if (!memo_collisions.containsKey(key)) {
        boolean value = true;
        for (Tensor _lambda : Subdivide.of(0, 1, 5)) {
          Scalar lambda = _lambda.Get();
          Scalar scalar = interpolation.Get( //
              pos0.multiply(lambda).add(pos1.multiply(RealScalar.ONE.subtract(lambda))));
          value &= Scalars.isZero(scalar);
        }
        memo_collisions.put(key, value);
      }
      if (memo_collisions.get(key))
        return STATE_COLLISION;
      return next;
    }
    return STATE_COLLISION;
  }

  @Override
  public Scalar reward(Tensor state, Tensor action, Tensor next) {
    if (!isTerminal(state) && isTerminal(next))
      if (next.equals(STATE_COLLISION))
        return RealScalar.of(-10); // cost of collision, required for value iteration
    if (isTerminal(next))
      return RealScalar.ZERO;
    // if (integrate(state, action).equals(next))
    return MINUS_ONE;
  }

  boolean isStart(Tensor state) {
    return statesStartIndex.containsKey(state);
  }

  /**************************************************/
  @Override // from MonteCarloInterface
  public Tensor startStates() {
    return statesStart;
  }

  @Override // from TerminalInterface
  public boolean isTerminal(Tensor state) {
    return statesTerminalIndex.containsKey(state);
  }

  /**************************************************/
  public Tensor image() {
    return image.copy();
  }
}
