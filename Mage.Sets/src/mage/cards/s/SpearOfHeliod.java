
package mage.cards.s;

import java.util.UUID;
import mage.abilities.Ability;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.common.DestroyTargetEffect;
import mage.abilities.effects.common.continuous.BoostControlledEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.*;
import mage.filter.common.FilterCreaturePermanent;
import mage.filter.predicate.permanent.DamagedPlayerThisTurnPredicate;
import mage.target.Target;
import mage.target.common.TargetCreaturePermanent;

/**
 *
 * @author LevelX2
 */
public final class SpearOfHeliod extends CardImpl {

    private static final FilterCreaturePermanent filter = new FilterCreaturePermanent("creature that dealt damage to you this turn");
    static {
        filter.add(new DamagedPlayerThisTurnPredicate(TargetController.YOU));
    }

    public SpearOfHeliod(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId,setInfo,new CardType[]{CardType.ENCHANTMENT,CardType.ARTIFACT},"{1}{W}{W}");
        addSuperType(SuperType.LEGENDARY);


        // Creatures you control get +1/+1.
        this.addAbility(new SimpleStaticAbility(Zone.BATTLEFIELD, new BoostControlledEffect(1,1, Duration.WhileOnBattlefield)));
        // {1}{W}{W}, {T}: Destroy target creature that dealt damage to you this turn.
        Ability ability = new SimpleActivatedAbility(Zone.BATTLEFIELD, new DestroyTargetEffect(), new ManaCostsImpl("{1}{W}{W}"));
        ability.addCost(new TapSourceCost());
        Target target = new TargetCreaturePermanent(filter);
        ability.addTarget(target);
        this.addAbility(ability);

    }

    public SpearOfHeliod(final SpearOfHeliod card) {
        super(card);
    }

    @Override
    public SpearOfHeliod copy() {
        return new SpearOfHeliod(this);
    }
}
