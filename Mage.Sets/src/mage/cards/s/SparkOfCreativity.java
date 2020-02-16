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
package mage.cards.s;

import java.util.UUID;
import mage.MageObject;
import mage.MageObjectReference;
import mage.abilities.Ability;
import mage.abilities.effects.AsThoughEffectImpl;
import mage.abilities.effects.OneShotEffect;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.AsThoughEffectType;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.Zone;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.common.TargetCreaturePermanent;

/**
 *
 * @author LevelX2
 */
public final class SparkOfCreativity extends CardImpl {

    public SparkOfCreativity(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId,setInfo,new CardType[]{CardType.SORCERY},"{R}");

        // Choose target creature. Exile the top card of your library. You may have Spark of Creativity deal damage to that creature equal to the converted mana cost of the exiled card. If you don't, you may play that card until end of turn.
        this.getSpellAbility().addEffect(new SparkOfCreativityEffect());
        this.getSpellAbility().addTarget(new TargetCreaturePermanent());
    }

    public SparkOfCreativity(final SparkOfCreativity card) {
        super(card);
    }

    @Override
    public SparkOfCreativity copy() {
        return new SparkOfCreativity(this);
    }
}

class SparkOfCreativityEffect extends OneShotEffect {

    public SparkOfCreativityEffect() {
        super(Outcome.Benefit);
        this.staticText = "Choose target creature. Exile the top card of your library. You may have Spark of Creativity deal damage to that creature equal to the converted mana cost of the exiled card. If you don't, you may play that card until end of turn";
    }

    public SparkOfCreativityEffect(final SparkOfCreativityEffect effect) {
        super(effect);
    }

    @Override
    public SparkOfCreativityEffect copy() {
        return new SparkOfCreativityEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        MageObject sourceObject = source.getSourceObject(game);
        if (controller != null && sourceObject != null) {
            Card card = controller.getLibrary().getFromTop(game);
            if (card != null) {
                controller.moveCards(card, Zone.EXILED, source, game);
                // You may have Spark of Creativity deal damage to that creature equal to the converted mana cost of the exiled card.
                Permanent targetCreature = game.getPermanent(getTargetPointer().getFirst(game, source));
                if (targetCreature != null) {
                    int cmc = card.getManaCost().convertedManaCost();
                    if (controller.chooseUse(outcome, "Let " + sourceObject.getLogName() + " deal " + cmc + " damage to " + targetCreature.getLogName() + '?', source, game)) {
                        targetCreature.damage(cmc, source.getSourceId(), game, false, true);
                        return true;
                    }
                }
                // If you don't, you may play that card until end of turn
                game.addEffect(new SparkOfCreativityPlayEffect(new MageObjectReference(card, game)), source);

            }
            return true;
        }
        return false;
    }
}

class SparkOfCreativityPlayEffect extends AsThoughEffectImpl {

    private final MageObjectReference objectReference;

    public SparkOfCreativityPlayEffect(MageObjectReference objectReference) {
        super(AsThoughEffectType.PLAY_FROM_NOT_OWN_HAND_ZONE, Duration.EndOfTurn, Outcome.Benefit);
        this.objectReference = objectReference;
        staticText = "you may play that card until end of turn";
    }

    public SparkOfCreativityPlayEffect(final SparkOfCreativityPlayEffect effect) {
        super(effect);
        this.objectReference = effect.objectReference;
    }

    @Override
    public boolean apply(Game game, Ability source) {
        return true;
    }

    @Override
    public SparkOfCreativityPlayEffect copy() {
        return new SparkOfCreativityPlayEffect(this);
    }

    @Override
    public boolean applies(UUID objectId, Ability source, UUID affectedControllerId, Game game) {
        if (objectReference.refersTo(objectId, game) && affectedControllerId.equals(source.getControllerId())) {
            Player controller = game.getPlayer(source.getControllerId());
            if (controller != null) {
                return true;
            } else {
                discard();
            }
        }
        return false;
    }

}
