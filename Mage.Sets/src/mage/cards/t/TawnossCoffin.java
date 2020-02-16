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
package mage.cards.t;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import mage.abilities.Ability;
import mage.abilities.common.LeavesBattlefieldTriggeredAbility;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.common.SkipUntapOptionalAbility;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.Effect;
import mage.abilities.effects.OneShotEffect;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.SubType;
import mage.constants.Zone;
import mage.counters.Counter;
import mage.counters.Counters;
import mage.filter.Filter;
import mage.filter.common.FilterEnchantmentPermanent;
import mage.filter.predicate.mageobject.SubtypePredicate;
import mage.game.ExileZone;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.Target;
import mage.target.common.TargetCreaturePermanent;
import mage.util.CardUtil;

/**
 *
 * @author MarcoMarin
 */
public class TawnossCoffin extends CardImpl {

    public TawnossCoffin(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ARTIFACT}, "{4}");

        // You may choose not to untap Tawnos's Coffin during your untap step.
        this.addAbility(new SkipUntapOptionalAbility());
        // {3}, {T}: Exile target creature and all Auras attached to it. Note the number and kind of counters that were on that creature.
        Ability ability = new SimpleActivatedAbility(Zone.BATTLEFIELD, new TawnossCoffinEffect(), new TapSourceCost());
        ability.addCost(new ManaCostsImpl("{3}"));
        ability.addTarget(new TargetCreaturePermanent());
        this.addAbility(ability);
        //When Tawnos's Coffin leaves the battlefield or becomes untapped, return the exiled card to the battlefield under its owner's control tapped with the noted number and kind of counters on it, and if you do, return the exiled Aura cards to the battlefield under their owner's control attached to that permanent.
        Ability ability3 = new TawnossCoffinTriggeredAbility(new TawnossCoffinReturnEffect(), false);
        this.addAbility(ability3);
    }

    public TawnossCoffin(final TawnossCoffin card) {
        super(card);
    }

    @Override
    public TawnossCoffin copy() {
        return new TawnossCoffin(this);
    }
}

class TawnossCoffinTriggeredAbility extends LeavesBattlefieldTriggeredAbility {

    public TawnossCoffinTriggeredAbility(Effect effect, boolean isOptional) {
        super(effect, isOptional);
    }

    public TawnossCoffinTriggeredAbility(final TawnossCoffinTriggeredAbility ability) {
        super(ability);
    }

    @Override
    public TawnossCoffinTriggeredAbility copy() {
        return new TawnossCoffinTriggeredAbility(this);
    }

    @Override
    public boolean checkEventType(GameEvent event, Game game) {
        return super.checkEventType(event, game) || event.getType() == GameEvent.EventType.UNTAPPED;
    }

    @Override
    public boolean checkTrigger(GameEvent event, Game game) {
        if (event.getType() == GameEvent.EventType.UNTAPPED) {
            return event.getTargetId().equals(getSourceId());
        } else {
            return super.checkTrigger(event, game);
        }
    }

    @Override
    public String getRule() {
        return "When {this} leaves the battlefield or becomes untapped, " + super.getRule();
    }
}

class TawnossCoffinEffect extends OneShotEffect {

    private static final FilterEnchantmentPermanent filter = new FilterEnchantmentPermanent();

    static {
        filter.add(new SubtypePredicate(SubType.AURA));
    }

    public TawnossCoffinEffect() {
        super(Outcome.Detriment);
        this.staticText = "exile target creature and all Auras attached to it. Note the number and kind of counters that were on that creature";
    }

    public TawnossCoffinEffect(final TawnossCoffinEffect effect) {
        super(effect);
    }

