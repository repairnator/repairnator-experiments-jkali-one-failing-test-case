
package mage.cards.c;

import java.util.UUID;
import mage.abilities.Ability;
import mage.abilities.effects.OneShotEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.Zone;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.target.TargetPermanent;

/**
 *
 * @author jeffwadsworth
 *
 */
public final class ConsignToDream extends CardImpl {

    public ConsignToDream(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId,setInfo,new CardType[]{CardType.INSTANT},"{2}{U}");


        // Return target permanent to its owner's hand. If that permanent is red or green, put it on top of its owner's library instead.
        this.getSpellAbility().addEffect(new ConsignToDreamEffect());
        this.getSpellAbility().addTarget(new TargetPermanent());

    }

    public ConsignToDream(final ConsignToDream card) {
        super(card);
    }

    @Override
    public ConsignToDream copy() {
        return new ConsignToDream(this);
    }
}

class ConsignToDreamEffect extends OneShotEffect {

    public ConsignToDreamEffect() {
        super(Outcome.ReturnToHand);
        this.staticText = "Return target permanent to its owner's hand. If that permanent is red or green, put it on top of its owner's library instead";
    }

    public ConsignToDreamEffect(final ConsignToDreamEffect effect) {
        super(effect);
    }

    @Override
    public ConsignToDreamEffect copy() {
        return new ConsignToDreamEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        boolean applied = false;
        Permanent target = game.getPermanent(source.getFirstTarget());
        if (target != null) {
            if (target.getColor(game).isRed() || target.getColor(game).isGreen()) {
                applied = target.moveToZone(Zone.LIBRARY, source.getSourceId(), game, true);
            } else {
                applied = target.moveToZone(Zone.HAND, source.getSourceId(), game, false);
            }
        }
        return applied;
    }
}