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
package mage.cards.l;

import java.util.List;
import java.util.UUID;
import mage.abilities.Ability;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.DestroyAllEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.filter.StaticFilters;
import mage.filter.common.FilterCreaturePermanent;
import mage.filter.predicate.Predicates;
import mage.filter.predicate.permanent.PermanentIdPredicate;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.util.RandomUtil;

/**
 *
 * @author TheElk801
 */
public final class LastOneStanding extends CardImpl {

    public LastOneStanding(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.SORCERY}, "{1}{B}{R}");

        // Choose a creature at random, then destroy the rest.
        this.getSpellAbility().addEffect(new LastOneStandingEffect());
    }

    public LastOneStanding(final LastOneStanding card) {
        super(card);
    }

    @Override
    public LastOneStanding copy() {
        return new LastOneStanding(this);
    }
}

class LastOneStandingEffect extends OneShotEffect {

    LastOneStandingEffect() {
        super(Outcome.Benefit);
        this.staticText = "Choose a creature at random, then destroy the rest.";
    }

    LastOneStandingEffect(final LastOneStandingEffect effect) {
        super(effect);
    }

    @Override
    public LastOneStandingEffect copy() {
        return new LastOneStandingEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        List<Permanent> creatureList = game.getBattlefield().getActivePermanents(StaticFilters.FILTER_PERMANENT_CREATURE, source.getControllerId(), game);
        if (creatureList.size() < 2) {
            return true;
        }
        int toSave = RandomUtil.nextInt(creatureList.size());
        FilterCreaturePermanent filter = new FilterCreaturePermanent();
        filter.add(Predicates.not(new PermanentIdPredicate(creatureList.get(toSave).getId())));
        return new DestroyAllEffect(filter).apply(game, source);
    }
}