    @Override
    public TawnossCoffinEffect copy() {
        return new TawnossCoffinEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        // Exile enchanted creature and all Auras attached to it.
        Permanent sourceObject = game.getPermanentOrLKIBattlefield(source.getSourceId());
        Player controller = game.getPlayer(source.getControllerId());
        if (controller != null && sourceObject != null) {
            Permanent enchantedCreature = game.getPermanent(getTargetPointer().getFirst(game, source));
            if (enchantedCreature != null) {
                UUID exileZoneId = CardUtil.getCardExileZoneId(game, source);
                Set<Card> toExile = new HashSet<>();
                toExile.add(enchantedCreature);
                for (UUID attachementId : enchantedCreature.getAttachments()) {
                    Permanent attachment = game.getPermanent(attachementId);
                    if (attachment != null && attachment.hasSubtype(SubType.AURA, game)) {
                        toExile.add(attachment);
                    }
                }
                controller.moveCardsToExile(toExile, source, game, true, exileZoneId, sourceObject.getIdName());
                game.getState().setValue(exileZoneId.toString() + "NotedCounters", enchantedCreature.getCounters(game).copy());
                game.getState().setValue(exileZoneId.toString() + "EnchantedCreature", enchantedCreature.getId());
            }
            return true;
        }

        return false;
    }
}

class TawnossCoffinReturnEffect extends OneShotEffect {

    public TawnossCoffinReturnEffect() {
        super(Outcome.Benefit);
        this.staticText = "return the exiled card to the battlefield under its owner's control tapped with the noted number and kind of counters on it. If you do, return the exiled Aura cards to the battlefield under their owner's control attached to that permanent";
    }

    public TawnossCoffinReturnEffect(final TawnossCoffinReturnEffect effect) {
        super(effect);
    }

    @Override
    public TawnossCoffinReturnEffect copy() {
        return new TawnossCoffinReturnEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        if (controller == null) {
            return false;
        }
        UUID exileZoneId = CardUtil.getCardExileZoneId(game, source);
        ExileZone exileZone = game.getExile().getExileZone(exileZoneId);
        if (exileZone == null) {
            return true;
        }
        UUID enchantedCreatureId = (UUID) game.getState().getValue(exileZoneId.toString() + "EnchantedCreature");
        if (enchantedCreatureId == null) {
            return false;
        }
        if (!exileZone.contains(enchantedCreatureId)) {
            return true; // Card was removed from exile meanwhile, other card sstay in exile
        }
        Card enchantedCreature = game.getCard(enchantedCreatureId);
        if (enchantedCreature == null) {
            return false;
        }
        controller.moveCards(enchantedCreature, Zone.BATTLEFIELD, source, game, true, false, true, null);
        Permanent newPermanent = game.getPermanent(enchantedCreature.getId());
        if (newPermanent != null) {
            // Add the noted counters
            Counters notedCounters = (Counters) game.getState().getValue(exileZoneId.toString() + "NotedCounters");
            if (notedCounters != null) {
                for (Counter c : notedCounters.values()) { //would be nice if could just use that copy function to set the whole field
                    if (c != null) {
                        newPermanent.getCounters(game).addCounter(c);
                    }
                }
            }
            // Return the exiled auras
            Set<Card> returningAuras = new HashSet<>();
            for (Card enchantment : exileZone.getCards(game)) {
                if (enchantment.hasSubtype(SubType.AURA, game)) {
                    boolean canTarget = false;
                    for (Target target : enchantment.getSpellAbility().getTargets()) {
                        Filter filter2 = target.getFilter();
                        if (filter2.match(newPermanent, game)) {
                            canTarget = true;
                            break;
                        }
                    }
                    if (!canTarget) {
                        // Aura stays exiled
                        continue;
                    }
                    returningAuras.add(enchantment);
                    game.getState().setValue("attachTo:" + enchantment.getId(), newPermanent);
                }
            }
            controller.moveCards(returningAuras, Zone.BATTLEFIELD, source, game, false, false, true, null);
            for (Card enchantment : returningAuras) {
                Permanent permanent = game.getPermanent(enchantment.getId());
                if (permanent != null) {
                    newPermanent.addAttachment(permanent.getId(), game);
                }
            }
        }
        return true;
    }
}
