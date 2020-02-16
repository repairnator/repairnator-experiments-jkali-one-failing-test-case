/*
 *  Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and should not be interpreted as representing official policies, either expressed
 *  or implied, of BetaSteward_at_googlemail.com.
 */
package mage.cards.z;

import java.util.UUID;
import mage.abilities.Ability;
import mage.abilities.effects.OneShotEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.SubType;
import mage.constants.Zone;
import mage.filter.FilterPermanent;
import mage.filter.common.FilterCreatureCard;
import mage.filter.predicate.mageobject.SubtypePredicate;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;

/**
 *
 * @author BetaSteward
 */
public class ZombieApocalypse extends CardImpl {

    public ZombieApocalypse(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.SORCERY}, "{3}{B}{B}{B}");

        // Return all Zombie creature cards from your graveyard to the battlefield tapped, then destroy all Humans.
        this.getSpellAbility().addEffect(new ZombieApocalypseEffect());
    }

    public ZombieApocalypse(final ZombieApocalypse card) {
        super(card);
    }

    @Override
    public ZombieApocalypse copy() {
        return new ZombieApocalypse(this);
    }
}

class ZombieApocalypseEffect extends OneShotEffect {

    private static final FilterCreatureCard filterZombie = new FilterCreatureCard();

    static {
        filterZombie.add(new SubtypePredicate(SubType.ZOMBIE));
    }

    public ZombieApocalypseEffect() {
        super(Outcome.PutCreatureInPlay);
        this.staticText = "Return all Zombie creature cards from your graveyard to the battlefield tapped, then destroy all Humans.";
    }

    public ZombieApocalypseEffect(final ZombieApocalypseEffect effect) {
        super(effect);
    }

    @Override
    public ZombieApocalypseEffect copy() {
        return new ZombieApocalypseEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        if (controller != null) {
            controller.moveCards(controller.getGraveyard().getCards(filterZombie, game), Zone.BATTLEFIELD, source, game, true, false, false, null);
            game.applyEffects();
            for (Permanent permanent : game.getBattlefield().getActivePermanents(
                    new FilterPermanent(SubType.HUMAN, "Humans"), source.getControllerId(), game)) {
                permanent.destroy(source.getSourceId(), game, false);
            }
            return true;
        }
        return false;
    }
}
