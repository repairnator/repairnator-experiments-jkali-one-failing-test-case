
package mage.cards.n;

import java.util.UUID;
import mage.MageInt;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.effects.common.continuous.GainAbilityControlledEffect;
import mage.abilities.effects.common.cost.AbilitiesCostReductionControllerEffect;
import mage.abilities.keyword.MonstrosityAbility;
import mage.abilities.keyword.TrampleAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.SubType;
import mage.constants.Duration;
import mage.constants.Zone;
import mage.counters.CounterType;
import mage.filter.common.FilterCreaturePermanent;
import mage.filter.predicate.permanent.CounterPredicate;

/**
 *
 * @author Styxo
 */
public final class NerfHerder extends CardImpl {

    private static final FilterCreaturePermanent filter = new FilterCreaturePermanent("Each creature with a +1/+1 counter on it");

    static {
        filter.add(new CounterPredicate(CounterType.P1P1));
    }

    public NerfHerder(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId,setInfo,new CardType[]{CardType.CREATURE},"{2}{G}");
        this.subtype.add(SubType.HUMAN);
        this.power = new MageInt(2);
        this.toughness = new MageInt(3);

        // Monstrosity abilities you activate cost {1} less to activate.
        this.addAbility(new SimpleStaticAbility(Zone.BATTLEFIELD, new AbilitiesCostReductionControllerEffect(MonstrosityAbility.class, "Monstrosity")));

        // Each creature you control with a +1/+1 counter on it has trample.
        this.addAbility(new SimpleStaticAbility(Zone.BATTLEFIELD, new GainAbilityControlledEffect(TrampleAbility.getInstance(), Duration.WhileOnBattlefield, filter)));

    }

    public NerfHerder(final NerfHerder card) {
        super(card);
    }

    @Override
    public NerfHerder copy() {
        return new NerfHerder(this);
    }
}
